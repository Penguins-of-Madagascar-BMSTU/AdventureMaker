package com.example.data

import com.example.data.api.CurrencyApiService
import com.example.data.api.dto.RatesResponse
import com.example.domain.usecases.ConvertCurrencyUseCase
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.reset
import org.mockito.Mockito.`when`

class CurrencyIntegrationTest {

    private val apiService = mock(CurrencyApiService::class.java)
    private val useCase = ConvertCurrencyUseCase(
        CurrencyConverterRepositoryImpl(apiService)
    )

    @After
    fun resetApiService() {
        reset(apiService)
    }

    @Test
    fun simpleApiCheck() = runBlocking {
        `when`(apiService.getRates(anyString())).thenReturn(
            RatesResponse(
                result = "success",
                baseCode = "USD",
                rates = mapOf("USD" to 1.0, "AUD" to 1.4817, "BGN" to 1.7741, "CAD" to 1.3168)
            )
        )
        
        val result = useCase.convert(100.0, "USD", "BGN")

        assertTrue(result.isSuccess)
        assertEquals(result.getOrNull(), 177.41)
    }

    @Test
    fun convertZero() = runBlocking {
        `when`(apiService.getRates(anyString())).thenReturn(
            RatesResponse(
                result = "success",
                baseCode = "RUB",
                rates = mapOf("RUB" to 1.0, "AUD" to 0.0, "BGN" to 0.0, "EUR" to 0.0, "USD" to 0.0)
            )
        )

        val result = useCase.convert(0.0, "RUB", "EUR")

        assertTrue(result.isSuccess)
        assertEquals(result.getOrNull(), 0.0)
    }
}