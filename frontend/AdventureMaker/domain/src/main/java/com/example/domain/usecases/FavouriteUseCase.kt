package com.example.domain.usecases

import android.util.Log
import com.example.domain.entities.Place
import com.example.domain.interfaces.FavouriteRepository
import kotlinx.coroutines.flow.StateFlow

/** Избранные места: список, идентификаторы, добавление/удаление и признак «в избранном». */
class FavouriteUseCase(
    private val repository: FavouriteRepository
) {
    fun getFavourites(userId: String): StateFlow<List<Place>> {
        Log.d("${this::class.simpleName}", "getFavourites($userId)")
        return repository.getFavourites(userId)
    }

    fun getFavouriteIds(userId: String): StateFlow<List<String>> {
        Log.d("${this::class.simpleName}", "getFavourites($userId)")
        return repository.getFavouriteIds(userId)
    }

    suspend fun addToFavourite(userId: String, place: Place) {
        Log.d("${this::class.simpleName}", "addToFavourite($userId, $place)")
        return repository.addToFavourite(userId, place.id)
    }

    suspend fun removeFromFavourites(userId: String, placeId: String) {
        Log.d("${this::class.simpleName}", "removeFromFavourites($userId, $placeId)")
        return repository.removeFromFavourites(userId, placeId)
    }

    fun observeIsFavourite(userId: String, placeId: String): StateFlow<Boolean> {
        Log.d("${this::class.simpleName}", "observeIsFavourite($userId, $placeId)")
        return repository.observeIsFavourite(userId, placeId)
    }
}