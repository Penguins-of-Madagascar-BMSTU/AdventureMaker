package com.example.domain.entities

/** Данные пользователя для профиля и сессии. */
data class User(
    val id: String,
    val name: String,
    val email: String,
    val avatarUrl: String?,
)
