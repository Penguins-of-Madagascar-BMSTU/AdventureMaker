package com.example.domain.interfaces

interface CurrencyConverterRepository {

    suspend fun convert(
        amount: Double,
        fromCurrency: String,
        toCurrency: String
    ): Result<Double>
}
