package com.softcat.adventuremaker.screens.profile

import com.example.domain.entities.Post
import com.example.domain.entities.User

sealed interface ProfileState {

    data object Loading : ProfileState

    data class Content(
        val user: User,
        val posts: List<Post>,
    ) : ProfileState
}
