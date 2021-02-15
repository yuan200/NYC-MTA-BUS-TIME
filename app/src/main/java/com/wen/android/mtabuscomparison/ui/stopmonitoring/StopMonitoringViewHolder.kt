package com.wen.android.mtabuscomparison.ui.stopmonitoring

import android.view.View
import android.view.ViewGroup
import com.wen.android.mtabuscomparison.R
import com.wen.android.mtabuscomparison.databinding.StopMonitoringListItemBinding
import com.wen.android.mtabuscomparison.feature.stop.StopMonitoringListItem
import com.wen.android.mtabuscomparison.util.getMinutesFromNow
import com.wen.android.mtabuscomparison.util.getTime
import com.wen.android.mtabuscomparison.util.lists.BaseAdapter
import com.wen.android.mtabuscomparison.util.lists.BindableVH

class StopMonitoringViewHolder(
    parent: ViewGroup
) : BaseAdapter.VH(R.layout.stop_monitoring_list_item, parent),
    BindableVH<StopMonitoringListItem, StopMonitoringListItemBinding> {

    override val viewBinding = lazy { StopMonitoringListItemBinding.bind(itemView) }

    override val onBindData: StopMonitoringListItemBinding.(
        item: StopMonitoringListItem,
        changes: List<Any>
    ) -> Unit = { initial, changes ->
        val item = changes.firstOrNull() as? StopMonitoringListItem ?: initial

        publishedLine.apply {
            text = item.publishedLineName
        }

        liveMinute.apply {
            text = item.expectedArrivalTime.getMinutesFromNow()
        }

        stopMonitoringSignal.apply {
            startAnim()
        }

        destinationName.apply {
            text = "arrival time: ${item.expectedArrivalTime.getTime()}"
        }

        presentableDistance.apply {
            text = item.presentableDistance
            visibility = if (item.presentableDistance.isNullOrEmpty()) View.GONE else View.VISIBLE
        }

        arrivalProximity.apply {
            text = item.arrivalProximityText
            visibility = if (item.arrivalProximityText.isNullOrEmpty()) View.GONE else View.VISIBLE
        }

        nextBusTime.apply {
            var time = "Next at: "
            for (bustTime in item.nextBusTime) {
                time += bustTime.getTime() + " "
            }
            text = time
            visibility = if (item.nextBusTime.isEmpty()) View.GONE else View.VISIBLE
        }

    }
}