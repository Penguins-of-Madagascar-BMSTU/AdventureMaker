package com.softcat.adventuremaker.screens.feed

data class PostsState(
    val posts: List<PostModel> = emptyList(),
    val userId: String? = null,
    val isLoading: Boolean = false
)