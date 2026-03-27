package com.example.data

import com.example.domain.interfaces.TranslationRepository
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions

class TranslationRepositoryImpl : TranslationRepository {

    override suspend fun translate(
        text: String,
        sourceLanguage: String,
        targetLanguage: String
    ): Result<String> {
        val normalizedSourceLanguage = sourceLanguage.toTranslateLanguage()
            ?: return Result.failure(
                IllegalArgumentException("Unsupported source language: $sourceLanguage")
            )
        val normalizedTargetLanguage = targetLanguage.toTranslateLanguage()
            ?: return Result.failure(
                IllegalArgumentException("Unsupported target language: $targetLanguage")
            )

        if (normalizedSourceLanguage == normalizedTargetLanguage) {
            return Result.success(text)
        }

        val options = TranslatorOptions.Builder()
            .setSourceLanguage(normalizedSourceLanguage)
            .setTargetLanguage(normalizedTargetLanguage)
            .build()
        val translator = Translation.getClient(options)

        return try {
            Tasks.await(translator.downloadModelIfNeeded())
            Result.success(Tasks.await(translator.translate(text)))
        } catch (exception: InterruptedException) {
            Thread.currentThread().interrupt()
            Result.failure(exception)
        } catch (exception: Exception) {
            Result.failure(exception.cause ?: exception)
        } finally {
            translator.close()
        }
    }

    private fun String.toTranslateLanguage(): String? {
        val languageTag = trim()
            .replace('_', '-')
            .lowercase()
        return TranslateLanguage.fromLanguageTag(languageTag)
    }
}
