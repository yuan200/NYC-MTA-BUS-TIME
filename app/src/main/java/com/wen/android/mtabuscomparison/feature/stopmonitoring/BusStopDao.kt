package com.wen.android.mtabuscomparison.feature.stopmonitoring

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BusStopDao {

    @Insert
    fun insert(vararg stop: Stop)

    @Insert
    suspend fun insertAll(stops: List<Stop>)

    @Query("select * from stops where (stop_lat between :lat1 and :lat2) and (stop_lon between :lng1 and :lng2)")
    fun getStopsInRange(lat1: Double, lat2: Double, lng1: Double, lng2: Double): Flow<List<Stop>>

    @Query("SELECT * from stops")
    fun getAll(): List<Stop>

    @Query("SELECT * FROM stops WHERE stop_name LIKE '%' || :query || '%' OR stop_id = :query")
    suspend fun searchByStopNameOrId(query: String): List<Stop>

    @Query("SELECT * FROM stops WHERE stop_id = :stopId LIMIT 1")
    fun getStop(stopId: String): Flow<Stop>
}