package com.example.data

import com.example.domain.interfaces.TranslationRepository
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.tasks.await

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
            android.util.Log.d("TRANSLATE", "Download model $normalizedSourceLanguage -> $normalizedTargetLanguage")

            // ожидание загрузки модели
            cachedTranslator.translator.downloadModelIfNeeded().await()

            android.util.Log.d("TRANSLATE", "Model ready")

            // ожидание перевода
            val result = cachedTranslator.translator.translate(text).await()

            android.util.Log.d("TRANSLATE", "Translation success: $result")

            Result.success(result)

        } catch (e: Exception) {
            android.util.Log.e("TRANSLATE", "Translate failed: ${e.message}", e)
            Result.failure(e)
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
