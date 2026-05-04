package com.example.data

import android.net.Uri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.data.FirebaseRules.USERS_STORAGE_NAME
import com.example.data.api.dto.UserDto
import com.example.data.api.toDto
import com.example.data.api.toEntity
import com.example.data.exceptions.UserNotFoundException
import com.example.domain.entities.User
import com.example.domain.interfaces.UserRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import com.example.data.exceptions.DataCorruptedException
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.tasks.await
import java.util.UUID

class UserRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
    private val imageLoader: S3ImageLoader
): UserRepository {

    private val loadLastUserRequest = MutableSharedFlow<Unit>(replay = 1)
    private val userFlow = flow {
        loadLastUserRequest.emit(Unit)
        loadLastUserRequest.collect {
            val user = loadLastUser()
            emit(user)
        }
    }.retry(1) { true }

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
                val result = readUser(email)
                result.onSuccess { rememberUser(it) }
                return result
            }
            val reference = usersStorage.push() // Создать узел (JSON), соответствующий пользователю.
            val user = User(
                id = reference.key.toString(),
                email = email,
                name = name,
                avatarUrl = null
            )
            val job = reference.setValue(user.toDto())
            job.await()
            job.exception?.let {
                auth.signOut()
                return Result.failure(it)
            }
            rememberUser(user)
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
            val result = readUser(email)
            result.onSuccess { rememberUser(it) }
            return result
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override suspend fun exit() {
        auth.signOut()
        forgetUser()
    }

    override suspend fun getLastEnteredUser() = loadLastUser()

    override fun observeLastEnteredUser() = userFlow

    override suspend fun updateAvatar(userId: String, uri: Uri, avatarUrl: String?): Result<String> {
        return try {
            avatarUrl?.let { imageLoader.deleteImageFromS3(it) }
            val id = UUID.randomUUID().toString()
            val result = imageLoader.uploadImageToS3(uri, id)
            result.onSuccess {
                usersStorage
                    .child(userId)
                    .child("avatarUrl")
                    .setValue(result.getOrNull() ?: "")
                    .await()
                val updatedUser = getLastEnteredUser()?.copy(avatarUrl = it)
                updatedUser?.let { rememberUser(it) }
            }
            result
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPostsAuthors(ids: List<String>): Result<Map<String, User?>> {
        return try {
            val result = ids.map { id ->
                val result = usersStorage.child(id).get().await()
                id to result.getValue<UserDto>()?.toEntity()
            }.toMap()
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
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
        val user = userSnapshot.getValue<UserDto>()
            ?: return Result.failure(
                DataCorruptedException("User data is invalid.")
            )
        return Result.success(user.toEntity())
    }

    private suspend fun rememberUser(user: User) {
        dataStore.edit { preferences ->
            val userKey = stringPreferencesKey(USER_KEY)
            val userJson = Gson().toJson(user)
            preferences[userKey] = userJson
        }
        loadLastUserRequest.emit(Unit)
    }

    private suspend fun forgetUser() {
        dataStore.edit { preferences ->
            val userKey = stringPreferencesKey(USER_KEY)
            preferences.remove(userKey)
        }
        loadLastUserRequest.emit(Unit)
    }

    private suspend fun loadLastUser(): User? {
        val datastoreKey = stringPreferencesKey(USER_KEY)
        val userJson = dataStore.data.firstOrNull()?.get(datastoreKey)
        val user = userJson?.let {
            Gson().fromJson(userJson, User::class.java)
        }
        return user
    }

    companion object {
        private const val USER_KEY = "user"
    }
}