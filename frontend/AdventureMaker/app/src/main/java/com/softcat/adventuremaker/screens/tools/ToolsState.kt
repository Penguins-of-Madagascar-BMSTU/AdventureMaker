package com.softcat.adventuremaker.screens.tools

object ToolsCurrencies {
    val codes = listOf("RUB", "USD", "EUR", "CNY", "BYN")

    fun emptyAmounts(): Map<String, String> = codes.associateWith { "" }
}

enum class ToolsSection {
    Currency,
    Translation,
    Phrases,
    Emergency,
}

data class TranslationState(
    val sourceText: String = "",
    val sourceLanguage: String = "en",
    val targetLanguage: String = "ru",
    val translatedText: String = "",
)


data class ToolsState(
    val activeSection: ToolsSection = ToolsSection.Currency,
    val currencyAmounts: Map<String, String> = ToolsCurrencies.emptyAmounts(),
    val translation: TranslationState = TranslationState(),
    val emergencyNumbers: List<Pair<String, String>>? = null,
    val usefulPhrases: List<Pair<String, String>>? = null,
    val currencyConversionInProgress: Boolean = false,
    val translationInProgress: Boolean = false,
)
