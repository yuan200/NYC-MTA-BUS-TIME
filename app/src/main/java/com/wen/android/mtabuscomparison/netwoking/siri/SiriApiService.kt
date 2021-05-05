package com.wen.android.mtabuscomparison.netwoking.siri

import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SiriApiService
@Inject constructor(retrofit: Retrofit){
    private val siriApi by lazy { retrofit.create(SiriApi::class.java) }

    fun siri(): SiriApi {
       return siriApi
    }
}