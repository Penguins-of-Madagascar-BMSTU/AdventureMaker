package com.example.data

import com.example.domain.interfaces.TranslationRepository

class TranslationRepositoryImpl : TranslationRepository {

    override suspend fun translate(
        text: String,
        sourceLanguage: String,
        targetLanguage: String
    ): Result<String> {
        if (sourceLanguage.equals(targetLanguage, ignoreCase = true)) {
            return Result.success(text)
        }

        val translation = translations[
            TranslationKey(
                sourceLanguage = sourceLanguage.lowercase(),
                targetLanguage = targetLanguage.lowercase(),
                text = text.trim().lowercase()
            )
        ] ?: text

        return Result.success(translation)
    }

    private data class TranslationKey(
        val sourceLanguage: String,
        val targetLanguage: String,
        val text: String
    )

    companion object {
        private val translations = mapOf(
            TranslationKey("ru", "en", "привет") to "hello",
            TranslationKey("ru", "en", "спасибо") to "thank you",
            TranslationKey("en", "ru", "hello") to "привет",
            TranslationKey("en", "ru", "thank you") to "спасибо"
        )
    }
}
