package com.wen.android.mtabuscomparison.stop

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FavoriteStopDao {

    @Insert
    fun insertAll(vararg stop: FavoriteStop)

    @Query("SELECT * from favorite_stop")
    fun getAll(): List<FavoriteStop>

    @Query("DELETE FROM favorite_stop where _id = :id")
    fun delete(id: Int)
}