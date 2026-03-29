package com.example.data

import com.example.data.api.CurrencyApiService
import com.example.domain.interfaces.CurrencyConverterRepository

class CurrencyConverterRepositoryImpl(
    private val api: CurrencyApiService
) : CurrencyConverterRepository {

    override suspend fun convert(
        amount: Double,
        fromCurrency: String,
        toCurrency: String
    ): Result<Double> {
        return try {
            val response = api.getRates(fromCurrency.uppercase())

            if (response.result != "success") {
                return Result.failure(Exception("API error"))
            }

            val rate = response.rates[toCurrency.uppercase()]
                ?: return Result.failure(
                    IllegalArgumentException("Unsupported currency")
                )

            Result.success(amount * rate)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}