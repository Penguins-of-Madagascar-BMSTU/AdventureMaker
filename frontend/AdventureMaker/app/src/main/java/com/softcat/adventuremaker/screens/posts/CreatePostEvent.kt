package com.softcat.adventuremaker.screens.posts

sealed interface CreatePostEvent {
    data object Published: CreatePostEvent
}