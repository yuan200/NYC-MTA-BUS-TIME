package com.wen.android.mtabuscomparison.screens.stopmap

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wen.android.mtabuscomparison.screens.stopmap.stoplistitem.StopsListItemView
import com.wen.android.mtabuscomparison.screens.stopmap.stoplistitem.StopsListItemViewImpl
import com.wen.android.mtabuscomparison.stop.StopInfo

class StopsRecyclerAdapter(var mStops: List<StopInfo>, private var view: StopMapView?,
                           private val mListener: Listener
)
    : RecyclerView.Adapter<StopsRecyclerAdapter.StopHolder>(),
        StopsListItemView.Listener {

    interface Listener {
        fun onStopClicked(stop: StopInfo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StopHolder {
        val stopsListItemView = StopsListItemViewImpl(LayoutInflater.from(parent.context), parent)
        stopsListItemView.registerListener(this)
        return StopHolder(stopsListItemView)
    }

    override fun onBindViewHolder(holder: StopHolder, position: Int) {
        holder.mView.bindStops(mStops[position])
        holder.itemView.setBackgroundColor(if (view?.getFocusStop() == position) Color.YELLOW else Color.TRANSPARENT)
    }

    override fun getItemCount() = mStops.size

    override fun onStopClicked(stop: StopInfo) {
        mListener.onStopClicked(stop)
    }

    class StopHolder(val mView: StopsListItemView) : RecyclerView.ViewHolder(mView.getRootView())
}