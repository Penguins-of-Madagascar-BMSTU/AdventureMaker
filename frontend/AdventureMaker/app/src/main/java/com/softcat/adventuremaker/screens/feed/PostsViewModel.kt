package com.softcat.adventuremaker.screens.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.usecases.PostsUseCase
import com.example.domain.usecases.UserUseCase
import com.softcat.adventuremaker.designElements.mappers.PostModelMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PostsViewModel(
    private val postsUseCase: PostsUseCase,
    private val userUseCase: UserUseCase,
    private val mapper: PostModelMapper
) : ViewModel() {

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
                    val postModels = mapper.mapToPostsModels(posts)
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

    private fun checkUserAuthorization() {
        viewModelScope.launch(Dispatchers.IO) {
            val id = userUseCase.getLastEnteredUser()?.id
            withContext(Dispatchers.Main) {
                _state.value = state.value?.copy(userId = id)
            }
        }
    }
}