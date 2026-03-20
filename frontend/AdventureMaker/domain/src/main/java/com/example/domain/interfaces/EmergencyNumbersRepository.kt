package com.example.domain.interfaces

interface EmergencyNumbersRepository {

    suspend fun getEmergencyNumbers(): List<String>
}
