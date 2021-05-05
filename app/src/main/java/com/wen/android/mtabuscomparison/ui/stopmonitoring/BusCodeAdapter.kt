package com.wen.android.mtabuscomparison.ui.stopmonitoring

import android.view.ViewGroup
import com.wen.android.mtabuscomparison.R
import com.wen.android.mtabuscomparison.databinding.StopMonitoringBusCodeItemBinding
import com.wen.android.mtabuscomparison.util.lists.AbstractAdapter
import com.wen.android.mtabuscomparison.util.lists.BaseAdapter
import com.wen.android.mtabuscomparison.util.lists.BindableVH

class BusCodeAdapter(
    private val data: List<String>,
    private val onItemClicked: (item: String) -> Unit

) : AbstractAdapter<BusCodeAdapter.ViewHolder>() {

    override fun onCreateBaseVH(parent: ViewGroup, viewType: Int) = ViewHolder(parent)

    override fun onBindBaseVH(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }


    inner class ViewHolder(
        parent: ViewGroup
    ) : BaseAdapter.VH(R.layout.stop_monitoring_bus_code_item, parent),
        BindableVH<String, StopMonitoringBusCodeItemBinding> {

        override val viewBinding = lazy { StopMonitoringBusCodeItemBinding.bind(itemView) }

        override val onBindData: StopMonitoringBusCodeItemBinding.(
            item: String,
            payloads: List<Any>
        ) -> Unit = { initial, payloads ->
            val item = payloads.firstOrNull() as? String ?: initial
            stopMonitoringBusCode.apply {
                text = item
                setOnClickListener { onItemClicked(item) }
            }
        }
    }
}