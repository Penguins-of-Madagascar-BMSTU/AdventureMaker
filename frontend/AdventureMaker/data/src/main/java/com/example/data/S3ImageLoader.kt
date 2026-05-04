package com.example.data

import android.content.Context
import android.net.Uri
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.ObjectMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class S3ImageLoader(
    private val context: Context
) {

    private val credentials = BasicAWSCredentials(
        BuildConfig.S3_ACCESS_KEY,
        BuildConfig.S3_SECRET_KEY
    )
    private val s3Client = AmazonS3Client(credentials).apply {
        setEndpoint(BuildConfig.S3_ENDPOINT)
    }

    private fun getFileSize(uri: Uri): Long {
        context.contentResolver.openFileDescriptor(uri, "r").use { descriptor ->
            descriptor?.statSize?.let { return it }
        }
        return 0L
    }

    suspend fun uploadImageToS3(uri: Uri, imageId: String): Result<String> = withContext(Dispatchers.IO) {
        val size = getFileSize(uri)
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: return@withContext Result.failure(Exception("Cannot open input stream."))
        val fileName = getImageFileName(imageId)
        val metadata = ObjectMetadata().apply {
            contentType = "image/jpeg"
            contentLength = size
        }
        inputStream.use {
            s3Client.putObject(
                BuildConfig.BUCKET_NAME,
                fileName,
                inputStream,
                metadata
            )
        }
        val result = "${BuildConfig.S3_ENDPOINT}/${BuildConfig.BUCKET_NAME}/$fileName"
        return@withContext Result.success(result)
    }

    suspend fun deleteImageFromS3(imageId: String) = withContext(Dispatchers.IO) {
        val fileName = getImageFileName(imageId)
        s3Client.deleteObject(
            BuildConfig.BUCKET_NAME,
            fileName
        )
    }

    private fun getImageFileName(postId: String) = "images/$postId.jpg"
}