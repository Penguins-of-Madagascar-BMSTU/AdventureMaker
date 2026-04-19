package com.softcat.adventuremaker.screens.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.entities.User
import com.example.domain.usecases.PostsUseCase
import com.example.domain.usecases.UserUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileViewModel(
    private val user: User,
    private val userUseCase: UserUseCase,
    private val postsUseCase: PostsUseCase
) : ViewModel() {

    private val _state = MutableLiveData<ProfileState>(
        ProfileState.Content(user, emptyList())
    )
    val state: LiveData<ProfileState>
        get() = _state

    private val _profileEvent = MutableSharedFlow<ProfileEvent>()
    val profileEvent: SharedFlow<ProfileEvent> = _profileEvent.asSharedFlow()

    init {
        loadPosts()
    }

    fun loadPosts() {
        viewModelScope.launch(Dispatchers.IO) {
            val posts = postsUseCase.getUserPosts(user.id).getOrElse { emptyList() }
            withContext(Dispatchers.Main) {
                _state.value = ProfileState.Content(
                    user = user,
                    posts = posts
                )
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
}
