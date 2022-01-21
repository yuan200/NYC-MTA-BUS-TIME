package com.wen.android.mtabuscomparison.netwoking.onebusaway

import com.wen.android.mtabuscomparison.netwoking.response.StopsForRouteResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface OneBusAwayApi {

    @GET("where/stops-for-route/{route}")
    suspend fun stopsForRoute(
        @Path("route") route: String,
        @Query("key") key: String
    ): Response<StopsForRouteResponse>

    enum class Agency(val agency: String) {
        MTABC("MTABC_"),
        NYCT("MTA NYCT_"),
        MTA("MTA_")
    }
}