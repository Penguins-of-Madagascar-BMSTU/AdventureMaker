package com.example.data

import com.example.domain.interfaces.TranslationRepository
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import java.util.concurrent.ConcurrentHashMap

class TranslationRepositoryImpl : TranslationRepository {

    private val translators = ConcurrentHashMap<LanguagePair, CachedTranslator>()

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

        val languagePair = LanguagePair(
            sourceLanguage = normalizedSourceLanguage,
            targetLanguage = normalizedTargetLanguage
        )
        val cachedTranslator = translators.computeIfAbsent(languagePair) {
            CachedTranslator(createTranslator(it))
        }

        return try {
            cachedTranslator.downloadModelIfNeeded()
            Result.success(Tasks.await(cachedTranslator.translator.translate(text)))
        } catch (exception: InterruptedException) {
            Thread.currentThread().interrupt()
            Result.failure(exception)
        } catch (exception: Exception) {
            Result.failure(exception.cause ?: exception)
        }
    }

    private fun createTranslator(languagePair: LanguagePair): Translator {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(languagePair.sourceLanguage)
            .setTargetLanguage(languagePair.targetLanguage)
            .build()
        return Translation.getClient(options)
    }

    private fun String.toTranslateLanguage(): String? {
        val languageTag = trim()
            .replace('_', '-')
            .lowercase()
        return TranslateLanguage.fromLanguageTag(languageTag)
    }

    private data class LanguagePair(
        val sourceLanguage: String,
        val targetLanguage: String
    )

    private class CachedTranslator(
        val translator: Translator
    ) {
        @Volatile
        private var isModelDownloaded = false
        private val modelDownloadLock = Any()

        fun downloadModelIfNeeded() {
            if (isModelDownloaded) return

            synchronized(modelDownloadLock) {
                if (isModelDownloaded) return
                Tasks.await(translator.downloadModelIfNeeded())
                isModelDownloaded = true
            }
        }
    }
}
