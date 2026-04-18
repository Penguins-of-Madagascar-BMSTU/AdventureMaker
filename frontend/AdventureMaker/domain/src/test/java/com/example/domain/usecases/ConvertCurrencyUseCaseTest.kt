package com.example.domain.usecases

import com.example.domain.interfaces.CurrencyConverterRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ConvertCurrencyUseCaseTest {

    @Test
    fun convert_negativeAmount_returnsFailureWithoutCallingRepository() = runTest {
        val repository = mockk<CurrencyConverterRepository>()
        val useCase = ConvertCurrencyUseCase(repository)

        val result = useCase.convert(-100.0, "USD", "EUR")

        assertTrue(result.isFailure)
        assertEquals("Amount must be non-negative.", result.exceptionOrNull()?.message)
        coVerify(exactly = 0) { repository.convert(any(), any(), any()) }
    }

    @Test
    fun convert_blankCurrency_returnsFailure() = runTest {
        val repository = mockk<CurrencyConverterRepository>()
        val useCase = ConvertCurrencyUseCase(repository)

        val result = useCase.convert(10.0, " ", "EUR")

        assertTrue(result.isFailure)
        assertEquals("Currency code must not be blank.", result.exceptionOrNull()?.message)
        coVerify(exactly = 0) { repository.convert(any(), any(), any()) }
    }

    @Test
    fun convert_validInput_delegatesToRepository() = runTest {
        val repository = mockk<CurrencyConverterRepository>()
        coEvery { repository.convert(100.0, "USD", "EUR") } returns Result.success(92.5)

        val useCase = ConvertCurrencyUseCase(repository)
        val result = useCase.convert(100.0, "USD", "EUR")

        assertTrue(result.isSuccess)
        assertEquals(92.5, result.getOrNull() ?: 0.0, 0.0)
        coVerify(exactly = 1) { repository.convert(100.0, "USD", "EUR") }
    }

    @Test
    fun convertToTargets_blankTargetInList_returnsFailure() = runTest {
        val repository = mockk<CurrencyConverterRepository>()
        val useCase = ConvertCurrencyUseCase(repository)

        val result = useCase.convertToTargets(10.0, "USD", listOf("EUR", " ", "GBP"))

        assertTrue(result.isFailure)
        coVerify(exactly = 0) { repository.convertToTargets(any(), any(), any()) }
    }
}
