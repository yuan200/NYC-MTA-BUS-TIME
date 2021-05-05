package com.wen.android.mtabuscomparison.feature.stopmonitoring.storage.repo

import android.content.Context
import com.wen.android.mtabuscomparison.data.mapper.ApiToUiMapper
import com.wen.android.mtabuscomparison.feature.stopmonitoring.BusStopDao
import com.wen.android.mtabuscomparison.feature.stopmonitoring.MonitoringData
import com.wen.android.mtabuscomparison.feature.stopmonitoring.Stop
import com.wen.android.mtabuscomparison.netwoking.siri.SiriApiService
import com.wen.android.mtabuscomparison.util.ConnectivityHelper
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException
import javax.inject.Inject

class DefaultStopMonitoringRepository
@Inject constructor(
    private val siriApiService: SiriApiService,
    private val busStopDao: BusStopDao,
    @ApplicationContext private val context: Context
) : StopMonitoringRepository {

    override suspend fun stopMonitoring(key: String, monitoringRef: String): MonitoringData {

        try {
            if (!ConnectivityHelper.isNetworkEnabled(context)) {
                return MonitoringData(
                    monitoringRef,
                    errorMessage = "Network Error"
                )
            }
            val response = siriApiService.siri().stopMonitoring(key, monitoringRef)
            if (!response.isSuccessful) throw HttpException(response)

            return with(
                requireNotNull(response.body()) { "Response was successful but body was null" }
            ) {
                val apiToUiMapper = ApiToUiMapper()
                apiToUiMapper.mapToStopMonitoring(this, monitoringRef)
            }
        } catch (e: Exception) {
            return MonitoringData(
                monitoringRef,
                errorMessage = "Sorry, something went wrong"
            )
        }
    }

    override fun stop(stopId: String): Flow<Stop?> {
        return busStopDao.getStop(stopId)
    }
}