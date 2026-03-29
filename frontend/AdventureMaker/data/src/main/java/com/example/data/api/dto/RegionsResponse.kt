package com.example.data.api.dto

import com.google.gson.annotations.SerializedName

data class RegionsResponse(
    @SerializedName("meta") val meta: MetaDto,
    @SerializedName("result") val result: RegionsResultDto
)