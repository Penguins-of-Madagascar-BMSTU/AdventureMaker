package com.softcat.adventuremaker.screens.search.model

import org.osmdroid.util.GeoPoint

data class MapState(
    val places: List<PlacePin>, // Локации мест, которые найдены для пользователя.
    val center: GeoPoint, // Положение камеры.
    val zoom: Double
)

data class PlacePin(
    val point: GeoPoint,
    val title: String
)