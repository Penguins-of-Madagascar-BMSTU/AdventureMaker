package com.example.data

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.example.domain.interfaces.LocationRepository
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull

class LocationRepositoryImpl(
    private val client: FusedLocationProviderClient,
    private val context: Context
): LocationRepository {

    @SuppressLint("MissingPermission")
    override suspend fun getUserLocation(cancellationToken: CancellationToken?): Pair<Double, Double>? {
        val location = runCatching {
            val request = CurrentLocationRequest.Builder()
                .setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY)
                .setMaxUpdateAgeMillis(60_000)
                .build()

            withTimeoutOrNull(10_000L) {
                if (locationPermission()) {
                    client.getCurrentLocation(
                        request,
                        cancellationToken ?: CancellationTokenSource().token
                    ).await()
                } else
                    null
            }
        }.getOrNull()

        return location?.let { it.latitude to it.longitude }
    }

    @SuppressLint("MissingPermission")
    override fun observeLocationUpdates(
        cancellationToken: CancellationToken?,
    ): Flow<Pair<Double, Double>> = callbackFlow {
        if (!locationPermission()) {
            close()
            return@callbackFlow
        }

        val builder = LocationRequest.Builder(
            Priority.PRIORITY_BALANCED_POWER_ACCURACY,
            LOC_UPDATE_INTERVAL
        )
        val request = builder
            .setMinUpdateIntervalMillis(LOC_UPDATE_INTERVAL / 2)
            .setWaitForAccurateLocation(false)
            .build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location ->
                    trySend(location.latitude to location.longitude)
                }
            }
        }

        try {
            client.requestLocationUpdates(
                request,
                locationCallback,
                Looper.getMainLooper()
            )

            awaitClose {
                client.removeLocationUpdates(locationCallback)
            }

        } catch (e: Exception) {
            close(e)
        }
    }

    private fun locationPermission() = ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    companion object {
        private const val LOC_UPDATE_INTERVAL = 5000L
    }
}