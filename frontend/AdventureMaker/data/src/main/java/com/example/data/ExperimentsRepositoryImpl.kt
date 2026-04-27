package com.example.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.domain.entities.FavouriteScreenVariant
import com.example.domain.interfaces.ExperimentsRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlin.random.Random

class ExperimentsRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
): ExperimentsRepository {

    override suspend fun getFavouriteScreenVariant(): FavouriteScreenVariant {
        val existing = readFavouriteScreenVariant()
        if (existing != null)
            return existing

        val variant = if (Random.nextDouble() < FIRST_VARIANT_PROBABILITY)
            FavouriteScreenVariant.First else FavouriteScreenVariant.Second

        dataStore.edit { preferences ->
            val key = stringPreferencesKey(FAVOURITE_SCREEN_VARIANT_KEY)
            preferences[key] = variant.name
        }

        return variant
    }

    private suspend fun readFavouriteScreenVariant(): FavouriteScreenVariant? {
        val datastoreKey = stringPreferencesKey(FAVOURITE_SCREEN_VARIANT_KEY)
        val value = dataStore.data.firstOrNull()?.get(datastoreKey)
        return value?.let {
            FavouriteScreenVariant.valueOf(value)
        }
    }

    companion object {
        private const val FIRST_VARIANT_PROBABILITY = 0.5f

        private const val FAVOURITE_SCREEN_VARIANT_KEY = "favourite_screen_variant_key"
    }
}