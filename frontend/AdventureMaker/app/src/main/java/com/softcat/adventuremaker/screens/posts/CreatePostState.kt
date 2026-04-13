package com.softcat.adventuremaker.screens.posts


sealed interface CreatePostState {

    data object Idle : CreatePostState // начальное состояние

    data class Editing(
        val description: String = "",
        val imageUri: android.net.Uri? = null,
        val score: Int? = null
    ) : CreatePostState

    data object Loading : CreatePostState

    data object Success : CreatePostState

    data class Error(
        val message: String
    ) : CreatePostState
}