package com.wen.android.mtabuscomparison.util

import java.text.SimpleDateFormat
import java.util.*

class TimeUtils {
    companion object {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US)
    }
}

    fun String?.getMinutesFromNow(): String {
        if (this.isNullOrEmpty()) return ""
        val futureDate = TimeUtils.dateFormat.parse(this)
        val diff = futureDate.time - Date().time
        val minutes = (diff / (60 * 1000) % 60).toInt()
        return minutes.toString()
    }

    fun String?.getTime(): String {
        if (this.isNullOrEmpty()) return ""
        val futureDate = TimeUtils.dateFormat.parse(this)
        val cal = Calendar.getInstance().apply {
            time = futureDate
        }
        return String.format("%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))

    }