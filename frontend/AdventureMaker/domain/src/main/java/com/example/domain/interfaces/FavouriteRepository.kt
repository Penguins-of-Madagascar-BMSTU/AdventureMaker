package com.example.domain.interfaces

import com.example.domain.entities.Place
import kotlinx.coroutines.flow.StateFlow

interface FavouriteRepository {

    suspend fun getFavourites(userId: String): StateFlow<List<Place>>

    suspend fun getFavouriteIds(userId: String): StateFlow<List<String>>

    suspend fun addToFavourite(userId: String, placeId: String)

    suspend fun removeFromFavourites(userId: String, placeId: String)

    suspend fun observeIsFavourite(userId: String, placeId: String): StateFlow<Boolean>
}