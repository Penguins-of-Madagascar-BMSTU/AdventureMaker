package com.softcat.adventuremaker.screens.tools

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.usecases.ConvertCurrencyUseCase
import com.example.domain.usecases.GetEmergencyNumbersUseCase
import com.example.domain.usecases.GetUsefulPhrasesUseCase
import com.example.domain.usecases.TranslateTextUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ToolsViewModel(
    private val convertCurrencyUseCase: ConvertCurrencyUseCase,
    private val translateTextUseCase: TranslateTextUseCase,
    private val getEmergencyNumbersUseCase: GetEmergencyNumbersUseCase,
    private val getUsefulPhrasesUseCase: GetUsefulPhrasesUseCase,
) : ViewModel() {

    private val _state = MutableLiveData(
        ToolsState(
            emergencyNumbers = getEmergencyNumbersUseCase.getEmergencyNumbers(),
        ),
    )
    val state: LiveData<ToolsState>
        get() = _state

    private var convertJob: Job? = null
    private var translateJob: Job? = null

    fun openCurrencyConverter() {
        updateState { copy(activeSection = ToolsSection.Currency) }
    }

    fun openTranslation() {
        updateState { copy(activeSection = ToolsSection.Translation) }
    }

    fun openEmergencyNumbers() {
        updateState { copy(activeSection = ToolsSection.Emergency) }
    }

    fun openUsefulPhrases() {
        val phrases = getUsefulPhrasesUseCase.getPhrases()
        updateState {
            copy(
                activeSection = ToolsSection.Phrases,
                usefulPhrases = phrases,
            )
        }
    }

    fun updateCurrencyInput(code: String, value: String) {
        convertJob?.cancel()
        convertJob = null

        val current = _state.value ?: ToolsState()
        val newAmounts = current.currencyAmounts.toMutableMap()
        newAmounts[code] = value
        updateState { copy(currencyAmounts = newAmounts.toMap()) }

        val normalized = value.replace(',', '.').trim()
        if (normalized.isEmpty()) {
            updateState {
                copy(
                    currencyAmounts = ToolsCurrencies.emptyAmounts(),
                    currencyConversionInProgress = false,
                )
            }
            return
        }

        val amount = normalized.toDoubleOrNull()
        if (amount == null || amount < 0) {
            updateState {
                copy(
                    currencyAmounts = newAmounts.toMap(),
                    currencyConversionInProgress = false,
                )
            }
            return
        }

        updateState { copy(currencyAmounts = newAmounts.toMap()) }

        val job = viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                updateState { copy(currencyConversionInProgress = true) }
            }
            try {
                val others = ToolsCurrencies.codes.filter { it != code }
                val merged = newAmounts.toMutableMap()
                merged[code] = value

                val batchResult =
                    convertCurrencyUseCase.convertToTargets(amount, code, others)
                batchResult.onSuccess { convertedByCode ->
                    others.forEach { target ->
                        merged[target] = convertedByCode[target]
                            ?: current.currencyAmounts[target].orEmpty()
                    }
                }.onFailure {
                    others.forEach { target ->
                        merged[target] = current.currencyAmounts[target].orEmpty()
                    }
                }
                merged[code] = value

                ensureActive()
                withContext(Dispatchers.Main) {
                    ensureActive()
                    updateState {
                        copy(currencyAmounts = merged.toMap())
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                withContext(Dispatchers.Main) {
                    updateState { copy() }
                }
            }
        }
        convertJob = job
        job.invokeOnCompletion {
            viewModelScope.launch(Dispatchers.Main) {
                if (convertJob === job) {
                    convertJob = null
                    updateState { copy(currencyConversionInProgress = false) }
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

        translateJob?.cancel()
        translateJob = null

        val job = viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                updateState { copy(translationInProgress = true) }
            }
            val result = translateTextUseCase.translate(
                text = t.sourceText,
                sourceLanguage = t.sourceLanguage,
                targetLanguage = t.targetLanguage
            )
            withContext(Dispatchers.Main) {
                result.onSuccess { text ->
                    val latest = _state.value ?: return@onSuccess
                    _state.value = latest.copy(
                        translation = latest.translation.copy(translatedText = text)
                    )
                }
            }
        }
        translateJob = job
        job.invokeOnCompletion {
            viewModelScope.launch(Dispatchers.Main) {
                if (translateJob === job) {
                    translateJob = null
                    updateState { copy(translationInProgress = false) }
                }
            }
        }
    }
}
