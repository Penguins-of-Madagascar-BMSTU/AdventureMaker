package com.example.data

import com.example.domain.entities.Place
import com.example.domain.interfaces.FavouriteRepository
import kotlinx.coroutines.flow.StateFlow

class FavouriteRepositoryImpl: FavouriteRepository {

    override fun getFavourites(userId: String): StateFlow<List<Place>> {
        TODO("Not yet implemented")
    }

    override suspend fun addToFavourite(
        userId: String,
        place: Place
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun removeFromFavourites(userId: String, placeId: String) {
        TODO("Not yet implemented")
    }

    override fun observeIsFavourite(
        userId: String,
        placeId: String
    ): StateFlow<Boolean> {
        TODO("Not yet implemented")
    }
}