package com.wen.android.mtabuscomparison.ui.stopmonitoring

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.wen.android.mtabuscomparison.R
import com.wen.android.mtabuscomparison.feature.favorite.FavoriteStop
import com.wen.android.mtabuscomparison.feature.stop.BusDatabase.Companion.getInstance
import com.wen.android.mtabuscomparison.feature.stop.MonitoringData
import com.wen.android.mtabuscomparison.feature.stop.usecase.FetchStopMonitoringUseCase
import com.wen.android.mtabuscomparison.netwoking.SiriApi
import com.wen.android.mtabuscomparison.netwoking.SiriService
import java.util.*
import java.util.concurrent.Executors

class StopMonitoringActivity : AppCompatActivity(), FetchStopMonitoringUseCase.Listener,
    StopMonitoringViewMvc.Listener,
    StopMonitoringViewMvc.FavoriteListener {
    private lateinit var mStopPointName: String
    private val mStopIds: MutableSet<String> = mutableSetOf()
    private lateinit var siriService: SiriService
    private lateinit var apiKey: String
    private lateinit var mStopMonitoringView: StopMonitoringViewMvc
    private lateinit var mFetchStopMonitoringUseCase: FetchStopMonitoringUseCase
    private var mRowId: Int = Integer.MAX_VALUE
    private val DATABASE_ROW_ID = "row_id"
    private val mFavoriteCheck = "favorite_checked"
    private lateinit var mTimer: Timer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mStopMonitoringView = StopMonitoringViewMvcImpl(layoutInflater, null, this)
        setContentView(mStopMonitoringView.getRootView())
        siriService = SiriApi().getSiriService()
        mFetchStopMonitoringUseCase = FetchStopMonitoringUseCase(siriService)
        if (intent.hasExtra(DATABASE_ROW_ID)) {
            mRowId = intent.getStringExtra(DATABASE_ROW_ID).toInt()
        }
        if (intent.hasExtra(mFavoriteCheck)) {
            mStopMonitoringView.setFavorite(true)
        }

        apiKey = getString(R.string.mta_bus_api_key)
    }

    override fun onStart() {
        super.onStart()
        mFetchStopMonitoringUseCase.registerListener(this)

        mStopMonitoringView.registerListener(this)

        mTimer = Timer()
        mTimer.schedule(
            object : TimerTask() {
                override fun run() {
                    for (stopId in intent.getStringArrayExtra(Intent.EXTRA_TEXT)) {
                        if (stopId.isNotBlank()) {
                            mFetchStopMonitoringUseCase.fetchStopMonitoring(
                                apiKey,
                                stopId
                            )
                        }
                    }
                }
            }, 0, 1000 * 30
        )

    }

    override fun onResume() {
        mStopMonitoringView.onResume()
        super.onResume()
    }

    override fun onPause() {
        mStopMonitoringView.onPause()
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
        mFetchStopMonitoringUseCase.unregisterListener(this)
        mStopMonitoringView.unregisterListener(this)
        if (mTimer != null) {
            mTimer.cancel()
            mTimer.purge()
        }
    }

    override fun onDestroy() {
        mStopMonitoringView.onDestroy()
        super.onDestroy()
    }

    override fun onStopMonitoringFetched(monitoringData: MonitoringData) {
        if (monitoringData.busMonitoring.isNotEmpty()) {
            mStopIds.add(monitoringData.busMonitoring[0].stopNumber)
            mStopPointName = monitoringData.busMonitoring[0].stopPointName
        }
        mStopMonitoringView.setRefreshing(false)
//        mStopMonitoringView.refreshMonitoringView(monitoringData)
        mStopMonitoringView.checkError(monitoringData)
        mStopMonitoringView.setAdapterData(monitoringData.busMonitoring)
    }

    override fun onStopMonitoringFetchFailed() {
        Toast.makeText(this, "fetch stop monitoring fail", Toast.LENGTH_LONG)
    }

    override fun onSwipeRefresh() {
        mStopMonitoringView.setRefreshing(true)
        mFetchStopMonitoringUseCase.fetchStopMonitoring(
            apiKey,
            intent.getStringArrayExtra(Intent.EXTRA_TEXT)[0]
        )
    }

    fun addToFavorite() {
        //todo disable this button if no info
        if (!mStopIds.isNullOrEmpty()) {
            val favorite = FavoriteStop(
                mStopIds.elementAtOrElse(0) { "" },
                mStopIds.elementAtOrElse(1) { "" },
                mStopIds.elementAtOrElse(2) { "" },
                "",
                "",
                mStopPointName,
                Date()
            )
            Executors.newSingleThreadExecutor().execute {
                mRowId = getInstance(
                    applicationContext
                ).favoriteStopDao().insert(favorite).toInt()
            }
        }
    }

    fun removeFromFavorite() {
        Executors.newSingleThreadExecutor().execute {
            getInstance(
                applicationContext
            ).favoriteStopDao().delete(mRowId)
        }
    }

    override fun onClickedFavorite() {
        if (mStopMonitoringView.getFavorite()) {
            mStopMonitoringView.setFavorite(false)
            removeFromFavorite()
        } else {
            mStopMonitoringView.setFavorite(true)
            addToFavorite()
        }
    }
}