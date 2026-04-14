package com.softcat.adventuremaker.screens.search

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.domain.entities.Place

data class SearchCategoryModel(
    val key: Place.Category,
    @StringRes val titleResId: Int,
    val isSelected: Boolean
)

data class PlaceItemModel(
    val id: String,
    val title: String,
    val imageUrl: String,
    val isFavourite: Boolean
)

data class SearchBottomSheetState(
    val places: List<PlaceItemModel>,
    val categories: List<SearchCategoryModel>,
    val isSheetVisible: Boolean,
)