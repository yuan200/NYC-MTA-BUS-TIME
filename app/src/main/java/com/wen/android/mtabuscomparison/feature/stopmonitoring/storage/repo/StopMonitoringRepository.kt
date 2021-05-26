package com.wen.android.mtabuscomparison.feature.stopmonitoring.storage.repo

import com.wen.android.mtabuscomparison.common.Result
import com.wen.android.mtabuscomparison.feature.stopmonitoring.Stop
import com.wen.android.mtabuscomparison.feature.stopmonitoring.StopMonitoringData
import kotlinx.coroutines.flow.Flow

interface StopMonitoringRepository {

    //network
    suspend fun stopMonitoring(key: String, monitoringRef: String): Flow<Result<StopMonitoringData>>

    //local
    fun stop(stopId: String): Flow<Stop?>

}