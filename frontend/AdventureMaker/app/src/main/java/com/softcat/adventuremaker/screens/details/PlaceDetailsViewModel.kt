package com.softcat.adventuremaker.screens.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.entities.Place
import com.example.domain.usecases.FavouriteUseCase
import com.example.domain.usecases.UserUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlaceDetailsViewModel(
    private val place: Place,
    private val userUseCase: UserUseCase,
    private val favouriteUseCase: FavouriteUseCase
): ViewModel() {

    private val _state = MutableLiveData(PlaceDetailsState(null, place))
    val state: LiveData<PlaceDetailsState>
        get() = _state

    private var savedUserId: String? = null

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val userId = userUseCase.getLastEnteredUser()?.id ?: return@launch
            savedUserId = userId
            favouriteUseCase.observeIsFavourite(userId, place.id).collect {
                _state.postValue(PlaceDetailsState(it, place))
            }
        }
    }

    fun changeFavouriteStatus() {
        val isFavourite = state.value?.isFavourite ?: return
        val userId = savedUserId ?: return
        viewModelScope.launch(Dispatchers.IO) {
            if (isFavourite) {
                favouriteUseCase.removeFromFavourites(userId, place.id)
            } else {
                favouriteUseCase.addToFavourite(userId, place)
            }
        }
    }
}