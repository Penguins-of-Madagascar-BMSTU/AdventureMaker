package com.example.domain.usecases

import com.example.domain.interfaces.EmergencyNumbersRepository

class GetEmergencyNumbersUseCase(
    private val repository: EmergencyNumbersRepository
) {

    fun getEmergencyNumbers(): List<Pair<String, String>> {
        return repository.getEmergencyNumbers()
    }
}
