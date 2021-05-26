package com.wen.android.mtabuscomparison.ui.stopmonitoring

import com.wen.android.mtabuscomparison.feature.stopmonitoring.StopMonitoringData
import com.wen.android.mtabuscomparison.feature.stopmonitoring.StopMonitoringListItem
import com.wen.android.mtabuscomparison.ui.commom.ObservableViewMvc

interface StopMonitoringViewMvc: ObservableViewMvc<StopMonitoringViewMvc.Listener> {
    fun setRefreshing(refreshing: Boolean)
    fun getFavorite(): Boolean
    fun setFavorite(isFavorite: Boolean)
    fun onMvcViewResume()
    fun onMvcViewPause()
    fun onMvcViewDestroy()
    fun setAdapterData(busMonitoring: List<StopMonitoringListItem>)
    fun checkError(stopMonitoringData: StopMonitoringData)
    fun setTitle(stopName: String)

    interface Listener {
        fun onSwipeRefresh()
    }

    interface FavoriteListener {
        fun onClickedFavorite()
    }
}