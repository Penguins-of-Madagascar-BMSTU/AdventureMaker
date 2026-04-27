package com.softcat.adventuremaker.screens.search.model

sealed interface SearchScreenEvent {
    data class Error(val message: String): SearchScreenEvent
}
