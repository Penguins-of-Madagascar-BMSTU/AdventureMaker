package com.example.domain.usecases

import com.example.domain.entities.User
import com.example.domain.interfaces.UserRepository
import kotlinx.coroutines.flow.Flow

/**
 * Сценарии работы с текущим пользователем: регистрация, вход, выход и наблюдение за сессией.
 */
class UserUseCase(
    private val repository: UserRepository
) {
    suspend fun createUser(
        name: String,
        email: String,
        password: String,
        repeatedPassword: String
    ): Result<User> {
        if (password != repeatedPassword)
            return Result.failure(Exception("Create user error: Passwords do not match."))
        return repository.createUser(name, email, password)
    }

    suspend fun enter(email: String, password: String): Result<User> {
        return repository.enter(email, password)
    }

    suspend fun exit() {
        return repository.exit()
    }

    suspend fun getLastEnteredUser(): User? {
        return repository.getLastEnteredUser()
    }

    fun observeLastEnteredUser(): Flow<User?> {
        return repository.observeLastEnteredUser()
    }
}