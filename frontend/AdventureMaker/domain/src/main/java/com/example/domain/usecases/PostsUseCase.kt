package com.example.domain.usecases

import com.example.domain.entities.Post
import com.example.domain.interfaces.PostsRepository
import kotlinx.coroutines.flow.StateFlow

/** Лента постов рядом с пользователем и посты выбранного пользователя. */
class PostsUseCase(
    private val repository: PostsRepository
) {

    /** Лента постов для координат пользователя (реактивно). */
    fun getPosts(userLat: Float, userLon: Float): StateFlow<List<Post>> {
        return repository.getPosts(userLat, userLon)
    }

    /** Посты одного пользователя. */
    suspend fun getUserPosts(userId: String): Result<List<Post>> {
        return repository.getUserPosts(userId)
    }

    /** Подгрузить следующую страницу ленты для текущих координат. */
    suspend fun loadMorePosts() {
        repository.loadMorePosts()
    }
}