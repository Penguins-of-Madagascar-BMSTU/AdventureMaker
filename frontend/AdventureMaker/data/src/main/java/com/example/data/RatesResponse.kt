package com.example.data

data class RatesResponse(
    val result: String,
    val base_code: String,
    val rates: Map<String, Double>
)