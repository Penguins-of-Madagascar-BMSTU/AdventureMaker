package com.softcat.adventuremaker.screens.feed

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Application
import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.domain.usecases.PostsUseCase
import com.example.domain.usecases.UserUseCase
import com.google.android.gms.location.LocationServices
import com.softcat.adventuremaker.designElements.mappers.PostModelMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PostsViewModel(
    private val postsUseCase: PostsUseCase,
    private val userUseCase: UserUseCase,
    private val mapper: PostModelMapper,
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

    @RequiresPermission(allOf = [ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION])
    fun startLoading(hasLocationPermission: Boolean) {
        _state.value = state.value?.copy(isLoading = true)
        if (hasLocationPermission) {
            getCurrentLocation(application, ::loadPostsForLocation)
        } else {
            loadPostsForLocation(DEFAULT_LAT, DEFAULT_LON)
        }
    }

    fun loadMore() {
        viewModelScope.launch {
            val hasNext = postsUseCase.loadMorePosts()
            _state.value = state.value?.copy(hasNext = hasNext)
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

    @RequiresPermission(allOf = [ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION])
    fun getCurrentLocation(
        context: Context,
        onResult: (Double, Double) -> Unit
    ) {
        val client = LocationServices.getFusedLocationProviderClient(context)
        val defaultAction = { onResult(DEFAULT_LAT, DEFAULT_LON) }
        client.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                onResult(location.latitude, location.longitude)
            } else {
                defaultAction()
            }
        }
        .addOnFailureListener { defaultAction() }
        .addOnCanceledListener { defaultAction() }
    }

    private fun loadPostsForLocation(userLat: Double, userLon: Double) {
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

    companion object {
        private const val DEFAULT_LAT = 55.7558
        private const val DEFAULT_LON = 37.6173
    }
}