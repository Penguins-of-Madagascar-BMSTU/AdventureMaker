package com.softcat.adventuremaker.screens.profile

sealed interface ProfileEvent {

    data object Exited: ProfileEvent

    data class Error(
        val msg: String
    ): ProfileEvent
}