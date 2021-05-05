package com.wen.android.mtabuscomparison.ui.stopmap.stoplistitem

import com.wen.android.mtabuscomparison.feature.stopmonitoring.StopInfo
import com.wen.android.mtabuscomparison.ui.commom.ObservableViewMvc

interface StopsListItemViewMvc : ObservableViewMvc<StopsListItemViewMvc.Listener> {

    interface Listener {
        fun onStopClicked(stopInfo: StopInfo)
    }

    fun bindStops(stopInfo: StopInfo)

    fun highlightSelected(isSelected: Boolean)
}