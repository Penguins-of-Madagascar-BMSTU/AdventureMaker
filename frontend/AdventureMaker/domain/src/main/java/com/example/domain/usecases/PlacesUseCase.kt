package com.example.domain.usecases

import com.example.domain.entities.City
import com.example.domain.entities.Place
import com.example.domain.interfaces.PlacesRepository

class PlacesUseCase(
    private val repository: PlacesRepository
) {
    suspend fun getCities(): Result<List<City>> {
        return repository.getAllCities()
    }

    suspend fun searchPlaces(
        cityId: Int,
        query: String,
        category: Place.Category,
        page: Int
    ): Result<List<Place>> {
        return repository.getPlaces(cityId, query, category, page)
    }
}