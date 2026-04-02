package com.example.domain.entities

data class Post(
    val id: String, // Идентификатор поста.
    val userId: String, // Идентификатор автора.
    val imageUrl: String, // Url, по которому можно загрузить картинку.
    val scoreValue: Int?, // Оценка места. Если она есть, то пост считается отзывом.
    val description: String, // Описание места, которое добавил автор поста.

    // Координаты места на карте.
    val latitude: Float,
    val longitude: Float
)