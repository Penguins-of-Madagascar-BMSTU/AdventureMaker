package com.softcat.adventuremaker.screens.tools

sealed interface ToolsState {

    data object Loading : ToolsState

    data class CurrencyConverter(
        val amount: String = "",
        val fromCurrency: String = "RUB",
        val toCurrency: String = "USD",
        val convertedAmount: String = ""
    ) : ToolsState

    data class Translation(
        val sourceText: String = "",
        val sourceLanguage: String = "ru",
        val targetLanguage: String = "en",
        val translatedText: String = ""
    ) : ToolsState

    data class EmergencyNumbers(
        val numbers: List<Pair<String, String>>
    ) : ToolsState
}
