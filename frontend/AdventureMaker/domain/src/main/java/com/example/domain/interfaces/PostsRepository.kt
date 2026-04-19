package com.example.domain.interfaces

import android.content.Context
import android.net.Uri
import com.example.domain.entities.Post
import kotlinx.coroutines.flow.StateFlow

/** Доступ к постам в облаке: лента по геолокации, посты пользователя, публикация и удаление. */
interface PostsRepository {

    /**
     * Посты рядом с точкой пользователя; обновления через [StateFlow] при подгрузке порций.
     */
    fun getPosts(userLat: Float, userLon: Float): StateFlow<List<Post>>

    /** Все посты указанного пользователя одним запросом. */
    suspend fun getUserPosts(userId: String): Result<List<Post>>

    /** Следующая порция постов для тех же координат (пагинация при скролле). */
    suspend fun loadMorePosts()

    /**
     * Публикация поста в удалённое хранилище.
     *
     * @param imageUri URI изображения из галереи устройства.
     */
    suspend fun publishPost(
        context: Context,
        imageUri: Uri,
        userId: String,
        description: String,
        latitude: Float,
        longitude: Float,
        scoreValue: Int? = null
    ): Result<Post>

    /** Удаление поста по идентификатору. */
    suspend fun deletePost(postId: String): Result<Unit>
}