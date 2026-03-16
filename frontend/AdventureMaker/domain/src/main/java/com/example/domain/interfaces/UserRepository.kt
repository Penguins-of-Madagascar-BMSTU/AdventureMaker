package com.example.domain.interfaces

import com.example.domain.entities.User

interface UserRepository {

    suspend fun createUser(name: String, email: String, password: String): Result<User>

    suspend fun enter(email: String, password: String): Result<User>

    suspend fun exit()
}