package com.example.domain.usecases

import com.example.domain.interfaces.ExperimentsRepository

class ExperimentsUseCase(
    private val repository: ExperimentsRepository
) {
    suspend fun getFavouriteScreenVariant() = repository.getFavouriteScreenVariant()
}