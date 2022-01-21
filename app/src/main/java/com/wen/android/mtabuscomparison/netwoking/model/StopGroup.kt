package com.wen.android.mtabuscomparison.netwoking.model

data class StopGroup(
    val id: String?,
    val name: Name,
    val polylines: List<PolylineX>?,
    val stopIds: List<String>,
    val subGroups: List<Any>?
)