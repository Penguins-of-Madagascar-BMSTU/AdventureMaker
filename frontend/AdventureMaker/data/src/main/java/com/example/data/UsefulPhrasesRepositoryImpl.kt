package com.example.data

import com.example.domain.interfaces.UsefulPhrasesRepository

class UsefulPhrasesRepositoryImpl : UsefulPhrasesRepository {

    override fun getPhrases(): List<Pair<String, String>> {
        return phrases
    }

    companion object {
        private val phrases = listOf(
            "Hi (Hello)" to "Привет (Здравствуйте)",
            "Good morning / afternoon / evening" to "Доброе утро/ день/ вечер",
            "How are you? (How do you do?)" to "Как дела?",
            "Nice to meet you" to "Приятно познакомиться",
            "Good night/ Goodbye / See you" to "Доброй ночи /До свидания / Увидимся",
            "Have a good day" to "Хорошего дня",
            "Please" to "Пожалуйста",
        )
    }
}
