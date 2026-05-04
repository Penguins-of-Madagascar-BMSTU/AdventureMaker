package com.example.data.api.dto

data class PostDto(
    val id: String = "",
    val userId: String = "",
    val imageUrl: String = "",
    val scoreValue: Int? = null,
    val description: String = "",
    val latitude: Float = 0f,
    val longitude: Float = 0f
)