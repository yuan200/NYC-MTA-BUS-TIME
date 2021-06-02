package com.wen.android.mtabuscomparison.ui.stopmap

import com.wen.android.mtabuscomparison.feature.stopmonitoring.BusDatabase
import com.wen.android.mtabuscomparison.feature.stopmonitoring.Stop
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface NearByRepository {

    fun getNearByStops(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Flow<List<Stop>>
}

class DefaultNearByRepository
@Inject constructor(
    private val busDatabase: BusDatabase
) : NearByRepository {

    override fun getNearByStops(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Flow<List<Stop>> {
        return busDatabase.busStopDao()
            .getStopsInRange(lat1, lon1, lat2, lon2)
    }

}

data class NearByRange(
    val latitude1 : Double,
    val longitude1 : Double,
    val latitude2: Double,
    val longitude2: Double
)