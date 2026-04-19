package com.example.data.api.dto

import com.google.gson.annotations.SerializedName

data class RegionLocationDto(
    @SerializedName("lat") val latitude: Double,
    @SerializedName("lon") val longitude: Double
)
