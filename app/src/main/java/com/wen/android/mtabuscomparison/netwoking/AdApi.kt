package com.wen.android.mtabuscomparison.netwoking

import com.wen.android.mtabuscomparison.data.remote.ad.AdUnitResponse
import retrofit2.Call
import retrofit2.http.GET

interface AdApi {
    @GET("/api/adunit")
    fun getAdUnit() : Call<AdUnitResponse>
}