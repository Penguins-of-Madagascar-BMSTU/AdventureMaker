package com.example.data.api.dto

import com.google.gson.annotations.SerializedName

data class PlaceDto(
    @SerializedName("id") val id: String,
    @SerializedName("point") val point: PointDto,
    @SerializedName("address_name") val address: String,
    @SerializedName("address_comment") val addressComment: String,
    @SerializedName("name") val name: String,
    @SerializedName("type") val type: String,
    @SerializedName("description") val description: String?,
    @SerializedName("rubrics") val rubrics: List<RubricDto>
)