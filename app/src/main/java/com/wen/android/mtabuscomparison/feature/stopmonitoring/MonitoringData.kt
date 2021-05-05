package com.wen.android.mtabuscomparison.feature.stopmonitoring

import com.wen.android.mtabuscomparison.data.remote.bustime.Situations

data class MonitoringData(
    val stopId: String,
    val situations: Situations? = null,
    val busMonitoring: List<StopMonitoringListItem> = listOf(),
    val errorMessage: String = ""
)