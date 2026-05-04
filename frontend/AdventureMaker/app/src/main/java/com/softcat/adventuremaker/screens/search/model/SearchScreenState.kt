package com.softcat.adventuremaker.screens.search.model

import com.softcat.adventuremaker.screens.search.SearchBottomSheetState

data class SearchScreenState(
    val bottomSheetState: SearchBottomSheetState,
    val query: String,
    val cityName: String,
    val availableCities: List<String>,
    val mapState: MapState
)