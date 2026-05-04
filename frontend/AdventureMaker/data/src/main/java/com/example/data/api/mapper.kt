package com.example.data.api

import com.example.data.api.dto.PlaceDto
import com.example.data.api.dto.PostDto
import com.example.data.api.dto.RegionDto
import com.example.data.api.dto.UserDto
import com.example.domain.entities.City
import com.example.domain.entities.Place
import com.example.domain.entities.Post
import com.example.domain.entities.User

fun PlaceDto.toEntity() = Place(
    id = id,
    name = name,
    description = description.orEmpty(),
    category = rubrics.first().id.toCategory(),
    imageUrls = emptyList(),
    latitude = point.latitude,
    longitude = point.longitude,
    address = address
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
    name = name,
    latitude = regionLocDto.latitude,
    longitude = regionLocDto.longitude
)

fun UserDto.toEntity() = User(
    id = id,
    name = name,
    email = email,
    avatarUrl = avatarUrl.ifEmpty { null },
)

fun User.toDto() = UserDto(
    id = id,
    email = email,
    name = name,
    avatarUrl = avatarUrl ?: ""
)

fun PostDto.toEntity() = Post(
    id = id,
    userId = userId,
    imageUrl = imageUrl,
    scoreValue = scoreValue,
    description = description,
    latitude = latitude,
    longitude = longitude
)

fun Post.toDto() = PostDto(
    id = id,
    userId = userId,
    imageUrl = imageUrl,
    scoreValue = scoreValue,
    description = description,
    latitude = latitude,
    longitude = longitude
)