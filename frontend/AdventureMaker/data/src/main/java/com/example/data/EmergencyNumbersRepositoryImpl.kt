package com.example.data

import com.example.domain.interfaces.EmergencyNumbersRepository

class EmergencyNumbersRepositoryImpl : EmergencyNumbersRepository {

    override suspend fun getEmergencyNumbers(): List<String> {
        return emergencyNumbers
    }

    companion object {
        private val emergencyNumbers = listOf(
            "101 - пожарная охрана",
            "102 - полиция",
            "103 - скорая помощь",
            "104 - аварийная газовая служба",
            "112 - единый номер экстренных служб"
        )
    }
}
