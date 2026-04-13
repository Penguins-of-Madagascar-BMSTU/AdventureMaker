package com.example.data.api.dto

import com.google.gson.annotations.SerializedName

class RegionDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("type") val type: String,
)