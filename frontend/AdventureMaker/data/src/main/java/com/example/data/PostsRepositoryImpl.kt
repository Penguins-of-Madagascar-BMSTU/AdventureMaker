package com.example.data

import android.content.Context
import android.net.Uri
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.ObjectMetadata
import com.example.data.FirebaseRules.POSTS_STORAGE_NAME
import com.example.data.api.dto.PostDto
import com.example.data.api.toDto
import com.example.data.api.toEntity
import com.example.domain.entities.Post
import com.example.domain.interfaces.PostsRepository
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.util.UUID

class PostsRepositoryImpl: PostsRepository {

    override fun getPosts(
        userLat: Float,
        userLon: Float
    ): StateFlow<List<Post>> {
        userLatitude = userLat
        userLongitude = userLon
        return postListFlow
            .mergeWith(updatedPostListFlow)
            .stateIn(
                scope = coroutineScope,
                started = SharingStarted.Lazily,
                initialValue = loadedPosts
            )
    }

    override suspend fun loadMorePosts() {
        loadPostsRequest.emit(Unit)
    }

    override suspend fun publishPost(
        context: Context,
        imageUri: Uri,
        userId: String,
        description: String,
        latitude: Float,
        longitude: Float,
        scoreValue: Int?
    ): Result<Post> {
        val contentSize = getFileSize(context, imageUri)
        val inputStream = context.contentResolver.openInputStream(imageUri)
            ?: return Result.failure(Exception("Cannot open input stream."))

        val result = inputStream.use {
            try {
                val postId = UUID.randomUUID().toString()
                val url = uploadImageToS3(inputStream, contentSize, postId)
                val post = Post(
                    id = postId,
                    userId = userId,
                    imageUrl = url,
                    scoreValue = scoreValue,
                    description = description,
                    latitude = latitude,
                    longitude = longitude
                )
                savePostData(post.toDto()).onFailure {
                    deletePost(postId)
                    return@use Result.failure(it)
                }
                return@use Result.success(post)
            } catch (e: Exception) {
                return@use Result.failure(e)
            }
        }
        return result
    }

    override suspend fun deletePost(postId: String): Result<Unit> {
        return try {
            val query = postsStorage.orderByChild("id").equalTo(postId)
            val result = query.get().await()
            if (result.exists()) {
                deleteImageFromS3(postId)
                val job = result.ref.removeValue()
                job.await()
            }
            loadedPosts.removeIf { it.id == postId }
            updatedPostListFlow.emit(loadedPosts)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private var userLatitude: Float = 0f
    private var userLongitude: Float = 0f

    private val loadedPosts = mutableListOf<Post>()

    private val loadPostsRequest = MutableSharedFlow<Unit>(replay = 1)
    private val updatedPostListFlow = MutableSharedFlow<List<Post>>()
    private val postListFlow = flow {
        loadPostsRequest.emit(Unit)
        loadPostsRequest.collect {
            loadPosts()
            emit(loadedPosts.toList())
        }
    }

    private val credentials = BasicAWSCredentials(
        BuildConfig.S3_ACCESS_KEY,
        BuildConfig.S3_SECRET_KEY
    )
    private val s3Client = AmazonS3Client(credentials).apply {
        setEndpoint(BuildConfig.S3_ENDPOINT)
    }

    private val postsStorage by lazy {
        Firebase.database.getReference(POSTS_STORAGE_NAME)
    }

    private var lastKey: String? = null

    private suspend fun loadPosts() {
        try {
            val query = postsStorage.apply {
                orderByKey()
                lastKey?.let { startAt(it) }
                limitToFirst(5)
            }
            val dataSnapshot = query.get().await()
            val newPosts = dataSnapshot.children.mapNotNull { postData ->
                lastKey = postData.key
                postData.getValue<PostDto>()
            }.map { it.toEntity() }
            loadedPosts.addAll(newPosts)
        } catch (_: Exception) {} // если подгрузка следующей порции постов не сработала, то ждём следующего запроса на порцию постов
    }

    private suspend fun savePostData(post: PostDto): Result<Unit> {
        val reference = postsStorage.push()
        val job = reference.setValue(post)
        job.await()
        job.exception?.let { return Result.failure(it) }
        return Result.success(Unit)
    }

    private suspend fun uploadImageToS3(inputStream: InputStream, contentSize: Long, postId: String): String = withContext(Dispatchers.IO) {
        val fileName = getImageFileName(postId)
        val metadata = ObjectMetadata().apply {
            contentType = "image/jpeg"
            contentLength = contentSize
        }
        s3Client.putObject(
            BuildConfig.BUCKET_NAME,
            fileName,
            inputStream,
            metadata
        )
        return@withContext "${BuildConfig.S3_ENDPOINT}/${BuildConfig.BUCKET_NAME}/$fileName"
    }

    suspend fun deleteImageFromS3(postId: String) = withContext(Dispatchers.IO) {
        val fileName = getImageFileName(postId)
        s3Client.deleteObject(
            BuildConfig.BUCKET_NAME,
            fileName
        )
    }

    private fun getImageFileName(postId: String) = "images/$postId.jpg"

    private fun <T> Flow<T>.mergeWith(other: Flow<T>): Flow<T> {
        return merge(this, other)
    }

    private fun getFileSize(context: Context, uri: Uri): Long {
        context.contentResolver.openFileDescriptor(uri, "r").use { descriptor ->
            descriptor?.statSize?.let { return it }
        }
        return 0L
    }
}