package com.example.data

import com.example.data.FirebaseRules.POSTS_STORAGE_NAME
import com.example.data.api.dto.PostDto
import com.example.data.api.toEntity
import com.example.domain.entities.Post
import com.example.domain.interfaces.PostsRepository
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await

class PostsRepositoryImpl(
    private val imageLoader: S3ImageLoader
): PostsRepository {

    override suspend fun getPosts(
        userLat: Double,
        userLon: Double
    ): StateFlow<List<Post>> {
        postListFlow.value = loadedPosts.toList()
        return postListFlow
    }

    override suspend fun loadMorePosts(): Boolean {
        val hasNext = loadNextPosts()
        postListFlow.value = loadedPosts.toList()
        return hasNext
    }

    private val loadedPosts = mutableListOf<Post>()

    private val postListFlow = MutableStateFlow<List<Post>>(emptyList())

    private val postsStorage by lazy {
        Firebase.database.getReference(POSTS_STORAGE_NAME)
    }

    private var lastKey: String? = null

    private suspend fun loadNextPosts(): Boolean {
        return try {
            val query = postsStorage
                .orderByKey()
                .let { q -> lastKey?.let { q.startAfter(it) } ?: q }
                .limitToFirst(LIMIT)
            val dataSnapshot = query.get().await()
            val postList = dataSnapshot.children.mapNotNull {
                it.getValue<PostDto>()?.toEntity()
            }
            if (postList.isNotEmpty()) {
                lastKey = dataSnapshot.children.lastOrNull()?.key
                loadedPosts.addAll(postList)
            }
            postList.size == LIMIT
        } catch (_: Exception) {
            true
        } // если подгрузка следующей порции постов не сработала, то ждём следующего запроса на порцию постов
    }

    companion object {
        private const val LIMIT = 5
    }
}