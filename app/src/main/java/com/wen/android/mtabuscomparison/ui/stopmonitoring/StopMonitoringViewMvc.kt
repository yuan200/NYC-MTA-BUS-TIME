package com.wen.android.mtabuscomparison.ui.stopmonitoring

import com.wen.android.mtabuscomparison.feature.stop.MonitoringData
import com.wen.android.mtabuscomparison.feature.stop.StopMonitoringListItem
import com.wen.android.mtabuscomparison.ui.commom.ObservableViewMvc

interface StopMonitoringViewMvc: ObservableViewMvc<StopMonitoringViewMvc.Listener> {
    fun refreshMonitoringView(monitoringData: MonitoringData)
    fun setRefreshing(refreshing: Boolean)
    fun getFavorite(): Boolean
    fun setFavorite(isFavorite: Boolean)
    fun onResume()
    fun onPause()
    fun onDestroy()
    fun setAdapterData(busMonitoring: List<StopMonitoringListItem>)
    fun checkError(monitoringData: MonitoringData)

    interface Listener {
        fun onSwipeRefresh()
    }

    interface FavoriteListener {
        fun onClickedFavorite()
    }
}