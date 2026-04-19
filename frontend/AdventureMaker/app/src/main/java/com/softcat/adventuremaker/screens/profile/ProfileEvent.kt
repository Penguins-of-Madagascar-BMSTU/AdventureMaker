package com.softcat.adventuremaker.screens.profile

sealed interface ProfileEvent {

    data object Exited: ProfileEvent
}