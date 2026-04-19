package com.example.domain.usecases

import android.util.Log
import com.example.domain.entities.Place
import com.example.domain.interfaces.FavouriteRepository
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class FavouriteUseCaseTest {

    @Before
    fun setUp() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
    }

    @After
    fun tearDown() {
        unmockkStatic(Log::class)
    }

    private val stubPlace = Place(
        id = "place-1",
        name = "Museum",
        description = "Desc",
        category = Place.Category.Museum,
        imageUrls = emptyList(),
        latitude = 55f,
        longitude = 37f,
        address = "Address"
    )

    @Test
    fun addToFavourite_passesUserIdAndPlaceIdToRepository() = runTest {
        val repository = mockk<FavouriteRepository>()
        coJustRun { repository.addToFavourite("user-1", "place-1") }

        FavouriteUseCase(repository).addToFavourite("user-1", stubPlace)

        coVerify(exactly = 1) { repository.addToFavourite("user-1", "place-1") }
    }

    @Test
    fun removeFromFavourites_delegatesToRepository() = runTest {
        val repository = mockk<FavouriteRepository>()
        coJustRun { repository.removeFromFavourites("user-1", "place-1") }

        FavouriteUseCase(repository).removeFromFavourites("user-1", "place-1")

        coVerify(exactly = 1) { repository.removeFromFavourites("user-1", "place-1") }
    }
}
