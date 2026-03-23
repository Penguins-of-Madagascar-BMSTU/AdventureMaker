package com.example.data

import com.example.domain.interfaces.EmergencyNumbersRepository

class EmergencyNumbersRepositoryImpl : EmergencyNumbersRepository {

    override fun getEmergencyNumbers(): List<Pair<String, String>> {
        return emergencyNumbers
    }

    companion object {
        private val emergencyNumbers = listOf(
            "101" to "пожарная охрана",
            "102" to "полиция",
            "103" to "скорая помощь",
            "104" to "аварийная газовая служба",
            "112" to "единый номер экстренных служб"
        )
    }
}
