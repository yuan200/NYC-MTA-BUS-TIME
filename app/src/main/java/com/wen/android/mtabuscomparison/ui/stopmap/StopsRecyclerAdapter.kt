package com.wen.android.mtabuscomparison.ui.stopmap

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wen.android.mtabuscomparison.feature.stopmonitoring.StopInfo
import com.wen.android.mtabuscomparison.ui.stopmap.stoplistitem.StopsListItemViewMvc
import com.wen.android.mtabuscomparison.ui.stopmap.stoplistitem.StopsListItemViewMvcImpl
import java.lang.ref.WeakReference

class StopsRecyclerAdapter(var mStops: List<StopInfo>, private var view: StopMapFragment?,
                           private val mListener: Listener
)
    : RecyclerView.Adapter<StopsRecyclerAdapter.StopHolder>(),
        StopsListItemViewMvc.Listener {

    interface Listener {
        fun onStopClicked(stop: StopInfo)
    }

    private var weakFragment: WeakReference<StopMapFragment> = WeakReference(view)
    private var weakListener: WeakReference<Listener> = WeakReference(mListener)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StopHolder {
        val stopsListItemView = StopsListItemViewMvcImpl(LayoutInflater.from(parent.context), parent)
        stopsListItemView.registerListener(this)
        return StopHolder(stopsListItemView)
    }

    override fun onBindViewHolder(holder: StopHolder, position: Int) {
        holder.mView.bindStops(mStops[position])
        holder.mView.highlightSelected(weakFragment.get()?.getFocusStop() == position)
    }

    override fun getItemCount() = mStops.size

    override fun onStopClicked(stop: StopInfo) {
        weakListener.get()?.onStopClicked(stop)
    }

    class StopHolder(val mView: StopsListItemViewMvc) : RecyclerView.ViewHolder(mView.getRootView())
}