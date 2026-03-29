package com.example.data

import com.example.data.FirebaseRules.POSTS_STORAGE_NAME
import com.example.domain.entities.Place
import com.example.domain.entities.Post
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import kotlinx.coroutines.tasks.await
import kotlin.math.sqrt

class PlaceImageProvider {

    private val postsStorage by lazy {
        Firebase.database.getReference(POSTS_STORAGE_NAME)
    }

    suspend fun provideImages(places: List<Place>): List<Place> {
        val dataSnapshot = postsStorage.limitToFirst(1000).get().await()
        val posts = dataSnapshot.children.mapNotNull { postData ->
            postData.getValue<Post>()
        }
        return places.map { place ->
            val post = posts.find { post -> matches(post, place) }
            if (post == null)
                place
            else
                place.copy(imageUrls = listOf(post.imageUrl))
        }
    }

    private fun matches(post: Post, place: Place): Boolean {
        val dx = (post.latitude - place.latitude)
        val dy = (post.longitude - place.longitude)
        val r = sqrt(dx * dx + dy * dy)
        return r < 0.001
    }
}