package com.example.data

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


@RunWith(AndroidJUnit4::class)
class CurrencyIntegrationTest {

    private val repo = CurrencyConverterRepositoryImpl(
        Retrofit.Builder()
            .baseUrl("https://open.er-api.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CurrencyApi::class.java)
    )

    @Test
    fun realApiWorks() = runBlocking {
        val result = repo.convert(100.0, "USD", "EUR")

        assertTrue(result.isSuccess)
        println(result)
    }
}