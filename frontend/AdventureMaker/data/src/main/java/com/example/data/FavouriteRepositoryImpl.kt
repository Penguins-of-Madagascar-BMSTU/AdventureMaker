package com.example.data

import com.example.data.FirebaseRules.FAVOURITE_PLACES_STORAGE_NAME
import com.example.data.api.MapsApiService
import com.example.data.api.toEntity
import com.example.domain.entities.Place
import com.example.domain.interfaces.FavouriteRepository
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.tasks.await

class FavouriteRepositoryImpl(
    private val apiService: MapsApiService
): FavouriteRepository {

    private val favouritesStorage by lazy {
        Firebase.database.getReference(FAVOURITE_PLACES_STORAGE_NAME)
    }

    private val _favouriteIdsFlow = MutableStateFlow<List<String>>(emptyList())
    val favouriteIdsFlow: StateFlow<List<String>> = _favouriteIdsFlow

    private val _favouriteStatusFlow = MutableStateFlow(false)
    val favouriteStatusFlow: StateFlow<Boolean> = _favouriteStatusFlow

    override suspend fun getFavourites(userId: String): StateFlow<List<Place>> {
        return favouriteIdsFlow.transform { ids ->
            val places = loadFavouritePlaces(ids)
            emit(places)
        }.stateIn(
            scope = CoroutineScope(Dispatchers.IO),
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )
    }

    override suspend fun getFavouriteIds(userId: String): StateFlow<List<String>> {
        loadFavouritePlaceIds(userId).let { ids ->
            _favouriteIdsFlow.value = ids
        }
        return favouriteIdsFlow
    }

    override suspend fun observeIsFavourite(
        userId: String,
        placeId: String
    ): StateFlow<Boolean> {
        _favouriteStatusFlow.value = loadIsFavourite(userId, placeId)
        return favouriteStatusFlow
    }

    override suspend fun addToFavourite(userId: String, placeId: String) {
        try {
            favouritesStorage
                .child(userId)
                .child(placeId)
                .setValue(true)
                .await()
            updateFavouriteStatus(userId, placeId)
        } catch (_: Exception) {}
    }

    override suspend fun removeFromFavourites(userId: String, placeId: String) {
        try {
            favouritesStorage
                .child(userId)
                .child(placeId)
                .removeValue()
                .await()
            updateFavouriteStatus(userId, placeId)
        } catch (_: Exception) {}
    }

    private suspend fun loadFavouritePlaceIds(userId: String): List<String> {
        val userFavouritesRef = favouritesStorage.child(userId)
        val snapshot = userFavouritesRef.get().await()
        return if (snapshot.exists()) {
            snapshot.children.mapNotNull { it.key }
        } else emptyList()
    }

    private suspend fun loadIsFavourite(userId: String, placeId: String): Boolean {
        val isFavouriteRef = favouritesStorage
            .child(userId)
            .child(placeId)
        val snapshot = isFavouriteRef.get().await()
        return snapshot.exists()
    }

    private suspend fun loadFavouritePlaces(favouriteIds: List<String>): List<Place> {
        try {
            val ids = favouriteIds.joinToString(",")
            val response = apiService.loadPlaceById(ids)
            val places = response.result.items.map { it.toEntity() }
            return places
        } catch (_: Exception) {
            return emptyList()
        }
    }

    private suspend fun updateFavouriteStatus(userId: String, placeId: String) {
        val favourites = loadFavouritePlaceIds(userId)
        _favouriteIdsFlow.value = favourites
        _favouriteStatusFlow.value = placeId in favourites
    }
}