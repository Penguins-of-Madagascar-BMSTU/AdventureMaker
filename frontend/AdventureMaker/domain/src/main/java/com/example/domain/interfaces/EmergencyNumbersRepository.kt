package com.example.domain.interfaces

interface EmergencyNumbersRepository {

    fun getEmergencyNumbers(): List<Pair<String, String>>
}
