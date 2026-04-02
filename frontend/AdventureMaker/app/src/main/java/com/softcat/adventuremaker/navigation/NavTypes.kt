package com.softcat.adventuremaker.navigation

import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavType
import com.example.domain.entities.Place
import kotlinx.serialization.json.Json

object NavTypes {

    val PlaceNavType = object: NavType<Place>(false) {

        override fun get(bundle: Bundle, key: String): Place? {
            return bundle.getParcelable(key, Place::class.java)
        }

        override fun parseValue(value: String): Place {
            return Json.decodeFromString(Uri.decode(value))
        }

        override fun serializeAsValue(value: Place): String {
            return Uri.encode(Json.encodeToString(Place.serializer(), value))
        }

        override fun put(bundle: Bundle, key: String, value: Place) {
            bundle.putParcelable(key, value)
        }
    }
}