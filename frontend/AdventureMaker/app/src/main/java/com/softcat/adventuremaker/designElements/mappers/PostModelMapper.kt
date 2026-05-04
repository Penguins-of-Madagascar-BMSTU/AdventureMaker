package com.softcat.adventuremaker.designElements.mappers

import android.content.Context
import android.location.Geocoder
import com.example.domain.entities.Post
import com.example.domain.usecases.UserUseCase
import com.softcat.adventuremaker.designElements.models.PostModel
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class PostModelMapper(
    private val context: Context,
    private val userUseCase: UserUseCase
) {
    suspend fun mapToPostsModels(posts: List<Post>): List<PostModel> {
        val ids = posts.map { it.userId }
        val authors = userUseCase.getPostsAuthors(ids).getOrElse {
            return posts.map { post ->
                PostModel(
                    id = post.id,
                    authorName = "",
                    authorAvatarUrl = "",
                    address = resolveAddress(post.latitude, post.longitude),
                    imageUrl = post.imageUrl,
                    description = post.description,
                    score = post.scoreValue
                )
            }
        }
        return posts.map { post ->
            val user = authors[post.userId]
            val avatarUrl = user?.avatarUrl ?: ""
            val userName = user?.name ?: ""
            PostModel(
                id = post.id,
                authorName = userName,
                authorAvatarUrl = avatarUrl,
                address = resolveAddress(post.latitude, post.longitude),
                imageUrl = post.imageUrl,
                description = post.description,
                score = post.scoreValue
            )
        }
    }

    private suspend fun resolveAddress(
        latitude: Float,
        longitude: Float
    ): String = suspendCancellableCoroutine { continuation ->
        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            geocoder.getFromLocation(
                latitude.toDouble(),
                longitude.toDouble(),
                1
            ) { addresses ->
                if (continuation.isActive) {
                    val address = addresses.firstOrNull()?.getAddressLine(0) ?: ""
                    continuation.resume(address)
                }
            }
        } catch (e: Exception) {
            if (continuation.isActive) {
                continuation.resumeWithException(e)
            }
        }
    }
}