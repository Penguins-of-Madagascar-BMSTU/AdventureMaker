package com.example.data.api.dto

import com.google.gson.annotations.SerializedName

data class PlaceDto(
    @SerializedName("point") val point: PointDto,
    @SerializedName("address") val address: AddressDto,
    @SerializedName("type") val type: String,
    @SerializedName("description") val description: String?,
    @SerializedName("rubrics") val rubrics: List<RubricDto>
)