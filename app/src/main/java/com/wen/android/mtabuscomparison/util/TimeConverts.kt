package com.wen.android.mtabuscomparison.util

import androidx.room.TypeConverter
import java.util.*

class TimeConverts {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }

}