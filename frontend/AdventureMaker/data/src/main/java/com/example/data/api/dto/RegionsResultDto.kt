package com.example.data.api.dto

import com.google.gson.annotations.SerializedName

data class RegionsResultDto(
    @SerializedName("items") val items: List<RegionDto>
)