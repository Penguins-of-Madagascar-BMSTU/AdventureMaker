package com.example.domain.interfaces

import android.content.Context
import android.net.Uri
import com.example.domain.entities.Post
import kotlinx.coroutines.flow.StateFlow

interface UserPostsRepository {

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
    ): Result<Unit>

    suspend fun deletePost(postId: String): Result<Unit>

    suspend fun getUserPosts(userId: String): StateFlow<List<Post>>
}