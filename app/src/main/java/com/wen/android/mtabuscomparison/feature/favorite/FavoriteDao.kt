package com.wen.android.mtabuscomparison.feature.favorite

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Insert
    fun insertAll(vararg stop: FavoriteStop)

    @Insert
    suspend fun insert(stop: FavoriteStop): Long

    @Query("SELECT * FROM favorite_stop")
    fun getAll(): Flow<List<FavoriteStop>>

    //todo make this a suspend function
    @Query("DELETE FROM favorite_stop where _id = :id")
    fun delete(id: Int)
}