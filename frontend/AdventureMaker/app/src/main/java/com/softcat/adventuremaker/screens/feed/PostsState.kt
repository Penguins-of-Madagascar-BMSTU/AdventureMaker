package com.softcat.adventuremaker.screens.feed

import com.softcat.adventuremaker.designElements.models.PostModel

data class PostsState(
    val posts: List<PostModel> = emptyList(),
    val userId: String? = null,
    val isLoading: Boolean = false,
    val hasNext: Boolean = true
)