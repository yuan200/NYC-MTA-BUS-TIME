package com.wen.android.mtabuscomparison.data.remote.bustime

data class ServiceDelivery(
    val ResponseTimestamp: String,
    val SituationExchangeDelivery: List<SituationExchangeDelivery>?,
    val StopMonitoringDelivery: List<StopMonitoringDelivery>
)