package com.wen.android.mtabuscomparison.netwoking.model

data class StopsForRouteDto(
    val polylines: List<Polyline>?,
    val route: Route?,
    val stopGroupings: List<StopGrouping>,
    val stops: List<Stop>
)