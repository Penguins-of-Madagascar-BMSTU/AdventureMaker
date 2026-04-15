package com.example.domain.entities

import android.os.Parcelable
import kotlinx.serialization.Serializable
import kotlinx.parcelize.Parcelize

@Serializable
@Parcelize
data class Place(
    val id: String,
    val name: String,
    val description: String,
    val category: Category,
    val imageUrls: List<String>,
    val latitude: Float,
    val longitude: Float,
    val address: String
): Parcelable {
    enum class Category {
        Museum, Entertainment, Bank, Hotel, Attraction, Restaurant, Unknown
    }
}