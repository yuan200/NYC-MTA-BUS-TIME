package com.wen.android.mtabuscomparison.feature.stopmonitoring.storage.repo

import android.content.Context
import com.wen.android.mtabuscomparison.common.Result
import com.wen.android.mtabuscomparison.data.mapper.ApiToUiMapper
import com.wen.android.mtabuscomparison.feature.stopmonitoring.BusStopDao
import com.wen.android.mtabuscomparison.feature.stopmonitoring.Stop
import com.wen.android.mtabuscomparison.feature.stopmonitoring.StopMonitoringData
import com.wen.android.mtabuscomparison.netwoking.siri.SiriApiService
import com.wen.android.mtabuscomparison.util.ConnectivityHelper
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import javax.inject.Inject

class DefaultStopMonitoringRepository
@Inject constructor(
    private val siriApiService: SiriApiService,
    private val busStopDao: BusStopDao,
    @ApplicationContext private val context: Context
) : StopMonitoringRepository {

    override suspend fun stopMonitoring(key: String, monitoringRef: String): Flow<Result<StopMonitoringData>> {

        try {
            if (!ConnectivityHelper.isNetworkEnabled(context)) {
                return flow {
                    emit(
                        Result.Failure("Network Error")
                    )
                }.flowOn(Dispatchers.IO)
            }

            flow {
                emit(Result.Loading)
            }

            val response = siriApiService.siri().stopMonitoring(key, monitoringRef)
            if (!response.isSuccessful) throw HttpException(response)

            return with(
                requireNotNull(response.body()) { "Response was successful but body was null" }
            ) {
                val result = ApiToUiMapper().mapToStopMonitoring(this, monitoringRef)
                return flow {
                    emit(Result.Success(result))
                }.flowOn(Dispatchers.IO)
            }
        } catch (e: Exception) {
            return flow {
                emit(
                    Result.Failure(e.localizedMessage)
                )
            }.flowOn(Dispatchers.IO)
        }
    }

    override fun stop(stopId: String): Flow<Stop?> {
        return busStopDao.getStop(stopId)
    }
}