package com.wen.android.mtabuscomparison.ui.stopmonitoring

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.wen.android.mtabuscomparison.R
import dagger.hilt.android.AndroidEntryPoint

// todo this activity only use for StopRouteActivity and should be removed in a near feature
@AndroidEntryPoint
class StopMonitoringActivity : AppCompatActivity() {
    private lateinit var apiKey: String
    private lateinit var mStopMonitoringView: StopMonitoringViewMvc
    private var mRowId: Int = Integer.MAX_VALUE
    private var mStopId: String = ""
    private val mFavoriteCheck = "favorite_checked"
    private val mViewModel: StopMonitoringViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stop_monitoring_2)
        if (intent.hasExtra(Intent.EXTRA_TEXT)) {
            mStopId = intent.getStringExtra(Intent.EXTRA_TEXT)
            val stopMonitoringFragment = StopMonitoringFragment()
            val bundle = Bundle().apply {
                putString("stopId", mStopId)
                putBoolean("isFavorite", false)
                putInt("dbRowId", -1)
            }
            stopMonitoringFragment.arguments = bundle
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container_view, stopMonitoringFragment, null)
                .commit()
        }


    }


    override fun onResume() {
        super.onResume()
    }

}