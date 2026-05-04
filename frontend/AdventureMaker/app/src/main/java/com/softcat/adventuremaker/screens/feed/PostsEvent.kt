package com.softcat.adventuremaker.screens.feed

sealed interface PostsEvent {
    data class Error(val msg: String): PostsEvent
}