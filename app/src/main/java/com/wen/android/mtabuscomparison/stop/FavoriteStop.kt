package com.wen.android.mtabuscomparison.stop

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "favorite_stop")
data class FavoriteStop(
        @ColumnInfo(name = "stop_id") val stopId: String,
        @ColumnInfo(name = "stop_id2") val stopId2: String?,
        @ColumnInfo(name = "stop_id3") val stopId3: String?,
        val busLine: String?,
        val busName: String?,
        val groupName: String?,
        val timestamp: Date?
) {
    @PrimaryKey(autoGenerate = true) var _id: Int = 0
}
