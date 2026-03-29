package com.softcat.adventuremaker.screens.tools

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.usecases.ConvertCurrencyUseCase
import com.example.domain.usecases.GetEmergencyNumbersUseCase
import com.example.domain.usecases.TranslateTextUseCase
import java.math.BigDecimal
import java.math.RoundingMode
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ToolsViewModel(
    private val convertCurrencyUseCase: ConvertCurrencyUseCase,
    private val translateTextUseCase: TranslateTextUseCase,
    private val getEmergencyNumbersUseCase: GetEmergencyNumbersUseCase
) : ViewModel() {

    private val _state = MutableLiveData<ToolsState>(ToolsState.CurrencyConverter())
    val state: LiveData<ToolsState>
        get() = _state

    private var currencyConverterState = ToolsState.CurrencyConverter()
    private var translationState = ToolsState.Translation()
    private var emergencyNumbersState: ToolsState.EmergencyNumbers? = null

    private var convertJob: Job? = null

    init {
        _state.value = currencyConverterState
    }

    fun openCurrencyConverter() {
        _state.value = currencyConverterState
    }

    fun openTranslation() {
        _state.value = translationState
    }

    fun openEmergencyNumbers() {
        emergencyNumbersState?.let {
            _state.value = it
            return
        }

        val numbers = getEmergencyNumbersUseCase.getEmergencyNumbers()
        val newState = ToolsState.EmergencyNumbers(numbers)
        emergencyNumbersState = newState
        _state.value = newState
    }

    fun updateCurrencyInput(code: String, value: String) {
        convertJob?.cancel()

        val current = currencyConverterState
        val newAmounts = current.amounts.toMutableMap()
        newAmounts[code] = value
        currencyConverterState = current.copy(amounts = newAmounts.toMap())
        _state.value = currencyConverterState

        val normalized = value.replace(',', '.').trim()
        if (normalized.isEmpty()) {
            val cleared = ToolsCurrencies.emptyAmounts()
            currencyConverterState = current.copy(amounts = cleared)
            _state.value = currencyConverterState
            return
        }

        val amount = normalized.toDoubleOrNull()
        if (amount == null || amount < 0) {
            currencyConverterState = current.copy(amounts = newAmounts.toMap())
            _state.value = currencyConverterState
            return
        }

        currencyConverterState = current.copy(amounts = newAmounts.toMap())
        _state.value = currencyConverterState

        convertJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                val others = ToolsCurrencies.codes.filter { it != code }
                val merged = newAmounts.toMutableMap()
                merged[code] = value

                for (target in others) {
                    ensureActive()
                    val result = convertCurrencyUseCase.convert(amount, code, target)
                    result.onSuccess { converted ->
                        merged[target] = formatAmount(converted)
                    }.onFailure {
                        merged[target] = current.amounts[target].orEmpty()
                    }
                }
                merged[code] = value

                ensureActive()
                withContext(Dispatchers.Main) {
                    ensureActive()
                    currencyConverterState =
                        ToolsState.CurrencyConverter(amounts = merged.toMap())
                    _state.value = currencyConverterState
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                withContext(Dispatchers.Main) {
                    _state.value = currencyConverterState
                }
            }
        }
    }

    private fun formatAmount(value: Double): String {
        return BigDecimal.valueOf(value)
            .setScale(10, RoundingMode.HALF_UP)
            .stripTrailingZeros()
            .toPlainString()
    }

    fun changeTextToTranslate(newValue: String) {
        val currentState = (state.value as? ToolsState.Translation) ?: return
        translationState = currentState.copy(sourceText = newValue)
        _state.value = translationState
    }

    fun changeSourceLanguage(newValue: String) {
        val currentState = (state.value as? ToolsState.Translation) ?: return
        translationState = currentState.copy(sourceLanguage = newValue)
        _state.value = translationState
    }

    fun changeTargetLanguage(newValue: String) {
        val currentState = (state.value as? ToolsState.Translation) ?: return
        translationState = currentState.copy(targetLanguage = newValue)
        _state.value = translationState
    }

    fun translateText() {
        val currentState = (state.value as? ToolsState.Translation) ?: return
        if (currentState.sourceText.isBlank()) return

        viewModelScope.launch(Dispatchers.IO) {
            val result = translateTextUseCase.translate(
                text = currentState.sourceText,
                sourceLanguage = currentState.sourceLanguage,
                targetLanguage = currentState.targetLanguage
            )
            withContext(Dispatchers.Main) {
                result.onSuccess { translatedText ->
                    translationState = currentState.copy(translatedText = translatedText)
                    _state.value = translationState
                }
            }
        }
    }
}
