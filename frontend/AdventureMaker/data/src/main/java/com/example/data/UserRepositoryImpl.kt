package com.example.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.data.FirebaseRules.USERS_STORAGE_NAME
import com.example.data.exceptions.UserNotFoundException
import com.example.domain.entities.User
import com.example.domain.interfaces.UserRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import com.example.data.exceptions.DataCorruptedException
import com.google.gson.Gson
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.tasks.await

class UserRepositoryImpl(
    private val dataStore: DataStore<Preferences>
): UserRepository {

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
                name = "\"$name\"",
                avatarUrl = null
            )
            val job = reference.setValue(user)
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

    override suspend fun getLastEnteredUser(): User? {
        val datastoreKey = stringPreferencesKey(USER_KEY)
        val userJson = dataStore.data.firstOrNull()?.get(datastoreKey)
        val user = userJson?.let {
            Gson().fromJson(userJson, User::class.java)
        }
        return user
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

    private suspend fun rememberUser(user: User) {
        dataStore.edit { preferences ->
            val userKey = stringPreferencesKey(USER_KEY)
            val userJson = Gson().toJson(user)
            preferences[userKey] = userJson
        }
    }

    private suspend fun forgetUser() {
        dataStore.edit { preferences ->
            val userKey = stringPreferencesKey(USER_KEY)
            preferences.remove(userKey)
        }
    }

    companion object {
        private const val USER_KEY = "user"
    }
}