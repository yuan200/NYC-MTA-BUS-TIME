package com.wen.android.mtabuscomparison.feature.stopmap

import android.location.Location
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocationRepository
@Inject constructor(
    private val locationDataSource: LocationDataSource
) {

    val locations: Flow<Location> = locationDataSource
        .locationsSource
}