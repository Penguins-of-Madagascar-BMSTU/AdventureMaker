package com.example.data

import com.example.data.api.CurrencyApiService
import com.example.domain.interfaces.CurrencyConverterRepository
import java.math.BigDecimal
import java.math.RoundingMode

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

    override suspend fun convertToTargets(
        amount: Double,
        fromCurrency: String,
        targetCurrencies: List<String>,
    ): Result<Map<String, String>> {
        if (targetCurrencies.isEmpty()) {
            return Result.success(emptyMap())
        }
        return try {
            val response = api.getRates(fromCurrency.uppercase())

            if (response.result != "success") {
                return Result.failure(Exception("API error"))
            }

            val rates = response.rates
            val out = mutableMapOf<String, String>()
            for (target in targetCurrencies) {
                val rate = rates[target.uppercase()] ?: continue
                out[target] = formatConvertedAmount(amount * rate)
            }
            Result.success(out)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun formatConvertedAmount(value: Double): String {
        return BigDecimal.valueOf(value)
            .setScale(10, RoundingMode.HALF_UP)
            .stripTrailingZeros()
            .toPlainString()
    }
}

check