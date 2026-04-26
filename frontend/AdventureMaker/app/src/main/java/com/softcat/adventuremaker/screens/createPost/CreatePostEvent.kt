package com.softcat.adventuremaker.screens.createPost

sealed interface CreatePostEvent {
    data object Published: CreatePostEvent
}