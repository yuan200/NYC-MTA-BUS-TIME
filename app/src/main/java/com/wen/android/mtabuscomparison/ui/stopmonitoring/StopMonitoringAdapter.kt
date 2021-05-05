package com.wen.android.mtabuscomparison.ui.stopmonitoring

import android.view.ViewGroup
import com.wen.android.mtabuscomparison.feature.stopmonitoring.StopMonitoringListItem
import com.wen.android.mtabuscomparison.util.lists.AbstractAdapter

class StopMonitoringAdapter(var data: List<StopMonitoringListItem>) :
    AbstractAdapter<StopMonitoringViewHolder>() {

    override fun getItemCount(): Int {
        return data.size
    }

    fun updateData(data: List<StopMonitoringListItem>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun onCreateBaseVH(parent: ViewGroup, viewType: Int) = StopMonitoringViewHolder(parent)

    override fun onBindBaseVH(holder: StopMonitoringViewHolder, position: Int, payloads: MutableList<Any>) {
        holder.bind(data[position])
    }
}