package com.wen.android.mtabuscomparison.data.remote.bustime

data class MonitoredCall(
    val AimedArrivalTime: String,
    val ExpectedArrivalTime: String,
    val ExpectedDepartureTime: String,
    val Extensions: Extensions,
    val StopPointName: String,
    val StopPointRef: String,
    val VisitNumber: Int
)