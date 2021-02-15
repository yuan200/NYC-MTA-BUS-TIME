package com.wen.android.mtabuscomparison.injection

import com.wen.android.mtabuscomparison.BuildConfig
import com.wen.android.mtabuscomparison.netwoking.AdApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(ActivityComponent::class)
object AdModule {

    @Provides
    fun provideAdApi(client: OkHttpClient): AdApi = if (BuildConfig.DEBUG) {
        retrofit2.Retrofit.Builder()
            .baseUrl("http://192.168.1.16:8080")
            .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
            .client(client)
            .build()
            .create(com.wen.android.mtabuscomparison.netwoking.AdApi::class.java)

    } else {
        Retrofit.Builder()
            .baseUrl("http://52.45.123.166:8080")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(AdApi::class.java)
    }

    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

}