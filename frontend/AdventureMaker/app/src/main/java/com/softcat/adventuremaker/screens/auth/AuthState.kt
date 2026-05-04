package com.softcat.adventuremaker.screens.auth

sealed interface AuthState {

    data object Loading: AuthState

    data object Initial: AuthState

    data class Enter(
        val email: String,
        val password: String
    ): AuthState

    data class Register(
        val name: String,
        val email: String,
        val password: String,
        val repeatedPassword: String
    ): AuthState
}