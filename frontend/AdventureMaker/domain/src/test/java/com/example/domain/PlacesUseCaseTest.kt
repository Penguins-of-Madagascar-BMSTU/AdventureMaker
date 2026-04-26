package com.example.domain

import com.example.domain.entities.City
import com.example.domain.entities.Place
import com.example.domain.interfaces.PlacesRepository
import com.example.domain.usecases.PlacesUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PlacesUseCaseTest {

    private val cities = listOf(City(id = 1, name = "Москва", latitude = 55.7558, longitude = 37.6173))

    private val places = listOf(
        Place(
            id = "p1",
            name = "Музей",
            description = "",
            category = Place.Category.Museum,
            imageUrls = emptyList(),
            latitude = 0f,
            longitude = 0f,
            address = ""
        )
    )

    @Test
    fun getCities_delegatesToRepository() = runTest {
        val repository = mockk<PlacesRepository>()
        coEvery { repository.getAllCities() } returns Result.success(cities)

        val result = PlacesUseCase(repository).getCities()

        assertTrue(result.isSuccess)
        assertEquals(cities, result.getOrNull())
        coVerify(exactly = 1) { repository.getAllCities() }
    }

    @Test
    fun searchPlaces_delegatesToRepositoryWithSameArguments() = runTest {
        val repository = mockk<PlacesRepository>()
        coEvery {
            repository.getPlaces(1, "музей", Place.Category.Museum, 2)
        } returns Result.success(places)

        val result = PlacesUseCase(repository).searchPlaces(
            cityId = 1,
            query = "музей",
            category = Place.Category.Museum,
            page = 2
        )

        assertTrue(result.isSuccess)
        assertEquals(places, result.getOrNull())
        coVerify(exactly = 1) {
            repository.getPlaces(1, "музей", Place.Category.Museum, 2)
        }
    }
}
