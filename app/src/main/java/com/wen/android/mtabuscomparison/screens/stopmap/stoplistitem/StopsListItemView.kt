package com.wen.android.mtabuscomparison.screens.stopmap.stoplistitem

import com.wen.android.mtabuscomparison.stop.StopInfo
import com.wen.android.mtabuscomparison.screens.commom.IObservableView

interface StopsListItemView : IObservableView<StopsListItemView.Listener> {

    interface Listener {
        fun onStopClicked(stopInfo: StopInfo)
    }

    fun bindStops(stopInfo: StopInfo)
}