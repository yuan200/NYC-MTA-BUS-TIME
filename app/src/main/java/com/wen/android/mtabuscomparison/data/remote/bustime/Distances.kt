package com.wen.android.mtabuscomparison.data.remote.bustime

data class Distances(
    val CallDistanceAlongRoute: Double,
    val DistanceFromCall: Double,
    val PresentableDistance: String,
    val StopsFromCall: Int
)