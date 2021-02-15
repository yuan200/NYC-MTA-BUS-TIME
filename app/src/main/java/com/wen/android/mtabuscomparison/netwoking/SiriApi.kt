package com.wen.android.mtabuscomparison.netwoking

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SiriApi {
    private val retrofit: Retrofit
    private val baseUrl: String
        get() = "http://bustime.mta.info/api/siri/"

    init {
        retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getSiriService(): SiriService {
       return retrofit.create(SiriService::class.java)
    }

}