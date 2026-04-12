package com.example.domain.interfaces

import android.content.Context
import android.net.Uri
import com.example.domain.entities.Post
import kotlinx.coroutines.flow.StateFlow

interface PostsRepository {

    // Получить список постов, связанных с местом, наиболее близким к координатам пользователя.
    // Осуществляется подписка на StateFlow, так как посты могут подгружаться.
    suspend fun getPosts(userLat: Float, userLon: Float): StateFlow<List<Post>>

    // Загрузить ещё несколько новых постов для тех же координат пользователя.
    // Нужно для подгрузки следующей порции постов при прокручивании списка с постами.
    suspend fun loadMorePosts()

    // Сохранить пост в удалённой базе данных.
    suspend fun publishPost(
        context: Context,
        imageUri: Uri, // Uri файла с изображением, хранящегося в галерее устройства.

        // Некоторые поля поста.
        userId: String,
        description: String,
        latitude: Float,
        longitude: Float,
        scoreValue: Int? = null
    ): Result<Post>

    suspend fun deletePost(postId: String): Result<Unit>
}