package com.example.domain

import com.example.domain.interfaces.TranslationRepository
import com.example.domain.usecases.TranslateTextUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TranslateTextUseCaseTest {

    @Test
    fun translate_blankText_returnsFailureWithoutCallingRepository() = runTest {
        val repository = mockk<TranslationRepository>()
        val useCase = TranslateTextUseCase(repository)

        val result = useCase.translate("   ", "en", "ru")

        assertTrue(result.isFailure)
        assertEquals("Text must not be blank.", result.exceptionOrNull()?.message)
        coVerify(exactly = 0) { repository.translate(any(), any(), any()) }
    }

    @Test
    fun translate_validInput_delegatesToRepository() = runTest {
        val repository = mockk<TranslationRepository>()
        coEvery {
            repository.translate("Hello", "en", "ru")
        } returns Result.success("Привет")

        val useCase = TranslateTextUseCase(repository)
        val result = useCase.translate("Hello", "en", "ru")

        assertTrue(result.isSuccess)
        assertEquals("Привет", result.getOrNull())
        coVerify(exactly = 1) { repository.translate("Hello", "en", "ru") }
    }
}
