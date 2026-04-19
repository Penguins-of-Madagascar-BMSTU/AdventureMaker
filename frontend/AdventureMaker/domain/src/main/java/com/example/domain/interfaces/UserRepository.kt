package com.example.domain.interfaces

import com.example.domain.entities.User
import kotlinx.coroutines.flow.Flow

/**
 * Хранение и загрузка данных пользователя и сессии.
 */
interface UserRepository {

    suspend fun createUser(name: String, email: String, password: String): Result<User>

    suspend fun enter(email: String, password: String): Result<User>

    suspend fun exit()

    suspend fun getLastEnteredUser(): User?

    fun observeLastEnteredUser(): Flow<User?>
}