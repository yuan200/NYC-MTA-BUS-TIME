package com.wen.android.mtabuscomparison.stop

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface StopDao {

    @Insert
    fun insert(vararg stop: Stop)

    @Insert
    suspend fun insertAll(stops: List<Stop>)

    @Query("select * from stops where (stop_lat between :lat1 and :lat2) and (stop_lon between :lng1 and :lng2)")
    fun getStopsInRange(lat1: Double, lat2: Double, lng1: Double, lng2: Double): List<Stop>;

    @Query("SELECT * from stops")
    fun getAll(): List<Stop>
}