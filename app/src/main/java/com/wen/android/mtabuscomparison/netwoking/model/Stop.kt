package com.wen.android.mtabuscomparison.netwoking.model

data class Stop(
    val code: String?,
    val direction: String?,
    val id: String?,
    val lat: Double?,
    val locationType: Int?,
    val lon: Double?,
    val name: String?,
    val routes: List<RouteX>?,
    val wheelchairBoarding: String?
)