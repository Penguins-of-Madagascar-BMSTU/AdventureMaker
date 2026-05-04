package com.example.domain.interfaces

interface TranslationRepository {

    suspend fun translate(
        text: String,
        sourceLanguage: String,
        targetLanguage: String
    ): Result<String>
}
