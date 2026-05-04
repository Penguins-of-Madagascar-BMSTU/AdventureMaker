package com.example.domain.interfaces

import com.example.domain.entities.Post
import kotlinx.coroutines.flow.StateFlow

interface PostsRepository {

    // Получить список постов, связанных с местом, наиболее близким к координатам пользователя.
    // Осуществляется подписка на StateFlow, так как посты могут подгружаться.
    suspend fun getPosts(userLat: Double, userLon: Double): StateFlow<List<Post>>

    // Загрузить ещё несколько новых постов для тех же координат пользователя.
    // Нужно для подгрузки следующей порции постов при прокручивании списка с постами.
    suspend fun loadMorePosts(): Boolean
}