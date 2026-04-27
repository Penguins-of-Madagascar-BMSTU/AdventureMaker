package com.example.domain.interfaces

import com.example.domain.entities.FavouriteScreenVariant

interface ExperimentsRepository {
    suspend fun getFavouriteScreenVariant(): FavouriteScreenVariant
}