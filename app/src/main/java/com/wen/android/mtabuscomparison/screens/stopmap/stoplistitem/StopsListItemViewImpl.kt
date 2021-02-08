package com.wen.android.mtabuscomparison.screens.stopmap.stoplistitem

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.wen.android.mtabuscomparison.R
import com.wen.android.mtabuscomparison.stop.StopInfo
import com.wen.android.mtabuscomparison.screens.commom.BaseObservableView

class StopsListItemViewImpl(inflater: LayoutInflater, parent: ViewGroup) :
        BaseObservableView<StopsListItemView.Listener>(), StopsListItemView {

    private var mStopCodeView: TextView
    private var mStopNameView: TextView
    private var mStopRouteView: TextView
    private lateinit var mStopInfo: StopInfo

    init {
        setRootView(inflater.inflate(R.layout.list_item_bus, parent, false))
        mStopCodeView = findViewById(R.id.item_read_stop_code_1)
        mStopNameView = findViewById(R.id.item_read_group)
        mStopRouteView = findViewById(R.id.item_routes)

        getRootView().setOnClickListener {
            for (listener in getListeners())
                listener.onStopClicked(mStopInfo)
        }
    }

    override fun bindStops(stopInfo: StopInfo) {
        mStopInfo = stopInfo
        mStopNameView.text = stopInfo.intersections
        mStopCodeView.text = stopInfo.stopCode
        mStopRouteView.text = stopInfo.routes
    }
}