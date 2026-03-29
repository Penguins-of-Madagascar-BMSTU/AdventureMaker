package com.example.data.api.dto

import com.google.gson.annotations.SerializedName

data class RatesResponse(
    @SerializedName("result") val result: String,
    @SerializedName("base_code") val baseCode: String,
    @SerializedName("rates") val rates: Map<String, Double>
)