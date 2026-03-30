package com.example.data

import com.example.data.FirebaseRules.POSTS_STORAGE_NAME
import com.example.domain.entities.Place
import com.example.domain.entities.Post
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import com.google.maps.android.SphericalUtil
import kotlinx.coroutines.tasks.await

class PlaceImageProvider {

    private val postsStorage by lazy {
        Firebase.database.getReference(POSTS_STORAGE_NAME)
    }

    suspend fun provideImages(places: List<Place>): List<Place> {
        try {
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
        } catch (_: Exception) {
            return places
        }
    }

    private fun matches(post: Post, place: Place): Boolean {
        val x1 = post.latitude.toDouble()
        val y1 = post.longitude.toDouble()
        val x2 = place.latitude.toDouble()
        val y2 = place.longitude.toDouble()
        val r = SphericalUtil.computeDistanceBetween(
            LatLng(x1, y1),
            LatLng(x2, y2)
        )
        return r < 10
    }
}