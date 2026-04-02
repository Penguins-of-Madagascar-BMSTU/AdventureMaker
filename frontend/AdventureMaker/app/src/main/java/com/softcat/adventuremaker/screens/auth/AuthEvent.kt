package com.softcat.adventuremaker.screens.auth

import com.example.domain.entities.User

sealed interface AuthEvent {

    data class LoggedIn(val user: User): AuthEvent

    data class Error(val msg: String): AuthEvent
}