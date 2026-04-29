package com.example.data

import android.content.Context
import android.net.Uri
import com.example.data.FirebaseRules.POSTS_STORAGE_NAME
import com.example.data.api.dto.PostDto
import com.example.data.api.toDto
import com.example.data.api.toEntity
import com.example.domain.entities.Post
import com.example.domain.interfaces.PostsRepository
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class PostsRepositoryImpl(
    private val imageLoader: S3ImageLoader
): PostsRepository {

    override suspend fun getPosts(
        userLat: Float,
        userLon: Float
    ): StateFlow<List<Post>> {
        loadNextPosts()
        postListFlow.value = loadedPosts.toList()
        return postListFlow
    }

    override suspend fun getUserPosts(userId: String): Result<List<Post>> {
        return try {
            val snapshot = postsStorage
                .orderByChild("userId")
                .equalTo(userId)
                .get()
                .await()
            if (!snapshot.exists()) {
                return Result.success(emptyList())
            }
            val posts = snapshot.children.mapNotNull { child ->
                child.getValue<PostDto>()?.toEntity()
            }
            Result.success(posts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun loadMorePosts() {
        loadNextPosts()
        postListFlow.value = loadedPosts.toList()
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
        return try {
            val postId = UUID.randomUUID().toString()
            val url = imageLoader.uploadImageToS3(imageUri, postId).getOrElse {
                return Result.failure(it)
            }
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
                return Result.failure(it)
            }
            Result.success(post)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deletePost(postId: String): Result<Unit> {
        return try {
            val postRef = postsStorage.child(postId)
            val snapshot = postRef.get().await()
            if (snapshot.exists()) {
                imageLoader.deleteImageFromS3(postId)
                postRef.removeValue().await()
            }
            loadedPosts.removeIf { it.id == postId }
            postListFlow.value = loadedPosts.toList()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private val loadedPosts = mutableListOf<Post>()

    private val postListFlow = MutableStateFlow<List<Post>>(emptyList())

    private val postsStorage by lazy {
        Firebase.database.getReference(POSTS_STORAGE_NAME)
    }

    private var lastKey: String? = null

    private suspend fun loadNextPosts() {
        try {
            val query = postsStorage
                .orderByKey()
                .let { q -> lastKey?.let { q.startAfter(it) } ?: q }
                .limitToFirst(5)
            val dataSnapshot = query.get().await()
            val postList = dataSnapshot.children.mapNotNull {
                it.getValue<PostDto>()?.toEntity()
            }
            if (postList.isNotEmpty()) {
                lastKey = dataSnapshot.children.lastOrNull()?.key
                loadedPosts.addAll(postList)
            }
        } catch (_: Exception) {} // если подгрузка следующей порции постов не сработала, то ждём следующего запроса на порцию постов
    }

    private suspend fun savePostData(post: PostDto): Result<Unit> {
        val reference = postsStorage.push()
        val job = reference.setValue(post)
        job.await()
        job.exception?.let { return Result.failure(it) }
        return Result.success(Unit)
    }
}