package com.softcat.adventuremaker.screens.tools

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.usecases.ConvertCurrencyUseCase
import com.example.domain.usecases.GetEmergencyNumbersUseCase
import com.example.domain.usecases.TranslateTextUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ToolsViewModel(
    private val convertCurrencyUseCase: ConvertCurrencyUseCase,
    private val translateTextUseCase: TranslateTextUseCase,
    private val getEmergencyNumbersUseCase: GetEmergencyNumbersUseCase,
) : ViewModel() {

    private val _state = MutableLiveData(
        ToolsState(
            emergencyNumbers = getEmergencyNumbersUseCase.getEmergencyNumbers(),
        ),
    )
    val state: LiveData<ToolsState>
        get() = _state

    private var currencyConversionInFlight: Boolean = false
    private var currencyConversionRequestId: Long = 0
    private var pendingCurrencyFromCode: String? = null
    private var pendingCurrencyAmount: Double? = null
    private var pendingCurrencySnapshot: Map<String, String>? = null
    private var translationInFlight: Boolean = false

    fun openCurrencyConverter() {
        updateState { copy(activeSection = ToolsSection.Currency) }
    }

    fun openTranslation() {
        updateState { copy(activeSection = ToolsSection.Translation) }
    }

    fun openEmergencyNumbers() {
        updateState { copy(activeSection = ToolsSection.Emergency) }
    }

    fun updateCurrencyInput(code: String, value: String) {
        val current = _state.value ?: ToolsState()
        val newAmounts = current.currencyAmounts.toMutableMap()
        newAmounts[code] = value
        updateState { copy(currencyAmounts = newAmounts.toMap()) }

        val normalized = value.replace(',', '.').trim()
        if (normalized.isEmpty()) {
            pendingCurrencyFromCode = null
            pendingCurrencyAmount = null
            pendingCurrencySnapshot = null
            currencyConversionRequestId++
            updateState { copy(currencyAmounts = ToolsCurrencies.emptyAmounts(), currencyConversionInProgress = false) }
            return
        }

        val amount = normalized.toDoubleOrNull()
        if (amount == null || amount < 0) {
            pendingCurrencyFromCode = null
            pendingCurrencyAmount = null
            pendingCurrencySnapshot = null
            currencyConversionRequestId++
            updateState { copy(currencyAmounts = newAmounts.toMap(), currencyConversionInProgress = false) }
            return
        }

        currencyConversionRequestId++
        val requestId = currencyConversionRequestId
        pendingCurrencyFromCode = code
        pendingCurrencyAmount = amount
        val snapshot = newAmounts.toMap()
        pendingCurrencySnapshot = snapshot

        if (!currencyConversionInFlight) {
            startCurrencyConversion(
                fromCode = code,
                amount = amount,
                requestId = requestId,
                baseAmountsSnapshot = snapshot,
            )
        }
    }

    private fun startCurrencyConversion(
        fromCode: String,
        amount: Double,
        requestId: Long,
        baseAmountsSnapshot: Map<String, String>,
    ) {
        currencyConversionInFlight = true
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                updateState { copy(currencyConversionInProgress = true) }
            }

            val others = ToolsCurrencies.codes.filter { it != fromCode }
            val baseAmounts = baseAmountsSnapshot
            val merged = baseAmounts.toMutableMap()

            try {
                val batchResult =
                    convertCurrencyUseCase.convertToTargets(amount, fromCode, others)

                batchResult
                    .onSuccess { convertedByCode ->
                        others.forEach { target ->
                            merged[target] = convertedByCode[target] ?: baseAmounts[target].orEmpty()
                        }
                    }
                    .onFailure {
                        others.forEach { target ->
                            merged[target] = baseAmounts[target].orEmpty()
                        }
                    }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
            }

            withContext(Dispatchers.Main) {
                currencyConversionInFlight = false

                if (currencyConversionRequestId == requestId) {
                    updateState { copy(currencyAmounts = merged.toMap(), currencyConversionInProgress = false) }
                    return@withContext
                }

                updateState { copy(currencyConversionInProgress = false) }

                val pendingCode = pendingCurrencyFromCode
                val pendingAmount = pendingCurrencyAmount
                val pendingSnapshot = pendingCurrencySnapshot
                if (pendingCode != null && pendingAmount != null && pendingSnapshot != null) {
                    val latestRequestId = currencyConversionRequestId
                    if (latestRequestId != requestId) {
                        startCurrencyConversion(
                            fromCode = pendingCode,
                            amount = pendingAmount,
                            requestId = latestRequestId,
                            baseAmountsSnapshot = pendingSnapshot,
                        )
                    }
                }
            }
        }
    }

    private inline fun updateState(transform: ToolsState.() -> ToolsState) {
        val base = _state.value ?: ToolsState()
        _state.value = base.transform()
    }

    fun changeTextToTranslate(newValue: String) {
        updateState {
            copy(translation = translation.copy(sourceText = newValue))
        }
    }

    fun changeSourceLanguage(newValue: String) {
        updateState {
            copy(translation = translation.copy(sourceLanguage = newValue))
        }
    }

    fun changeTargetLanguage(newValue: String) {
        updateState {
            copy(translation = translation.copy(targetLanguage = newValue))
        }
    }

    fun swapTranslationLanguages() {
        updateState {
            val t = translation
            copy(
                translation = t.copy(
                    sourceLanguage = t.targetLanguage,
                    targetLanguage = t.sourceLanguage,
                    sourceText = t.translatedText,
                    translatedText = t.sourceText,
                ),
            )
        }
    }

    fun translateText() {
        val current = _state.value ?: return
        val t = current.translation
        if (t.sourceText.isBlank()) return
        if (current.translationInProgress || translationInFlight) return

        translationInFlight = true

        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                updateState { copy(translationInProgress = true) }
            }

            try {
                val result = translateTextUseCase.translate(
                    text = t.sourceText,
                    sourceLanguage = t.sourceLanguage,
                    targetLanguage = t.targetLanguage,
                )
                withContext(Dispatchers.Main) {
                    result.onSuccess { text ->
                        val latest = _state.value ?: return@onSuccess
                        _state.value = latest.copy(
                            translation = latest.translation.copy(translatedText = text)
                        )
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } finally {
                withContext(Dispatchers.Main) {
                    updateState { copy(translationInProgress = false) }
                    translationInFlight = false
                }
            }
        }
    }
}
