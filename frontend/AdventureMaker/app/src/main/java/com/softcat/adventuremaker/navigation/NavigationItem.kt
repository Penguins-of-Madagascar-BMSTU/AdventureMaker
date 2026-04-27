package com.softcat.adventuremaker.navigation

import com.example.domain.entities.Place
import com.example.domain.entities.User
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
        data class Profile(val user: User): Networking

        @Serializable
        data object CreatePost: Networking

        @Serializable
        data object Auth : NavigationItem
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
        Favourites, Tools, Search, Networking, None
    }
}