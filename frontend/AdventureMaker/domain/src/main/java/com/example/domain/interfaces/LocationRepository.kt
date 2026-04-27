package com.example.domain.interfaces

import com.google.android.gms.tasks.CancellationToken
import kotlinx.coroutines.flow.Flow

interface LocationRepository {

    suspend fun getUserLocation(cancellationToken: CancellationToken? = null): Pair<Double, Double>?

    fun observeLocationUpdates(
        cancellationToken: CancellationToken? = null,
    ): Flow<Pair<Double, Double>>
}