package com.example.data

import android.content.Context
import android.net.Uri
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.ObjectMetadata
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
import java.util.UUID

class PostsRepositoryImpl(
    private val context: Context,
): PostsRepository {

    override suspend fun getPosts(
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
        imageUri: Uri,
        userId: String,
        description: String,
        latitude: Float,
        longitude: Float,
        scoreValue: Int?
    ): Result<Post> {
        return try {
            val postId = UUID.randomUUID().toString()
            val url = uploadImageToS3(imageUri, postId)
            val post = Post(
                id = postId,
                userId = userId,
                imageUrl = url,
                scoreValue = scoreValue,
                description = description,
                latitude = latitude,
                longitude = longitude
            )
            savePostData(post)
            Result.success(post)
        } catch (e: Exception) {
            Result.failure(e)
        }
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
        Firebase.database.getReference(POSTS_STORAGE)
    }

    private var lastKey: String? = null

    private suspend fun loadPosts() {
        val query = postsStorage.apply {
            orderByKey()
            lastKey?.let { startAt(it) }
            limitToFirst(5)
        }
        val dataSnapshot = query.get().await()
        val newPosts = dataSnapshot.children.mapNotNull { postData ->
            lastKey = postData.key
            postData.getValue<Post>()
        }
        loadedPosts.addAll(newPosts)
    }

    private suspend fun savePostData(post: Post) {
        val reference = postsStorage.push()
        val job = reference.setValue(post)
        job.await()
        job.exception?.let { throw it }
    }

    private suspend fun uploadImageToS3(imageUri: Uri, postId: String): String = withContext(Dispatchers.IO) {
        val inputStream = context.contentResolver.openInputStream(imageUri)
        requireNotNull(inputStream) { "Cannot open input stream from URI" }

        val fileName = getImageFileName(postId)
        val metadata = ObjectMetadata().apply {
            contentType = "image/jpeg"
        }
        s3Client.putObject(
            BuildConfig.BUCKET_NAME,
            fileName,
            inputStream,
            metadata
        )
        inputStream.close()

        return@withContext "${BuildConfig.S3_ENDPOINT}/${BuildConfig.BUCKET_NAME}/$fileName"
    }

    suspend fun deleteImageFromS3(postId: String): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            val fileName = getImageFileName(postId)
            s3Client.deleteObject(
                BuildConfig.BUCKET_NAME,
                fileName
            )
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun getImageFileName(postId: String) = "images/$postId.jpg"

    private fun <T> Flow<T>.mergeWith(other: Flow<T>): Flow<T> {
        return merge(this, other)
    }

    companion object {
        private const val POSTS_STORAGE = "Posts"
    }
}