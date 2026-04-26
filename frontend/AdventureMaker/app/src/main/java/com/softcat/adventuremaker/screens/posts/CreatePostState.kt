package com.softcat.adventuremaker.screens.posts


data class CreatePostState(
    val description: String = "",
    val imageUri: android.net.Uri? = null,
    val score: Int? = null,
    val errorMessage: String? = null,
    val isLoading: Boolean = false
)