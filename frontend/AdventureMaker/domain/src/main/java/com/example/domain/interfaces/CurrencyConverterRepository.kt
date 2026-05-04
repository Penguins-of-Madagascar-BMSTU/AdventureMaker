package com.example.domain.interfaces

interface CurrencyConverterRepository {

    suspend fun convert(
        amount: Double,
        fromCurrency: String,
        toCurrency: String
    ): Result<Double>

    suspend fun convertToTargets(
        amount: Double,
        fromCurrency: String,
        targetCurrencies: List<String>,
    ): Result<Map<String, String>>
}
