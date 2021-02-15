package com.wen.android.mtabuscomparison.feature.ad

import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import com.wen.android.mtabuscomparison.BusApplication
import com.wen.android.mtabuscomparison.R
import com.wen.android.mtabuscomparison.data.proto.AdUnitSerializer.adUnitDataStore
import com.wen.android.mtabuscomparison.data.remote.ad.AdUnitProtos
import com.wen.android.mtabuscomparison.data.remote.ad.AdUnitResponse
import com.wen.android.mtabuscomparison.netwoking.AdApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject

class FetchAdUnitUseCase @Inject constructor(
    private val adApi: AdApi
) {

    fun fetchAdUnit() {
        adApi.getAdUnit()
            .enqueue(object : Callback<AdUnitResponse> {
                override fun onResponse(
                    call: Call<AdUnitResponse>,
                    response: Response<AdUnitResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        CoroutineScope(Dispatchers.IO).launch {
                            val context = BusApplication.instance.applicationContext
                            context.adUnitDataStore.updateData { currentAdUnits ->
                                currentAdUnits.toBuilder()
                                    .clearAdUnits()
                                    .addAllAdUnits(response.body()!!.adUnits.map {
                                        AdUnitProtos.AdUnit.newBuilder().setAdUnitId(it.adUnitId)
                                            .setEnabled(it.enabled)
                                            .setLocation(it.location)
                                            .build()
                                    }.toList())
                                    .setExpired(response.body()!!.expired).build()


                            }
                        }
                    }
                }

                override fun onFailure(call: Call<AdUnitResponse>, t: Throwable) {
                    Timber.i(t.toString())
                }

            })
    }
}