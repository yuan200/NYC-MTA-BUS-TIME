package com.wen.android.mtabuscomparison.data.remote.bustime

data class StopMonitoringDelivery(
    val MonitoredStopVisit: List<MonitoredStopVisit>?,
    val ResponseTimestamp: String,
    val ValidUntil: String,
    val ErrorCondition: ErrorCondition?
)