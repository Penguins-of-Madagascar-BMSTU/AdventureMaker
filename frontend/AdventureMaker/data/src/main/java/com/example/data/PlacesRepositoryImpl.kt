package com.example.data

import com.example.data.api.MapsApiService
import com.example.data.api.toEntity
import com.example.domain.entities.City
import com.example.domain.entities.Place
import com.example.domain.interfaces.PlacesRepository

class PlacesRepositoryImpl(
    private val mapsApiService: MapsApiService,
    private val placeImageProvider: PlaceImageProvider
): PlacesRepository {

    override suspend fun getPlaces(
        cityId: Int,
        query: String,
        category: Place.Category?
    ): Result<List<Place>> {
        val places = mutableListOf<Place>()
        for (i in 1..5) {
            try {
                val placeList = mapsApiService.loadPlaces(
                    query = query,
                    categoryAlias = category.toAlias(),
                    cityId = cityId,
                    page = i
                )
                val newPlaces = placeList.result.items
                    .map { it.toEntity() }
                    .filter { it.category == category }
                places.addAll(newPlaces)
            } catch (e: Exception) {
                if (i == 5 && places.isEmpty())
                    return Result.failure(e)
            }
        }
        val result = placeImageProvider.provideImages(places)
        return Result.success(result)
    }

    override suspend fun getCities(query: String): Result<List<City>> {
        return try {
            val models = mapsApiService.searchCity(query).result.items
            val cities = models.map { it.toEntity() }
            Result.success(cities)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun Place.Category?.toAlias() = when (this) {
        Place.Category.Museum -> "branch"
        Place.Category.Entertainment -> "adm_div.place"
        Place.Category.Bank -> "branch"
        Place.Category.Hotel -> "branch"
        Place.Category.Attraction -> "attraction"
        Place.Category.Restaurant -> "branch"
        else -> "branch"
    }
}