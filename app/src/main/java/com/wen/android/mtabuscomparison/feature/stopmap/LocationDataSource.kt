package com.wen.android.mtabuscomparison.feature.stopmap

import android.annotation.SuppressLint
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber
import javax.inject.Inject

class LocationDataSource
@Inject constructor(
    private val locationClient: FusedLocationProviderClient
) {
    @SuppressLint("MissingPermission")
    val locationsSource: Flow<Location> = callbackFlow<Location> {
        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult?) {
                result ?: return
                try {
                    trySend(result.lastLocation)
                } catch (e: Exception) {
                }
            }
        }

        locationClient.lastLocation.addOnSuccessListener {
            it ?: return@addOnSuccessListener
            try {
                Timber.v(it.toString())
                trySend(it)
            } catch (e: java.lang.Exception) {
            }

        }
        // clean up when Flow collection ends
        awaitClose {
            locationClient.removeLocationUpdates(callback)
        }
    }
}
