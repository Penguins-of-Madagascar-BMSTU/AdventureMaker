package com.example.data.api

import com.example.data.api.dto.PlaceDto
import com.example.data.api.dto.RegionDto
import com.example.domain.entities.City
import com.example.domain.entities.Place

fun PlaceDto.toEntity() = Place(
    id = address.id,
    name = address.name,
    description = description.orEmpty(),
    category = rubrics.first().id.toCategory(),
    imageUrls = emptyList(),
    latitude = point.latitude,
    longitude = point.longitude,
    address = address.address + ", " + address.comment
)

fun String.toCategory() = when (this) {
    "193", "112670", "15800" -> Place.Category.Museum
    "161", "164", "52248", "1203", "15791", "111594", "162", "112658", "469" -> Place.Category.Restaurant
    "24169" -> Place.Category.Attraction
    "14967" -> Place.Category.Entertainment
    "492", "498" -> Place.Category.Bank
    "269" -> Place.Category.Hotel
    else -> Place.Category.Unknown
}

fun RegionDto.toEntity() = City(
    id = id,
    name = name
)