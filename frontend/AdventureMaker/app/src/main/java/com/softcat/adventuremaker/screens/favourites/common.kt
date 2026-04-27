package com.softcat.adventuremaker.screens.favourites

import com.example.domain.entities.Place
import com.softcat.adventuremaker.R

fun getCategoryNameId(category: Place.Category?) = when (category) {
    Place.Category.Museum -> R.string.museum_category
    Place.Category.Entertainment -> R.string.entertainment_category
    Place.Category.Bank -> R.string.bank_category
    Place.Category.Hotel -> R.string.hotel_category
    Place.Category.Attraction -> R.string.attraction_category
    Place.Category.Restaurant -> R.string.restaurant_category
    else -> R.string.any_category
}