package com.example.domain.interfaces

import com.example.domain.entities.City
import com.example.domain.entities.Place

interface PlacesRepository {

    suspend fun getPlaces(
        cityId: Int,
        query: String,
        category: Place.Category?,
        page: Int
    ): Result<List<Place>>

    suspend fun getCities(query: String): Result<List<City>>
}