package com.softcat.adventuremaker.screens.tools

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.usecases.ConvertCurrencyUseCase
import com.example.domain.usecases.GetEmergencyNumbersUseCase
import com.example.domain.usecases.TranslateTextUseCase
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ToolsViewModel(
    private val convertCurrencyUseCase: ConvertCurrencyUseCase,
    private val translateTextUseCase: TranslateTextUseCase,
    private val getEmergencyNumbersUseCase: GetEmergencyNumbersUseCase
) : ViewModel() {

    private val _state = MutableLiveData<ToolsState>(ToolsState.Loading)
    val state: LiveData<ToolsState>
        get() = _state

    private var currencyConverterState = ToolsState.CurrencyConverter()
    private var translationState = ToolsState.Translation()
    private var emergencyNumbersState: ToolsState.EmergencyNumbers? = null

    init {
        openCurrencyConverter()
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

        _state.value = ToolsState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            val numbers = getEmergencyNumbersUseCase.getEmergencyNumbers()
            val newState = ToolsState.EmergencyNumbers(numbers)
            emergencyNumbersState = newState
            withContext(Dispatchers.Main) {
                _state.value = newState
            }
        }
    }

    fun changeAmount(newValue: String) {
        val currentState = (state.value as? ToolsState.CurrencyConverter) ?: return
        currencyConverterState = currentState.copy(amount = newValue)
        _state.value = currencyConverterState
    }

    fun changeFromCurrency(newValue: String) {
        val currentState = (state.value as? ToolsState.CurrencyConverter) ?: return
        currencyConverterState = currentState.copy(fromCurrency = newValue)
        _state.value = currencyConverterState
    }

    fun changeToCurrency(newValue: String) {
        val currentState = (state.value as? ToolsState.CurrencyConverter) ?: return
        currencyConverterState = currentState.copy(toCurrency = newValue)
        _state.value = currencyConverterState
    }

    fun convertCurrency() {
        val currentState = (state.value as? ToolsState.CurrencyConverter) ?: return
        val amount = currentState.amount.toDoubleOrNull() ?: return

        viewModelScope.launch(Dispatchers.IO) {
            val result = convertCurrencyUseCase.convert(
                amount = amount,
                fromCurrency = currentState.fromCurrency,
                toCurrency = currentState.toCurrency
            )
            withContext(Dispatchers.Main) {
                result.onSuccess { convertedAmount ->
                    currencyConverterState = currentState.copy(
                        convertedAmount = String.format(Locale.US, "%.2f", convertedAmount)
                    )
                    _state.value = currencyConverterState
                }
            }
        }
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
