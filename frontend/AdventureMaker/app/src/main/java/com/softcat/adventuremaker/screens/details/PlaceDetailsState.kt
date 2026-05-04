package com.softcat.adventuremaker.screens.details

import com.example.domain.entities.Place

data class PlaceDetailsState(
    val isFavourite: Boolean?,
    val place: Place
)