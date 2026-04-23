package com.softcat.adventuremaker.screens.profile

import android.net.Uri
import com.example.domain.entities.Post
import com.example.domain.entities.User

sealed interface ProfileState {

    data object Loading : ProfileState

    data object NoUser : ProfileState

    data class Content(
        val user: User,
        val posts: List<Post>,
        val selectedAvatarUri: Uri? = null
    ) : ProfileState
}
