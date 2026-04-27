package com.example.domain.usecases

import com.example.domain.entities.Post
import com.example.domain.interfaces.PostsRepository
import kotlinx.coroutines.flow.StateFlow


class PostsUseCase(
    private val repository: PostsRepository
) {

    suspend fun getPosts(userLat: Float, userLon: Float): StateFlow<List<Post>> {
        return repository.getPosts(userLat, userLon)
    }

    suspend fun getUserPosts(userId: String): Result<List<Post>> {
        return repository.getUserPosts(userId)
    }

    suspend fun loadMorePosts() {
        repository.loadMorePosts()
    }
}