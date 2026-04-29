package com.softcat.adventuremaker.screens.createPost

import com.example.domain.entities.Post
import com.softcat.adventuremaker.screens.feed.PostModel

sealed interface PostsState {

    data object Loading : PostsState

    data object Empty : PostsState

    data class Content(
        val posts: List<PostModel>
    ) : PostsState

    data class Error(
        val message: String
    ) : PostsState
}