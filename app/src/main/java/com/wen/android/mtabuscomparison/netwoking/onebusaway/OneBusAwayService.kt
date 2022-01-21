package com.wen.android.mtabuscomparison.netwoking.onebusaway

import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OneBusAwayService
@Inject constructor(retrofit: Retrofit){

    private val oneBusAwayApi by lazy {
        retrofit.create(OneBusAwayApi::class.java)
    }

    fun oneBusAway(): OneBusAwayApi = oneBusAwayApi
}