package com.example.domain.entities

data class Place(
    val id: String,
    val name: String,
    val description: String,
    val category: Category,
    val imageUrl: String,
    val latitude: Float,
    val longitude: Float,
) {
    enum class Category {
        Museum, Entertainment, Bank, Hotel, Attraction, Restaurant
    }
}