package com.wen.android.mtabuscomparison.feature.stoproute

import com.wen.android.mtabuscomparison.common.Result
import com.wen.android.mtabuscomparison.feature.stopmonitoring.BusDirection

interface StopRouteRepository {

    suspend fun getStopRoute(route: String, key: String): Result<BusDirection?>

}