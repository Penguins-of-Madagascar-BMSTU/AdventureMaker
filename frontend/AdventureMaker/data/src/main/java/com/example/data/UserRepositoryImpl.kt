package com.example.data

import com.example.data.exceptions.UserNotFoundException
import com.example.domain.entities.User
import com.example.domain.interfaces.UserRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import com.example.data.exceptions.DataCorruptedException
import kotlinx.coroutines.tasks.await

class UserRepositoryImpl: UserRepository {

    private val usersStorage by lazy {
        Firebase.database.getReference(USERS_STORAGE_NAME)
    }

    private val auth by lazy {
        Firebase.auth
    }

    override suspend fun createUser(
        name: String,
        email: String,
        password: String
    ): Result<User> {
        return try {
            val registerJob = auth.createUserWithEmailAndPassword(email, password)
            registerJob.await()
            registerJob.exception?.let { return Result.failure(it) }
            if (registerJob.result.additionalUserInfo?.isNewUser == false) {
                return readUser(email)
            }
            val reference = usersStorage.push() // Создать узел (JSON), соответствующий пользователю.
            val user = User(
                email = email,
                name = "\"$name\"",
                avatarUrl = null
            )
            val job = reference.setValue(user)
            job.await()
            job.exception?.let {
                auth.signOut()
                return Result.failure(it)
            }
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun enter(
        email: String,
        password: String
    ): Result<User> {
        try {
            val verifyJob = auth.signInWithEmailAndPassword(email, password)
            verifyJob.await()
            verifyJob.exception?.let { return Result.failure(it) }
            return readUser(email)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override suspend fun exit() {
        auth.signOut()
    }

    private suspend fun readUser(email: String): Result<User> {
        // Получить пользователей, у которых поле email совпадает со значением переменной email.
        val query = usersStorage.orderByChild("email").equalTo(email)
        val result = query.get().await()

        if (!result.exists()) {
            return Result.failure(UserNotFoundException(email))
        }

        // result.children - массив из 1 JSON пользователя, в котором нужный email.
        val userSnapshot = result.children.first()
        val user = userSnapshot.getValue<User>()
            ?: return Result.failure(
                DataCorruptedException("User data is invalid.")
            )
        return Result.success(user)
    }

    companion object {
        private const val USERS_STORAGE_NAME = "Users"
    }
}