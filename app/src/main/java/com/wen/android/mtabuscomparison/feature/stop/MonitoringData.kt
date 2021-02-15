package com.wen.android.mtabuscomparison.feature.stop

import com.wen.android.mtabuscomparison.data.remote.bustime.Situations

data class MonitoringData(val stopId: String, val situations: Situations?, val busMonitoring: List<StopMonitoringListItem>, val errorMessage: String = "")