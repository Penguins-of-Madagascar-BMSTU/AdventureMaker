package com.softcat.adventuremaker.navigation

sealed interface MainNavigation {
    data object Favourites: MainNavigation

    data object Networking: MainNavigation

    data object Search: MainNavigation

    data object Utils: MainNavigation
}