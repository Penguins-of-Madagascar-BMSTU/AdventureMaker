package com.softcat.adventuremaker.screens.tools

object ToolsCurrencies {
    val codes = listOf("RUB", "USD", "EUR", "CNY", "BYN")

    fun emptyAmounts(): Map<String, String> = codes.associateWith { "" }
}

sealed interface ToolsState {

    data object Loading : ToolsState

    data class CurrencyConverter(
        val amounts: Map<String, String> = ToolsCurrencies.emptyAmounts(),
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
