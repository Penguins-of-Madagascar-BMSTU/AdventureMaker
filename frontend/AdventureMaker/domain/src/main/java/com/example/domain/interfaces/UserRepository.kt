package com.example.domain.interfaces

import android.net.Uri
import com.example.domain.entities.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    suspend fun createUser(name: String, email: String, password: String): Result<User>

    suspend fun enter(email: String, password: String): Result<User>

    suspend fun exit()

    suspend fun getLastEnteredUser(): User?

    fun observeLastEnteredUser(): Flow<User?>

    suspend fun updateAvatar(uri: Uri, avatarUrl: String?): Result<String>
}