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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.tasks.await

class FavouriteRepositoryImpl(
    private val apiService: MapsApiService
): FavouriteRepository {

    private val favouritesStorage by lazy {
        Firebase.database.getReference(FAVOURITE_PLACES_STORAGE_NAME)
    }

    private val loadFavouritesRequest = MutableSharedFlow<Unit>(replay = 1)
    private val favouriteIdsFlow = flow {
        loadFavouritesRequest.emit(Unit)
        loadFavouritesRequest.collect {
            val favouriteIds = loadFavouritePlaceIds()
            emit(favouriteIds)
        }
    }.retry(1) { true }

    override fun getFavourites(userId: String): StateFlow<List<Place>> {
        selectedUserId = userId
        return favouriteIdsFlow.transform { ids ->
            val places = loadFavouritePlaces(ids)
            emit(places)
        }.stateIn(
            scope = CoroutineScope(Dispatchers.IO),
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )
    }

    override fun getFavouriteIds(userId: String) = favouriteIdsFlow.stateIn(
        scope = CoroutineScope(Dispatchers.IO),
        started = SharingStarted.Lazily,
        initialValue = emptyList()
    )

    private var selectedUserId: String? = null

    private suspend fun loadFavouritePlaceIds(): List<String> {
        val userId = selectedUserId ?: return emptyList()
        val userFavouritesRef = favouritesStorage.child(userId)
        val snapshot = userFavouritesRef.get().await()
        if (!snapshot.exists())
            return emptyList()
        val favouriteIds = snapshot.children.mapNotNull {
            it.key.toString()
        }
        return favouriteIds
    }

    private suspend fun loadFavouritePlaces(favouriteIds: List<String>): List<Place> {
        val ids = favouriteIds.joinToString(",")
        val response = apiService.loadPlaceById(ids)
        val places = response.result.items.map { it.toEntity() }
        return places
    }

    override suspend fun addToFavourite(
        userId: String,
        placeId: String
    ) {
        try {
            val favouriteRef = favouritesStorage
                .child(userId)
                .child(placeId)
            favouriteRef.setValue(true).await()
            loadFavouritesRequest.emit(Unit)
            updateIsFavouriteRequest.emit(Unit)
        } catch (_: Exception) {}
    }

    override suspend fun removeFromFavourites(userId: String, placeId: String) {
        try {
            val favouriteRef = favouritesStorage
                .child(userId)
                .child(placeId)
            favouriteRef.removeValue().await()
            loadFavouritesRequest.emit(Unit)
            updateIsFavouriteRequest.emit(Unit)
        } catch (_: Exception) {}
    }

    private var selectedUserAndPlace: Pair<String, String>? = null
    private val updateIsFavouriteRequest = MutableSharedFlow<Unit>(replay = 1)
    private val isFavouriteFlow = flow {
        updateIsFavouriteRequest.emit(Unit)
        updateIsFavouriteRequest.collect {
            emit(loadFavouriteStatus())
        }
    }

    private suspend fun loadFavouriteStatus(): Boolean {
        val (placeId, userId) = selectedUserAndPlace ?: return false
        try {
            val favouriteRef = favouritesStorage
                .child(userId)
                .child(placeId)
            val result = favouriteRef.get().await()
            return result.exists()
        } catch (_: Exception) {
            return false
        }
    }

    override fun observeIsFavourite(
        userId: String,
        placeId: String
    ): StateFlow<Boolean> {
        selectedUserAndPlace = userId to placeId
        return isFavouriteFlow.stateIn(
            scope = CoroutineScope(Dispatchers.IO),
            started = SharingStarted.Lazily,
            initialValue = false
        )
    }
}