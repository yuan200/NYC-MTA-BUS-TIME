package com.wen.android.mtabuscomparison.ui.stopmonitoring

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.material.snackbar.Snackbar
import com.wen.android.mtabuscomparison.R
import com.wen.android.mtabuscomparison.feature.stop.MonitoringData
import com.wen.android.mtabuscomparison.feature.stop.StopMonitoringListItem
import com.wen.android.mtabuscomparison.ui.commom.BaseObservableViewMvc
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel

class StopMonitoringViewMvcImpl(
    private val inflater: LayoutInflater,
    parent: ViewGroup?,
    private val favoriteListener: StopMonitoringViewMvc.FavoriteListener
) :
    BaseObservableViewMvc<StopMonitoringViewMvc.Listener>(), StopMonitoringViewMvc {


    //    private val mMonitoringViewContainer: LinearLayout
    private val mSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var mAlertView: TextView
    private lateinit var mLastRefreshed: TextView
    private var mAdview: AdView
    private val mViewMap: MutableMap<String, LinearLayout> = mutableMapOf()
    private var mFavoriteButton: AppCompatImageView
    private var mFavorite: Boolean = false
    private lateinit var mStopMonitoringLayout: ConstraintLayout
    private val uiScope: CoroutineScope
    private val recyclerView: RecyclerView
    private val adapter: StopMonitoringAdapter


    init {
        setRootView(inflater.inflate(R.layout.activity_stop_monitoring, parent, false))
        mStopMonitoringLayout = findViewById(R.id.stop_monitoring_layout)
//        mMonitoringViewContainer = findViewById(R.id.stop_monitoring_view_container)
        mSwipeRefreshLayout = findViewById(R.id.stop_monitoring_swipe_refresh)
        mFavoriteButton = findViewById(R.id.stop_favorite)
        mFavoriteButton.setOnClickListener { favoriteListener.onClickedFavorite() }
        uiScope = CoroutineScope(Job() + Dispatchers.IO)
        recyclerView = findViewById((R.id.stop_monitoring_recycler_view))
        adapter = StopMonitoringAdapter(emptyList())
        recyclerView.layoutManager = LinearLayoutManager(getContext())

//        mAdview = AdView(getContext()).apply {
//            layoutParams = ConstraintLayout.LayoutParams(
//                ConstraintLayout.LayoutParams.MATCH_PARENT,
//                ConstraintLayout.LayoutParams.WRAP_CONTENT
//            )
//        }
//        val adUnitFlow = getContext().adUnitDataStore.data.map { adUnits ->
//            adUnits.adUnitsList
//        }
        val adRequest = AdRequest.Builder().build()
        mAdview = (findViewById<AdView>(R.id.stop_monitoring_ad)).apply {
            loadAd(adRequest)
        }

//        uiScope.launch {
//            adUnitFlow
//                .take(1)
//                .collect {
//                    if (it.isNotEmpty()) {
//                        val adUnit = it.singleOrNull {
//                            it.location == getString(R.string.ad_unit_location_stop_monitoring)
//                        }
//                        if (adUnit != null) {
//                            if (adUnit.enabled) {
//                                withContext(Dispatchers.Main) {
//                                    mAdview.id = View.generateViewId()
//                                    mStopMonitoringLayout.addView(mAdview)
//                                    val constraintSet = ConstraintSet()
//                                    constraintSet.clone(mStopMonitoringLayout)
//                                    constraintSet.connect(
//                                        mAdview.id,
//                                        ConstraintSet.TOP,
//                                        R.id.stop_monitoring_swipe_refresh,
//                                        ConstraintSet.BOTTOM
//                                    )
//                                    constraintSet.connect(
//                                        mAdview.id,
//                                        ConstraintSet.BOTTOM,
//                                        ConstraintSet.PARENT_ID,
//                                        ConstraintSet.BOTTOM
//                                    )
//                                    constraintSet.applyTo(mStopMonitoringLayout)
//                                    mAdview.adSize = AdSize.BANNER
//                                    mAdview.adUnitId = adUnit.adUnitId
//                                    mAdview.loadAd(adRequest)
//                                }
//                            }
//                        }
//                    } else {
//                        withContext(Dispatchers.Main) {
//                            mAdview.id = View.generateViewId()
//                            mStopMonitoringLayout.addView(mAdview)
//                            val constraintSet = ConstraintSet()
//                            constraintSet.clone(mStopMonitoringLayout)
//                            constraintSet.connect(
//                                mAdview.id,
//                                ConstraintSet.TOP,
//                                R.id.stop_monitoring_swipe_refresh,
//                                ConstraintSet.BOTTOM
//                            )
//                            constraintSet.connect(
//                                mAdview.id,
//                                ConstraintSet.BOTTOM,
//                                ConstraintSet.PARENT_ID,
//                                ConstraintSet.BOTTOM
//                            )
//                            constraintSet.applyTo(mStopMonitoringLayout)
//                            mAdview.adSize = AdSize.BANNER
//                            mAdview.adUnitId = getString(R.string.app_unit_id_stop_map_top)
//                            mAdview.loadAd(adRequest)
//                        }
//                    }
//                }
//        }

        mSwipeRefreshLayout.setOnRefreshListener {
            for (listener in getListeners()) {
                listener.onSwipeRefresh()
            }
        }
    }

    override fun refreshMonitoringView(monitoringData: MonitoringData) {
//        val mStopsContainer: LinearLayout =
//            if (mViewMap.containsKey(monitoringData.stopId)) {
//                mViewMap[monitoringData.stopId]!!
//            } else {
//                (inflater.inflate(
//                    R.layout.view_stop_monitoring_group,
//                    mMonitoringViewContainer,
//                    false
//                ) as LinearLayout).apply {
//                    mViewMap[monitoringData.stopId] = this
//                    mMonitoringViewContainer.addView(this)
//                }
////                LinearLayout(getContext()).apply {
////                    layoutParams = LinearLayout.LayoutParams(
////                        LinearLayout.LayoutParams.MATCH_PARENT,
////                        LinearLayout.LayoutParams.WRAP_CONTENT
////                    )
////                    orientation = LinearLayout.VERTICAL
////                    mViewMap[monitoringData.stopId] = this
////                    mMonitoringViewContainer.addView(this)
////                }
//            }
////        mStopsContainer.removeAllViews()
//
//        val groupContainer = mStopsContainer.findViewById<LinearLayout>(R.id.group_stop_monitoring)
//        if (monitoringData.situations != null) {
//            findViewById<ImageView>(R.id.group_alert_img).apply {
//                visibility = View.VISIBLE
//            }
//            mAlertView = findViewById(R.id.group_stop_advisory)
//            mAlertView.visibility = View.VISIBLE
//            mAlertView.bringToFront()
//            mAlertView.setOnClickListener {
//                var alertSummary = ""
//                for (situation in monitoringData.situations.PtSituationElement) {
//                    alertSummary += situation.Description + "\n\n\n"
//                }
//                AlertDialog.Builder(getContext()).setMessage(alertSummary).create().show()
//            }
//        }
//
//        if (monitoringData.errorMessage.isNotEmpty()) {
//            Snackbar.make(
//                findViewById(R.id.stop_monitoring_coordinator),
//                monitoringData.errorMessage,
//                Snackbar.LENGTH_SHORT
//            ).show()
//        } else if (monitoringData.busMonitoring.isEmpty()) {
//            Snackbar.make(
//                findViewById(R.id.stop_monitoring_coordinator),
//                "Sorry, MTA is not providing stop monitoring data for this stop right now:(",
//                Snackbar.LENGTH_SHORT
//            ).show()
//        } else {
//            val format = SimpleDateFormat("hh:mm:ss")
//            mLastRefreshed = findViewById(R.id.last_refreshed)
//            mLastRefreshed.text = "Last refreshed at: " + format.format(Date())
//        }
//        var i = 0
//        for (timeInfo in monitoringData.busMonitoring) {
//            if (i == 0) {
//                groupContainer.removeAllViews()
//                mStopsContainer.findViewById<TextView>(R.id.group_stop_name).apply {
//                    text = timeInfo.stopPointName
//                }
//            }
//            i += 1
//            val view = inflater.inflate(
//                R.layout.view_stop_monitoring_card,
//                mMonitoringViewContainer, false
//            )
//
//            val view1 = view.findViewById<TextView>(R.id.published_line).apply {
//                text = timeInfo.publishedLineName
//            }
//            view.findViewById<TextView>(R.id.live_minute).apply {
//                text = timeInfo.expectedArrivalTime.getMinutesFromNow()
//
//
//            }
//            view.findViewById<MySignalIcon>(R.id.stop_monitoring_signal).apply {
//                startAnim()
//            }
//            view.findViewById<TextView>(R.id.destination_name).apply {
//                text = timeInfo.destinationName
//            }
//            view.findViewById<TextView>(R.id.expected_arrive_time).apply {
//                text =
//                    "arrival time: ${timeInfo.expectedArrivalTime.getTime()}"
//            }
//            view.findViewById<TextView>(R.id.presentable_distance).apply {
//                text = timeInfo.presentableDistance
//                visibility =
//                    if (timeInfo.presentableDistance.isNullOrEmpty()) View.GONE else View.VISIBLE
//
//            }
//            view.findViewById<TextView>(R.id.arrival_proximity).apply {
//                text = timeInfo.arrivalProximityText
//                visibility =
//                    if (timeInfo.arrivalProximityText.isNullOrEmpty()) View.GONE else View.VISIBLE
//            }
//
//            view.findViewById<TextView>(R.id.next_bus_time).apply {
//                var time = "Next at: "
//                for (bustTime in timeInfo.nextBusTime) {
//                    time += bustTime.getTime() + "  "
//                }
//                text = time
//                visibility = if (timeInfo.nextBusTime.isEmpty()) View.GONE else View.VISIBLE
//            }
//
//            groupContainer.addView(view)
////            mStopsContainer.addView(view)
////            mMonitoringViewContainer.addView(linearLayout)
//        }

    }

    override fun setRefreshing(refreshing: Boolean) {
        mSwipeRefreshLayout.isRefreshing = refreshing
    }

    override fun setAdapterData(busMonitoring: List<StopMonitoringListItem>) {
        recyclerView.adapter = StopMonitoringAdapter(busMonitoring)
    }

    override fun checkError(monitoringData: MonitoringData) {
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

    override fun onResume() {
        mAdview.resume()
    }

    override fun onPause() {
        mAdview.pause()
    }

    override fun onDestroy() {
        uiScope.cancel()
        mAdview.destroy()
    }
}