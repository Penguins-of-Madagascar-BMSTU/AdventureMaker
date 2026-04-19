package com.softcat.adventuremaker.screens.search.model

import androidx.annotation.StringRes
import com.softcat.adventuremaker.R

data class SearchCategoryChipModel(
    val id: String,
    @StringRes val titleResId: Int
)

data class SearchPlaceItemModel(
    val id: String,
    val title: String,
    val imageUrl: String,
    val isFavorite: Boolean,
    val categoryId: String
)

object SearchSheetMockData {
    const val CATEGORY_ALL = "all"
    const val CATEGORY_MUSEUMS = "museums"
    const val CATEGORY_ENTERTAINMENT = "entertainment"
    const val CATEGORY_BANKS = "banks"
    const val CATEGORY_LANDMARKS = "landmarks"
    const val CATEGORY_FOOD = "food"
    const val CATEGORY_HOTELS = "hotels"

    val categories = listOf(
        SearchCategoryChipModel(
            id = CATEGORY_ALL,
            titleResId = R.string.search_sheet_category_all
        ),
        SearchCategoryChipModel(
            id = CATEGORY_MUSEUMS,
            titleResId = R.string.search_sheet_category_museums
        ),
        SearchCategoryChipModel(
            id = CATEGORY_ENTERTAINMENT,
            titleResId = R.string.search_sheet_category_entertainment
        ),
        SearchCategoryChipModel(
            id = CATEGORY_BANKS,
            titleResId = R.string.search_sheet_category_banks
        ),
        SearchCategoryChipModel(
            id = CATEGORY_LANDMARKS,
            titleResId = R.string.search_sheet_category_landmarks
        ),
        SearchCategoryChipModel(
            id = CATEGORY_FOOD,
            titleResId = R.string.search_sheet_category_food
        ),
        SearchCategoryChipModel(
            id = CATEGORY_HOTELS,
            titleResId = R.string.search_sheet_category_hotels
        )
    )

    val places = listOf(
        SearchPlaceItemModel(
            id = "1",
            title = "Riverside Museum of Modern Art",
            imageUrl = "https://picsum.photos/id/1040/300/300",
            isFavorite = true,
            categoryId = CATEGORY_MUSEUMS
        ),
        SearchPlaceItemModel(
            id = "2",
            title = "City Lights Performance Hall",
            imageUrl = "https://picsum.photos/id/1068/300/300",
            isFavorite = false,
            categoryId = CATEGORY_ENTERTAINMENT
        ),
        SearchPlaceItemModel(
            id = "3",
            title = "Grand Avenue Exchange Bank",
            imageUrl = "https://picsum.photos/id/1031/300/300",
            isFavorite = false,
            categoryId = CATEGORY_BANKS
        ),
        SearchPlaceItemModel(
            id = "4",
            title = "Old Town Clock Tower and Observation Point",
            imageUrl = "https://picsum.photos/id/1025/300/300",
            isFavorite = true,
            categoryId = CATEGORY_LANDMARKS
        ),
        SearchPlaceItemModel(
            id = "5",
            title = "Amber Courtyard Bistro",
            imageUrl = "https://picsum.photos/id/292/300/300",
            isFavorite = false,
            categoryId = CATEGORY_FOOD
        ),
        SearchPlaceItemModel(
            id = "6",
            title = "Harbor View Suites",
            imageUrl = "https://picsum.photos/id/1018/300/300",
            isFavorite = false,
            categoryId = CATEGORY_HOTELS
        ),
        SearchPlaceItemModel(
            id = "7",
            title = "National Gallery of Sculpture and Exhibitions",
            imageUrl = "https://picsum.photos/id/237/300/300",
            isFavorite = true,
            categoryId = CATEGORY_MUSEUMS
        ),
        SearchPlaceItemModel(
            id = "8",
            title = "Canal Street Food Market",
            imageUrl = "https://picsum.photos/id/1080/300/300",
            isFavorite = false,
            categoryId = CATEGORY_FOOD
        )
    )
}
