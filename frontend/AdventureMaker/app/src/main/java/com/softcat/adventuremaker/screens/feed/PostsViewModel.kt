package com.softcat.adventuremaker.screens.feed

import android.app.Application
import android.location.Geocoder
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.domain.entities.Post
import com.example.domain.usecases.PostsUseCase
import com.example.domain.usecases.UserUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class PostsViewModel(
    private val postsUseCase: PostsUseCase,
    private val userUseCase: UserUseCase,
    private val application: Application
) : AndroidViewModel(application) {

    private val _state = MutableLiveData<PostsState>(PostsState(isLoading = true))
    val state: LiveData<PostsState>
        get() = _state

    private val _events = MutableSharedFlow<PostsEvent>()
    val events: SharedFlow<PostsEvent> = _events

    init {
        checkUserAuthorization()
    }

    fun loadPosts(userLat: Float, userLon: Float) {
        _state.value = state.value?.copy(isLoading = true)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                postsUseCase.getPosts(userLat, userLon).collect { posts ->
                    val postModels = createPostsModels(posts)
                    withContext(Dispatchers.Main) {
                        _state.value = state.value?.copy(posts = postModels, isLoading = false)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    val msg = e.message ?: "Unknown error"
                    _events.emit(PostsEvent.Error(msg))
                }
            }
        }
    }

    fun loadMore() {
        viewModelScope.launch {
            postsUseCase.loadMorePosts()
        }
    }

    private suspend fun createPostsModels(posts: List<Post>): List<PostModel> {
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
        val geocoder = Geocoder(application, Locale.getDefault())
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

    private fun checkUserAuthorization() {
        viewModelScope.launch(Dispatchers.IO) {
            val id = userUseCase.getLastEnteredUser()?.id
            withContext(Dispatchers.Main) {
                _state.value = state.value?.copy(userId = id)
            }
        }
    }
}