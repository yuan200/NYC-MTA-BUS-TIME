package com.wen.android.mtabuscomparison.feature.stop.usecase

import com.wen.android.mtabuscomparison.common.BaseObservable
import com.wen.android.mtabuscomparison.data.mapper.ApiToUiMapper
import com.wen.android.mtabuscomparison.data.remote.bustime.StopMonitoringResponse
import com.wen.android.mtabuscomparison.feature.stop.MonitoringData
import com.wen.android.mtabuscomparison.netwoking.SiriService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FetchStopMonitoringUseCase(
    private val siriService: SiriService
) : BaseObservable<FetchStopMonitoringUseCase.Listener>() {
     private val apiToUiMapper = ApiToUiMapper()

    interface Listener {
        fun onStopMonitoringFetched(monitoringData: MonitoringData)
        fun onStopMonitoringFetchFailed()
    }

    fun fetchStopMonitoring(apiKey: String, stopId: String) {
        siriService.stopMonitoring(apiKey, stopId)
            .enqueue(object : Callback<StopMonitoringResponse> {
                override fun onResponse(
                    call: Call<StopMonitoringResponse>,
                    response: Response<StopMonitoringResponse>
                ) {
                    if (!response.isSuccessful || response.body() == null) {
                        notifyFailure()
                        return
                    }
                    notifySuccess(apiToUiMapper.mapToStopForId(response.body()!!, stopId))
                }

                override fun onFailure(call: Call<StopMonitoringResponse>, t: Throwable) {
                    notifyFailure()
                }

            })
    }

    private fun notifySuccess(result: MonitoringData) {
        for (listener in getListeners()) {
            listener.onStopMonitoringFetched(result)
        }
    }

    private fun notifyFailure() {
        for (listener in getListeners()) {
            listener.onStopMonitoringFetchFailed()
        }
    }
}