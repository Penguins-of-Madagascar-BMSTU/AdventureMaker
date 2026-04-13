package com.softcat.adventuremaker.screens.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.usecases.UserUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileViewModel(
    private val userUseCase: UserUseCase
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
            withContext(Dispatchers.Main) {
                _state.value = if (user != null) {
                    ProfileState.Content(user)
                } else {
                    ProfileState.NoUser
                }
            }
        }
    }
}
