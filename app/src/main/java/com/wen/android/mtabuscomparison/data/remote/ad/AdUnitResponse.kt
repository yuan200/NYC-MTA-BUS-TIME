package com.wen.android.mtabuscomparison.data.remote.ad

data class AdUnitResponse(
    val adUnits: List<AdUnit>,
    val expired: Long
) {
}