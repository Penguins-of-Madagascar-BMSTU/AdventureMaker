package com.example.domain.usecases

import com.example.domain.interfaces.TranslationRepository

class TranslateTextUseCase(
    private val repository: TranslationRepository
) {

    suspend fun translate(
        text: String,
        sourceLanguage: String,
        targetLanguage: String
    ): Result<String> {
        if (text.isBlank()) {
            return Result.failure(IllegalArgumentException("Text must not be blank."))
        }
        if (sourceLanguage.isBlank() || targetLanguage.isBlank()) {
            return Result.failure(IllegalArgumentException("Language code must not be blank."))
        }
        return repository.translate(text, sourceLanguage, targetLanguage)
    }
}
