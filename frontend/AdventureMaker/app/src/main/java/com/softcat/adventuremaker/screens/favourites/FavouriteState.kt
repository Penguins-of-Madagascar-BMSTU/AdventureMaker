package com.softcat.adventuremaker.screens.favourites

import com.example.domain.entities.FavouriteScreenVariant
import com.example.domain.entities.Place

sealed interface FavouriteState {

    data object Loading: FavouriteState

    data object NoUser: FavouriteState

    data class Content(
        val filterCategory: Place.Category?,
        val places: List<Place>,
        val variant: FavouriteScreenVariant
    ): FavouriteState
}