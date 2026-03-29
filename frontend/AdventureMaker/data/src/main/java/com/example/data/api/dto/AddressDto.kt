package com.example.data.api.dto

import com.google.gson.annotations.SerializedName

data class AddressDto(
    @SerializedName("address_name") val address: String,
    @SerializedName("address_comment") val comment: String,
    @SerializedName("full_name") val fullName: String,
    @SerializedName("name") val name: String,
    @SerializedName("id") val id: String,
)