package com.softcat.adventuremaker.screens.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.usecases.PostsUseCase
import com.softcat.adventuremaker.screens.posts.PostsState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PostsViewModel(
    private val postsUseCase: PostsUseCase
) : ViewModel() {

    private val _state = MutableLiveData<PostsState>(PostsState.Loading)
    val state: LiveData<PostsState>
        get() = _state


    fun loadPosts(userLat: Float, userLon: Float) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val flow = postsUseCase.getPosts(userLat, userLon)

                flow.collect { posts ->

                    withContext(Dispatchers.Main) {
                        _state.value =
                            if (posts.isEmpty())
                                PostsState.Empty
                            else
                                PostsState.Content(posts)
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _state.value = PostsState.Error(e.message ?: "Error")
                }
            }
        }
    }


    fun loadMore() {
        viewModelScope.launch {
            postsUseCase.loadMorePosts()
        }
    }

}