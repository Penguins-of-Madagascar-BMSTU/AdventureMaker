package com.softcat.adventuremaker.screens.profile

import com.example.domain.entities.User
import com.softcat.adventuremaker.designElements.models.PostModel

sealed interface ProfileState {

    data object Loading : ProfileState

    data class Content(
        val user: User,
        val posts: List<PostModel>,
    ) : ProfileState
}
