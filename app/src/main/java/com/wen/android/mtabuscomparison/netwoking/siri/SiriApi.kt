package com.wen.android.mtabuscomparison.netwoking.siri

import com.wen.android.mtabuscomparison.data.remote.bustime.StopMonitoringResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SiriApi {

    @GET("siri/stop-monitoring.json")
    suspend fun stopMonitoring(
        @Query("key") key: String,
        @Query("MonitoringRef") monitoringRef: String
    ) : Response<StopMonitoringResponse>
}