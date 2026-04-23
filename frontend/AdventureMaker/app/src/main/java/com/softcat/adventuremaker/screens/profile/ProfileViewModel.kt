package com.softcat.adventuremaker.screens.profile

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.usecases.PostsUseCase
import com.example.domain.usecases.UserUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileViewModel(
    private val userUseCase: UserUseCase,
    private val postsUseCase: PostsUseCase
) : ViewModel() {

    private val _state = MutableLiveData<ProfileState>(ProfileState.Loading)
    val state: LiveData<ProfileState>
        get() = _state

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch(Dispatchers.IO) {
            val user = userUseCase.getLastEnteredUser()
            if (user == null) {
                withContext(Dispatchers.Main) {
                    _state.value = ProfileState.NoUser
                }
                return@launch
            }

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
                _state.value = ProfileState.NoUser
            }
        }
    }

    fun onAvatarSelected(uri: Uri) {
        // TODO
    }
}
