package com.example.domain.usecases

import com.example.domain.interfaces.UsefulPhrasesRepository

class GetUsefulPhrasesUseCase(
    private val repository: UsefulPhrasesRepository
) {

    fun getPhrases(): List<Pair<String, String>> {
        return repository.getPhrases()
    }
}
