package com.softcat.adventuremaker.screens.feed

data class PostModel(
    val id: String,
    val authorName: String,
    val authorAvatarUrl: String,
    val address: String,
    val imageUrl: String,
    val description: String,
    val score: Int?
)