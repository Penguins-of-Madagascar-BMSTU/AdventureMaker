package com.softcat.adventuremaker.screens.posts

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.domain.interfaces.PostsRepository
import com.example.domain.usecases.LocationUseCase
import com.example.domain.usecases.UserUseCase
import com.google.android.gms.tasks.CancellationTokenSource
import com.softcat.adventuremaker.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class CreatePostViewModel(
    private val postsRepository: PostsRepository,
    private val userUseCase: UserUseCase,
    private val locationUseCase: LocationUseCase,
    private val application: Application
) : AndroidViewModel(application) {

    private val _state = MutableLiveData<CreatePostState>(CreatePostState())
    val state: LiveData<CreatePostState>
        get() = _state

    private val _event = MutableSharedFlow<CreatePostEvent>()
    val event: SharedFlow<CreatePostEvent>
        get() = _event.asSharedFlow()

    private var userLocation: Pair<Double, Double>? = null
    private var locationCollectionJob: Job? = null

    private val locationTokenSource = CancellationTokenSource()

    init {
        startLocationTracking()
    }

    fun startLocationTracking() {
        locationCollectionJob?.cancel()

        locationCollectionJob = viewModelScope.launch {
            locationUseCase.observeUserLocation(locationTokenSource.token)
                .collect { userLocation = it }
        }
    }

    fun updateDescription(newValue: String) {
        _state.value = state.value?.copy(description = newValue)
    }

    fun updateImage(uri: Uri) {
        _state.value = state.value?.copy(imageUri = uri)
    }

    fun updateScore(score: Int?) {
        _state.value = state.value?.copy(score = score)
    }

    fun publishPost() {
        val current = _state.value ?: return
        if (current.imageUri == null) {
            val msg = application.getString(R.string.no_image_selected_error)
            _state.value = current.copy(errorMessage = msg)
            return
        }
        _state.value = current.copy(isLoading = true)

        viewModelScope.launch(Dispatchers.IO) {
            val userId = userUseCase.getLastEnteredUser()?.id
            if (userId == null) {
                val msg = application.getString(R.string.no_user_error)
                _state.postValue(
                    current.copy(errorMessage = msg, isLoading = false)
                )
                return@launch
            }

            val result = postsRepository.publishPost(
                context = application,
                imageUri = current.imageUri,
                userId = userId,
                description = current.description,
                latitude = userLocation?.first?.toFloat() ?: 0f,
                longitude = userLocation?.second?.toFloat() ?: 0f,
                scoreValue = current.score
            )

            withContext(Dispatchers.Main) {
                result.onSuccess {
                    _event.emit(CreatePostEvent.Published)
                }.onFailure {
                    _state.value = current.copy(errorMessage = it.message, isLoading = false)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        locationTokenSource.cancel()
    }
}