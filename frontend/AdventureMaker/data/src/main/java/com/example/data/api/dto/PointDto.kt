package com.example.data.api.dto

import com.google.gson.annotations.SerializedName

data class PointDto(
    @SerializedName("lat") val latitude: Float,
    @SerializedName("lon") val longitude: Float
)