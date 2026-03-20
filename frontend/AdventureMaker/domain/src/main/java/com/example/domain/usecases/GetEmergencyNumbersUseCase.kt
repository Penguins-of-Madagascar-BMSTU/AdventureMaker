package com.example.domain.usecases

import com.example.domain.interfaces.EmergencyNumbersRepository

class GetEmergencyNumbersUseCase(
    private val repository: EmergencyNumbersRepository
) {

    suspend fun getEmergencyNumbers(): List<String> {
        return repository.getEmergencyNumbers()
    }
}
