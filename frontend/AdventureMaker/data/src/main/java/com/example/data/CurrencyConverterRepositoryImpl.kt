package com.example.data

import com.example.domain.interfaces.CurrencyConverterRepository

class CurrencyConverterRepositoryImpl : CurrencyConverterRepository {

    override suspend fun convert(
        amount: Double,
        fromCurrency: String,
        toCurrency: String
    ): Result<Double> {
        val normalizedFrom = fromCurrency.uppercase()
        val normalizedTo = toCurrency.uppercase()
        val fromRate = exchangeRatesToRub[normalizedFrom]
            ?: return Result.failure(IllegalArgumentException("Unsupported currency: $fromCurrency"))
        val toRate = exchangeRatesToRub[normalizedTo]
            ?: return Result.failure(IllegalArgumentException("Unsupported currency: $toCurrency"))

        val amountInRub = amount * fromRate
        return Result.success(amountInRub / toRate)
    }

    companion object {
        private val exchangeRatesToRub = mapOf(
            "RUB" to 1.0,
            "USD" to 90.0,
            "EUR" to 98.0
        )
    }
}
