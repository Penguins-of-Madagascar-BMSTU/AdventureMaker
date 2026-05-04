package com.example.domain

import com.example.domain.entities.User
import com.example.domain.interfaces.UserRepository
import com.example.domain.usecases.UserUseCase
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class UserUseCaseTest {

    private val stubUser = User(
        id = "stub-id",
        name = "Test",
        email = "test@example.com",
        avatarUrl = null
    )

    @Test
    fun createUser_whenPasswordsDoNotMatch_returnsFailureWithoutCallingRepository() = runTest {
        val repository = mockk<UserRepository>()
        val useCase = UserUseCase(repository)

        val result = useCase.createUser(
            name = "Test",
            email = "test@example.com",
            password = "secret1",
            repeatedPassword = "secret2"
        )

        assertTrue(result.isFailure)
        coVerify(exactly = 0) { repository.createUser(any(), any(), any()) }
    }

    @Test
    fun createUser_whenPasswordsMatch_delegatesToRepository() = runTest {
        val repository = mockk<UserRepository>()
        coEvery {
            repository.createUser("Test", "test@example.com", "same")
        } returns Result.success(stubUser)

        val useCase = UserUseCase(repository)
        val result = useCase.createUser(
            name = "Test",
            email = "test@example.com",
            password = "same",
            repeatedPassword = "same"
        )

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) {
            repository.createUser("Test", "test@example.com", "same")
        }
    }

    @Test
    fun enter_delegatesToRepository() = runTest {
        val repository = mockk<UserRepository>()
        coEvery { repository.enter("user@test.com", "secret") } returns Result.success(stubUser)

        val useCase = UserUseCase(repository)
        val result = useCase.enter("user@test.com", "secret")

        assertTrue(result.isSuccess)
        assertEquals(stubUser, result.getOrNull())
        coVerify(exactly = 1) { repository.enter("user@test.com", "secret") }
    }

    @Test
    fun exit_callsRepositoryExit() = runTest {
        val repository = mockk<UserRepository>()
        coJustRun { repository.exit() }

        UserUseCase(repository).exit()

        coVerify(exactly = 1) { repository.exit() }
    }

    @Test
    fun getLastEnteredUser_returnsFromRepository() = runTest {
        val repository = mockk<UserRepository>()
        coEvery { repository.getLastEnteredUser() } returns stubUser

        assertEquals(stubUser, UserUseCase(repository).getLastEnteredUser())
    }

    @Test
    fun getLastEnteredUser_whenNoUser_returnsNull() = runTest {
        val repository = mockk<UserRepository>()
        coEvery { repository.getLastEnteredUser() } returns null

        assertNull(UserUseCase(repository).getLastEnteredUser())
    }
}
