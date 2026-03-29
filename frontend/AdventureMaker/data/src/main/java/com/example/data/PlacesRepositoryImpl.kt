package com.example.data

import com.example.data.FirebaseRules.POSTS_STORAGE_NAME
import com.example.data.api.MapsApiService
import com.example.data.api.toEntity
import com.example.domain.entities.City
import com.example.domain.entities.Place
import com.example.domain.entities.Post
import com.example.domain.interfaces.PlacesRepository
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import kotlinx.coroutines.tasks.await
import java.util.Locale
import kotlin.math.sqrt

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
                    locale = getLocale(),
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

    private fun getLocale(): String {
        val locale = "${Locale.getDefault().language}_${Locale.getDefault().country}"
        return if (locale in AVAILABLE_LOCALES)
            locale
        else
            DEFAULT_LOCALE
    }

    companion object {
        private val AVAILABLE_LOCALES = listOf(
            "az_AZ", "ru_AZ", "hy_AM", "ru_AM", "ar_BH", "en_BH", "ru_BY", "ka_GE", "ru_GE", "en_EG",
            "ar_EG", "it_IT", "en_IQ", "ar_IQ", "kk_KZ", "ru_KZ", "en_QA", "ar_QA", "en_CY", "es_CL",
            "ky_KG", "ru_KG", "en_CN", "zh_CN", "ru_CN", "en_KW", "ar_KW", "en_MA", "ar_MA", "mn_MN",
            "en_MN", "en_AE", "ar_AE", "en_OM", "ar_OM", "en_RU", "ar_RU", "es_RU", "it_RU", "ru_RU",
            "uk_RU", "cs_RU", "en_SA", "ar_SA", "ru_TJ", "tg_TJ", "ru_UZ", "uz_UZ", "ru_UA", "uk_UA",
            "cs_CZ"
        )

        private const val DEFAULT_LOCALE = "en_RU"
    }
}