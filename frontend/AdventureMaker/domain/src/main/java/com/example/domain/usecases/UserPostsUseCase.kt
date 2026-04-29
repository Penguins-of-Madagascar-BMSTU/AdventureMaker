package com.example.domain.usecases

import android.content.Context
import android.net.Uri
import com.example.domain.entities.Post
import com.example.domain.interfaces.UserPostsRepository
import kotlinx.coroutines.flow.StateFlow


class UserPostsUseCase(
    private val repository: UserPostsRepository
) {

    suspend fun observe(userId: String): StateFlow<List<Post>> {
        return repository.getUserPosts(userId)
    }

    suspend fun delete(postId: String): Result<Unit> {
        return repository.deletePost(postId)
    }

    suspend fun publishPost(
        context: Context,
        imageUri: Uri,
        userId: String,
        description: String,
        latitude: Float,
        longitude: Float,
        scoreValue: Int? = null
    ): Result<Unit> {
        return repository.publishPost(
            context,
            imageUri,
            userId,
            description,
            latitude,
            longitude,
            scoreValue
        )
    }
}