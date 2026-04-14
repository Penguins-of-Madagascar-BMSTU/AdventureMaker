package com.softcat.adventuremaker.screens.search.model

import com.example.domain.entities.Place
import com.softcat.adventuremaker.R
import com.softcat.adventuremaker.screens.search.PlaceItemModel
import com.softcat.adventuremaker.screens.search.SearchCategoryModel

class SearchViewModelMapper {

    fun mapToCategoryList(selectedCategory: Place.Category) = Place.Category.entries.map {
        mapToCategoryModel(it, it == selectedCategory)
    }

    fun mapToPlaceModels(places: List<Place>, favouriteIds: List<String>): List<PlaceItemModel> {
        return places.map {
            val iconResId = if (it.id in favouriteIds)
                R.drawable.heart_filled
            else
                R.drawable.heart

            PlaceItemModel(
                id = it.id,
                title = it.name,
                imageUrl = it.imageUrls.firstOrNull() ?: "",
                iconResId = iconResId
            )
        }
    }

    private fun mapToCategoryModel(category: Place.Category, isSelected: Boolean): SearchCategoryModel {
        val titleResId = when (category) {
            Place.Category.Museum -> R.string.museum_category
            Place.Category.Entertainment -> R.string.entertainment_category
            Place.Category.Bank -> R.string.bank_category
            Place.Category.Hotel -> R.string.hotel_category
            Place.Category.Attraction -> R.string.attraction_category
            Place.Category.Restaurant -> R.string.restaurant_category
            Place.Category.Unknown -> R.string.search_sheet_category_all
        }
        return SearchCategoryModel(category, titleResId, isSelected)
    }
}