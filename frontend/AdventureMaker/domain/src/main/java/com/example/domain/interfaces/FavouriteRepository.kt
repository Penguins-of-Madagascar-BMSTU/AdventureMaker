package com.example.domain.interfaces

import com.example.domain.entities.Place
import kotlinx.coroutines.flow.StateFlow

/** Хранение избранных мест пользователя и подписка на изменения. */
interface FavouriteRepository {

    /** Полный список избранных [Place] для [userId]. */
    fun getFavourites(userId: String): StateFlow<List<Place>>

    /** Только идентификаторы избранных мест. */
    fun getFavouriteIds(userId: String): StateFlow<List<String>>

    suspend fun addToFavourite(userId: String, placeId: String)

    suspend fun removeFromFavourites(userId: String, placeId: String)

    /** Текущее состояние «место в избранном» для пары пользователь–место. */
    fun observeIsFavourite(userId: String, placeId: String): StateFlow<Boolean>
}