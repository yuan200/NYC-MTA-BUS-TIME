package com.wen.android.mtabuscomparison.feature.stoproute

import android.content.Context
import com.wen.android.mtabuscomparison.common.Result
import com.wen.android.mtabuscomparison.feature.stopmonitoring.BusDirection
import com.wen.android.mtabuscomparison.netwoking.model.StopsForRouteMapper
import com.wen.android.mtabuscomparison.netwoking.onebusaway.OneBusAwayApi
import com.wen.android.mtabuscomparison.netwoking.onebusaway.OneBusAwayService
import com.wen.android.mtabuscomparison.util.ConnectivityHelper
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StopRouteRepositoryImpl
@Inject constructor(
    private val oneBusAwayService: OneBusAwayService,
    @ApplicationContext private val context: Context
) : StopRouteRepository {

    /// TODO: 1/17/22 avoid unnecessary call by use the correct agency
    /**
     * should try on three different path
     * MTABC_Q18.json
     * "http://bustime.mta.info/api/where/stops-for-route/MTABC_",
     * "http://bustime.mta.info/api/where/stops-for-route/MTA NYCT_",
     * "http://bustime.mta.info/api/where/stops-for-route/MTA_"
     */
    override suspend fun getStopRoute(route: String, key: String): Result<BusDirection?> {
        if (!ConnectivityHelper.isNetworkEnabled(context)) {
            return Result.Failure(
                "Sorry, MTA BUS TIME encountered a network error, " +
                        "please try again later"
            )
        }
        val agencyList = buildAgencyPath(route)
        for (agencyRoute in agencyList) {
            val response = oneBusAwayService.oneBusAway().stopsForRoute(
                route = agencyRoute,
                key
            )
            Timber.i(response.toString())
            if (response.isSuccessful) {
                val response = StopsForRouteMapper().mapToDomainModel(response.body()!!.data!!)
                return Result.Success(response)
            }
        }
        return Result.Failure("Sorry, Something went wrong, please try again")
    }

    private fun buildAgencyPath(route: String): List<String> {
        return OneBusAwayApi.Agency.values()
            .map {
                it.agency + route + ".json"
            }.toList()
    }
}