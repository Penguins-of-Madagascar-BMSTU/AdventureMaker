package com.example.data.api.dto

import com.google.gson.annotations.SerializedName

data class PlaceSearchResultDto(
    @SerializedName("items") val items: List<PlaceDto>,
    @SerializedName("total") val count: Int
)