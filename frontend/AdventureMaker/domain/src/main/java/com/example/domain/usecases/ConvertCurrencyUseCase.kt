package com.example.domain.usecases

import com.example.domain.interfaces.CurrencyConverterRepository

class ConvertCurrencyUseCase(
    private val repository: CurrencyConverterRepository
) {

    suspend fun convert(
        amount: Double,
        fromCurrency: String,
        toCurrency: String
    ): Result<Double> {
        if (amount < 0) {
            return Result.failure(IllegalArgumentException("Amount must be non-negative."))
        }
        if (fromCurrency.isBlank() || toCurrency.isBlank()) {
            return Result.failure(IllegalArgumentException("Currency code must not be blank."))
        }
        return repository.convert(amount, fromCurrency, toCurrency)
    }
}
