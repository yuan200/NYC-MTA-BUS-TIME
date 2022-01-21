package com.wen.android.mtabuscomparison.netwoking.response

import com.wen.android.mtabuscomparison.netwoking.model.StopsForRouteDto

data class StopsForRouteResponse(
    val code: Int,
    val currentTime: Long,
    val `data`: StopsForRouteDto?,
    val text: String,
    val version: Int
)