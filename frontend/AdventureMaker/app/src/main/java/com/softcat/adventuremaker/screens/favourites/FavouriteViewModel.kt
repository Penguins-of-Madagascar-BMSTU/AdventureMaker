package com.softcat.adventuremaker.screens.favourites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.entities.FavouriteScreenVariant
import com.example.domain.entities.Place
import com.example.domain.usecases.ExperimentsUseCase
import com.example.domain.usecases.FavouriteUseCase
import com.example.domain.usecases.UserUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavouriteViewModel(
    private val userUseCase: UserUseCase,
    private val favouriteUseCase: FavouriteUseCase,
    private val expUseCase: ExperimentsUseCase
): ViewModel() {

    private val _state = MutableLiveData<FavouriteState>(FavouriteState.Loading)
    val state: LiveData<FavouriteState>
        get() = _state

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val userId = userUseCase.getLastEnteredUser()?.id
            if (userId != null) {
                subscribeFavourites(userId)
            } else {
                withContext(Dispatchers.Main) {
                    _state.value = FavouriteState.NoUser
                }
            }
        }
    }

    fun changeCategoryFilter(newValue: Place.Category) {
        _state.value = state.value.let {
            if (it is FavouriteState.Content)
                it.copy(filterCategory = newValue)
            else
                it
        }
    }

    private suspend fun subscribeFavourites(userId: String) {
        val variant = expUseCase.getFavouriteScreenVariant()
        favouriteUseCase.getFavourites(userId)
            .collect { placeList ->
                withContext(Dispatchers.Main) {
                    _state.value = state.value?.let {
                        if (it is FavouriteState.Content)
                            it.copy(places = placeList)
                        else
                            FavouriteState.Content(Place.Category.Unknown, placeList, variant)
                    }
                }
            }
    }
}