package com.example.domain.usecases

import android.net.Uri
import com.example.domain.entities.User
import com.example.domain.interfaces.UserRepository
import kotlinx.coroutines.flow.Flow

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
            return Result.failure(Exception("Create user error: passwords do not match."))
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

    suspend fun updateAvatar(userId: String, uri: Uri, avatarUrl: String?): Result<String> {
        return repository.updateAvatar(userId, uri, avatarUrl)
    }

    suspend fun getPostsAuthors(ids: List<String>): Result<Map<String, User?>> {
        return repository.getPostsAuthors(ids)
    }
}