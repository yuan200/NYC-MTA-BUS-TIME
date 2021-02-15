package com.wen.android.mtabuscomparison.netwoking

import com.wen.android.mtabuscomparison.data.remote.bustime.StopMonitoringResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface SiriService {
    //http://bustime.mta.info/api/siri/stop-monitoring.json?key=272e0b38-54a4-485f-875a-e9b1460a9509&MonitoringRef=401844

    @GET("stop-monitoring.json")
    fun stopMonitoring(
        @Query("key") key: String,
        @Query("MonitoringRef") monitoringRef: String
    ) : Call<StopMonitoringResponse>
}