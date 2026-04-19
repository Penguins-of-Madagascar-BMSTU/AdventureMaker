package com.softcat.adventuremaker.navigation

import com.example.domain.entities.Place
import kotlinx.serialization.Serializable

/**
 * Типобезопасные маршруты приложения для Jetpack Navigation (Serializable).
 */
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

        @Serializable
        data object CreatePost: Networking
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

    /** Какой пункт нижней панели считается активным на текущем экране. */
    enum class BottomBarConfiguration {
        Favourites, Tools, Search, Networking
    }
}