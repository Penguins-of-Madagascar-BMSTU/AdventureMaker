package com.example.domain.usecases

import com.example.domain.entities.Post
import com.example.domain.interfaces.PostsRepository
import kotlinx.coroutines.flow.StateFlow


class PostsUseCase(
    private val repository: PostsRepository
) {

    suspend fun getPosts(userLat: Double, userLon: Double): StateFlow<List<Post>> {
        return repository.getPosts(userLat, userLon)
    }

    suspend fun loadMorePosts(): Boolean {
        return repository.loadMorePosts()
    }
}