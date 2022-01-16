package com.wen.android.mtabuscomparison.feature.stopmonitoring

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stops")
data class Stop(
    @PrimaryKey @ColumnInfo(name = "stop_id") val stopId: String,
    @ColumnInfo(name = "stop_name") val stopName: String?,
    @ColumnInfo(name = "stop_lat") val stopLat: Double,
    @ColumnInfo(name = "stop_lon") val stopLon: Double,
    @ColumnInfo(name = "route_id") val routeId: String?
) {
    fun hasRoute(route: String): String? {
        if (routeId == null) return null
        val route = route.uppercase()

        val routeList = routeId.split(' ')
        for (r in routeList) {
            if (r == route) return r
        }
        return null
    }
}