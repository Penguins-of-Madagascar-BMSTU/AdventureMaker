package com.example.data.api.dto

import com.google.gson.annotations.SerializedName

data class RubricDto(
    @SerializedName("alias") val alias: String,
    @SerializedName("id") val id: String
)