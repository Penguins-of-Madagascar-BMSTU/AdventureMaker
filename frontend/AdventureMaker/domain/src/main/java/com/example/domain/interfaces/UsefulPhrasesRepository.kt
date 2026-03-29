package com.example.domain.interfaces

interface UsefulPhrasesRepository {

    fun getPhrases(): List<Pair<String, String>>
}
