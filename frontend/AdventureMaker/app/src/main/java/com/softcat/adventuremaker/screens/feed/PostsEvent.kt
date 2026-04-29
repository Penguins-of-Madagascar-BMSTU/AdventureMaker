package com.softcat.adventuremaker.screens.feed

interface PostsEvent {
    data class Error(val msg: String): PostsEvent
}