package com.softcat.adventuremaker.screens.posts

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.interfaces.PostsRepository
import com.example.domain.usecases.UserUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class CreatePostViewModel(
    private val postsRepository: PostsRepository,
    private val userUseCase: UserUseCase
) : ViewModel() {

    private val _state = MutableLiveData<CreatePostState>(CreatePostState.Editing())
    val state: LiveData<CreatePostState>
        get() = _state

    // обновление текста
    fun updateDescription(newValue: String) {
        val current = _state.value
        if (current is CreatePostState.Editing) {
            _state.value = current.copy(description = newValue)
        }
    }

    // обновление картинки
    fun updateImage(uri: Uri) {
        val current = _state.value
        if (current is CreatePostState.Editing) {
            _state.value = current.copy(imageUri = uri)
        }
    }

    // обновление рейтинга
    fun updateScore(score: Int?) {
        val current = _state.value
        if (current is CreatePostState.Editing) {
            _state.value = current.copy(score = score)
        }
    }

    // публикация поста
    fun publishPost(context: Context, latitude: Float, longitude: Float) {

        val current = _state.value
        if (current !is CreatePostState.Editing) return

        if (current.imageUri == null) {
            _state.value = CreatePostState.Error("No image")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {

            _state.postValue(CreatePostState.Loading)

            val userId = userUseCase.getLastEnteredUser()?.id

            if (userId == null) {
                _state.postValue(CreatePostState.Error("No user"))
                return@launch
            }

            val result = postsRepository.publishPost(
                context = context,
                imageUri = current.imageUri,
                userId = userId,
                description = current.description,
                latitude = latitude,
                longitude = longitude,
                scoreValue = current.score
            )

            withContext(Dispatchers.Main) {
                if (result.isSuccess) {
                    _state.value = CreatePostState.Success
                } else {
                    _state.value = CreatePostState.Error(
                        result.exceptionOrNull()?.message ?: "Error"
                    )
                }
            }
        }
    }
}