package com.softcat.adventuremaker.screens.profile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.entities.User
import com.example.domain.usecases.UserPostsUseCase
import com.example.domain.usecases.UserUseCase
import com.softcat.adventuremaker.designElements.mappers.PostModelMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileViewModel(
    private val user: User,
    private val userUseCase: UserUseCase,
    private val userPostsUseCase: UserPostsUseCase,
    private val mapper: PostModelMapper
) : ViewModel() {

    private val _state = MutableLiveData<ProfileState>(
        ProfileState.Content(user, emptyList())
    )
    val state: LiveData<ProfileState>
        get() = _state

    private val _profileEvent = MutableSharedFlow<ProfileEvent>()
    val profileEvent: SharedFlow<ProfileEvent> = _profileEvent.asSharedFlow()

    init {
        observeUserPosts()
    }

    fun observeUserPosts() {
        viewModelScope.launch(Dispatchers.IO) {
            userPostsUseCase.observe(user.id).collect { posts ->
                val postModels = mapper.mapToPostsModels(posts)
                withContext(Dispatchers.Main) {
                    _state.value = ProfileState.Content(
                        user = user,
                        posts = postModels
                    )
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            userUseCase.exit()

            withContext(Dispatchers.Main) {
                _profileEvent.emit(ProfileEvent.Exited)
            }
        }
    }

    fun selectAvatar(uri: Uri?) {
        val currentState = state.value as? ProfileState.Content ?: return
        uri ?: return

        viewModelScope.launch {
            userUseCase.updateAvatar(user.id, uri, user.avatarUrl).onSuccess { url ->
                _state.postValue(
                    currentState.copy(
                        user = currentState.user.copy(avatarUrl = url)
                    )
                )
            }.onFailure {
                _profileEvent.emit(ProfileEvent.Error(it.message ?: "Unknown error"))
            }
        }
    }

    fun deletePost(postId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            userPostsUseCase.delete(postId)
        }
    }
}
