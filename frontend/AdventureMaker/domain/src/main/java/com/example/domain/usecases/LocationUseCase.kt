package com.example.domain.usecases

import com.example.domain.interfaces.LocationRepository
import com.google.android.gms.tasks.CancellationToken

class LocationUseCase(
    private val repository: LocationRepository
) {
    suspend fun getUserLocation(cancellationToken: CancellationToken?) =
        repository.getUserLocation(cancellationToken)

    fun observeUserLocation(cancellationToken: CancellationToken) =
        repository.observeLocationUpdates(cancellationToken)
}