package com.wen.android.mtabuscomparison.injection

import android.content.Context
import com.wen.android.mtabuscomparison.BuildConfig
import com.wen.android.mtabuscomparison.feature.stopmonitoring.BusDatabase
import com.wen.android.mtabuscomparison.feature.stopmonitoring.BusStopDao
import com.wen.android.mtabuscomparison.feature.stopmonitoring.storage.repo.DefaultStopMonitoringRepository
import com.wen.android.mtabuscomparison.feature.stopmonitoring.storage.repo.StopMonitoringRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://bustime.mta.info/api/siri/")
            .client(createClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun createClient(): OkHttpClient {
        val okHttpClientBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) {
            val loggingInterceptor =
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC)
            okHttpClientBuilder.addInterceptor(loggingInterceptor)
        }
        return okHttpClientBuilder.build()
    }

    @Provides
    @Singleton
    fun provideStopMonitoringRepository(dataSource: DefaultStopMonitoringRepository): StopMonitoringRepository =
        dataSource

    @Provides
    @Singleton
    fun provideStopDB(@ApplicationContext context: Context): BusStopDao {
        return BusDatabase
            .getInstance(context)
            .busStopDao()
    }
}