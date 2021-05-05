package com.wen.android.mtabuscomparison.ui.stopmonitoring

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.material.snackbar.Snackbar
import com.wen.android.mtabuscomparison.R
import com.wen.android.mtabuscomparison.feature.stopmonitoring.MonitoringData
import com.wen.android.mtabuscomparison.feature.stopmonitoring.StopMonitoringListItem
import com.wen.android.mtabuscomparison.ui.commom.BaseObservableViewMvc

class StopMonitoringViewMvcImpl(
    private val inflater: LayoutInflater,
    parent: ViewGroup?,
    private val favoriteListener: StopMonitoringViewMvc.FavoriteListener
) :
    BaseObservableViewMvc<StopMonitoringViewMvc.Listener>(), StopMonitoringViewMvc {

    private lateinit var mAlertView: TextView
    private lateinit var mLastRefreshed: TextView
    private var mAdview: AdView
    private var mFavoriteButton: AppCompatImageView
    private var mFavorite: Boolean = false
    private val recyclerView: RecyclerView
    private val adapter: StopMonitoringAdapter
    private val appBar: Toolbar
    private val tooBar: Toolbar


    init {
        setRootView(inflater.inflate(R.layout.activity_stop_monitoring, parent, false))
        appBar = findViewById(R.id.stop_monitoring_app_bar)
        tooBar = findViewById(R.id.stop_monitoring_tool_bar)
//        mSwipeRefreshLayout = findViewById(R.id.stop_monitoring_swipe_refresh)
        mFavoriteButton = findViewById(R.id.stop_favorite)
        mFavoriteButton.setOnClickListener { favoriteListener.onClickedFavorite() }
        recyclerView = findViewById((R.id.stop_monitoring_recycler_view))
        adapter = StopMonitoringAdapter(emptyList())
        recyclerView.layoutManager = LinearLayoutManager(getContext())

        val adRequest = AdRequest.Builder().build()
        mAdview = (findViewById<AdView>(R.id.stop_monitoring_ad)).apply {
            loadAd(adRequest)
        }
//        mSwipeRefreshLayout.setOnRefreshListener {
//            for (listener in getListeners()) {
//                listener.onSwipeRefresh()
//            }
//        }
    }

    override fun setTitle(stopName: String) {
        appBar.title = stopName
    }

    override fun setRefreshing(refreshing: Boolean) {
//        mSwipeRefreshLayout.isRefreshing = refreshing
    }

    override fun setAdapterData(busMonitoring: List<StopMonitoringListItem>) {
        recyclerView.adapter = StopMonitoringAdapter(busMonitoring)
    }

    override fun checkError(monitoringData: MonitoringData) {
        if (monitoringData.situations != null) {
            tooBar.visibility = View.VISIBLE
            findViewById<ImageView>(R.id.stop_monitoring_alert_img).apply {
                visibility = View.VISIBLE
            }
            mAlertView = findViewById(R.id.stop_monitoring_stop_advisory)
            mAlertView.visibility = View.VISIBLE
            mAlertView.bringToFront()
            mAlertView.setOnClickListener {
                var alertSummary = ""
                for (situation in monitoringData.situations.PtSituationElement) {
                    alertSummary += situation.Description + "\n\n\n"
                }
                AlertDialog.Builder(getContext()).setMessage(alertSummary).create().show()
            }
        }
        if (monitoringData.errorMessage.isNotEmpty()) {
            Snackbar.make(
                findViewById(R.id.stop_monitoring_coordinator),
                monitoringData.errorMessage,
                Snackbar.LENGTH_LONG
            ).show()
        } else if (monitoringData.busMonitoring.isEmpty()) {
            Snackbar.make(
                findViewById(R.id.stop_monitoring_coordinator),
                "Sorry, MTA is not providing stop monitoring data for this stop right now:(",
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    override fun getFavorite(): Boolean = mFavorite
    override fun setFavorite(isFavorite: Boolean) {
        mFavorite = isFavorite
        when (mFavorite) {
            true -> mFavoriteButton.background =
                ContextCompat.getDrawable(getContext(), R.drawable.ic_favorite_pink_24dp)
            else -> mFavoriteButton.background =
                ContextCompat.getDrawable(getContext(), R.drawable.ic_favorite_border_pink_24dp)
        }
    }

    override fun onMvcViewResume() {
        mAdview.resume()
    }

    override fun onMvcViewPause() {
        mAdview.pause()
    }

    override fun onMvcViewDestroy() {
        mAdview.destroy()
    }
}