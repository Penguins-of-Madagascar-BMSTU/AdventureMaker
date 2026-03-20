package com.softcat.adventuremaker.navigation

import com.example.domain.entities.Place
import kotlinx.serialization.Serializable

sealed interface NavigationItem {

    @Serializable
    sealed interface Favourites: NavigationItem {

        @Serializable
        data object Content: Favourites

        @Serializable
        data class PlaceDetails(val place: Place): Favourites
    }

    @Serializable
    sealed interface Networking: NavigationItem {
        @Serializable
        data object Posts: Networking

        @Serializable
        data object Profile: Networking
    }

    @Serializable
    sealed interface Search: NavigationItem {

        @Serializable
        data object Map: Search

        @Serializable
        data class Details(val place: Place): Search
    }

    @Serializable
    data object Tools: NavigationItem

    enum class BottomBarConfiguration {
        Favourites, Tools, Search, Networking
    }
}