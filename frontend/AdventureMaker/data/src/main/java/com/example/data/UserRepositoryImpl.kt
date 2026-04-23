package com.example.data

import android.content.Context
import android.net.Uri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.ObjectMetadata
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
import kotlinx.coroutines.withContext
import java.util.Locale

class UserRepositoryImpl(
    private val dataStore: DataStore<Preferences>
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

    private val credentials = BasicAWSCredentials(
        BuildConfig.S3_ACCESS_KEY,
        BuildConfig.S3_SECRET_KEY
    )
    private val s3Client = AmazonS3Client(credentials).apply {
        setEndpoint(BuildConfig.S3_ENDPOINT)
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

    override suspend fun uploadAvatar(context: Context, avatarUri: Uri): Result<User> {
        return try {
            val currentUser = loadLastUser()
                ?: return Result.failure(IllegalStateException("No logged in user."))
            saveLocalAvatarUri(currentUser.email, avatarUri.toString())
            val localAvatarUser = currentUser.copy(avatarUrl = avatarUri.toString())
            rememberUser(localAvatarUser)
            loadLastUserRequest.emit(Unit)

            val contentSize = getFileSize(context, avatarUri)
            val inputStream = context.contentResolver.openInputStream(avatarUri)
                ?: return Result.failure(IllegalStateException("Cannot open avatar stream."))

            val avatarUrl = inputStream.use {
                uploadAvatarToS3(it, contentSize, currentUser.id)
            }
            val updatedUser = currentUser.copy(avatarUrl = avatarUrl)
            updateUserInFirebase(updatedUser).onFailure { return Result.failure(it) }
            rememberUser(updatedUser)
            loadLastUserRequest.emit(Unit)
            Result.success(updatedUser)
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
        val normalizedUser = if (user.id.isBlank()) {
            user.copy(id = userSnapshot.key.orEmpty())
        } else {
            user
        }
        val remoteUser = normalizedUser.toEntity()
        val localAvatarUri = loadLocalAvatarUri(remoteUser.email)
        return Result.success(
            remoteUser.copy(avatarUrl = localAvatarUri ?: remoteUser.avatarUrl)
        )
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

    private suspend fun loadLastUser(): User? {
        val datastoreKey = stringPreferencesKey(USER_KEY)
        val userJson = dataStore.data.firstOrNull()?.get(datastoreKey)
        val user = userJson?.let {
            Gson().fromJson(userJson, User::class.java)
        }
        return user
    }

    private suspend fun saveLocalAvatarUri(email: String, avatarUri: String) {
        val avatarKey = stringPreferencesKey(getAvatarKey(email))
        dataStore.edit { preferences ->
            preferences[avatarKey] = avatarUri
        }
    }

    private suspend fun loadLocalAvatarUri(email: String): String? {
        val avatarKey = stringPreferencesKey(getAvatarKey(email))
        return dataStore.data.firstOrNull()?.get(avatarKey)
    }

    private suspend fun uploadAvatarToS3(
        inputStream: java.io.InputStream,
        contentSize: Long,
        userId: String
    ): String = withContext(kotlinx.coroutines.Dispatchers.IO) {
        val fileName = getAvatarFileName(userId)
        val metadata = ObjectMetadata().apply {
            // OpenDocument providers may return unknown size (-1).
            contentType = "image/*"
            if (contentSize > 0L) {
                contentLength = contentSize
            }
        }
        s3Client.putObject(
            BuildConfig.BUCKET_NAME,
            fileName,
            inputStream,
            metadata
        )
        return@withContext "${BuildConfig.S3_ENDPOINT}/${BuildConfig.BUCKET_NAME}/$fileName"
    }

    private suspend fun updateUserInFirebase(user: User): Result<Unit> {
        val snapshotById = usersStorage.orderByChild("id").equalTo(user.id).get().await()
        val userNode = snapshotById.children.firstOrNull() ?: run {
            val snapshotByEmail = usersStorage.orderByChild("email").equalTo(user.email).get().await()
            snapshotByEmail.children.firstOrNull()
        } ?: return Result.failure(UserNotFoundException(user.email))
        val updateJob = userNode.ref.setValue(user.toDto())
        updateJob.await()
        updateJob.exception?.let { return Result.failure(it) }
        return Result.success(Unit)
    }

    private fun getAvatarFileName(userId: String): String {
        return "avatars/${userId.lowercase(Locale.US)}.jpg"
    }

    private fun getAvatarKey(email: String): String {
        return "avatar_uri_${email.lowercase(Locale.US)}"
    }

    private fun getFileSize(context: Context, uri: Uri): Long {
        context.contentResolver.openFileDescriptor(uri, "r").use { descriptor ->
            descriptor?.statSize?.let { return it }
        }
        return 0L
    }

    companion object {
        private const val USER_KEY = "user"
    }
}