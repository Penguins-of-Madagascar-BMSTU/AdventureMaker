package com.softcat.adventuremaker.screens.posts

import com.example.domain.entities.Post

sealed interface PostsState {

    data object Loading : PostsState

    data object Empty : PostsState

    data class Content(
        val posts: List<Post>
    ) : PostsState

    data class Error(
        val message: String
    ) : PostsState
}