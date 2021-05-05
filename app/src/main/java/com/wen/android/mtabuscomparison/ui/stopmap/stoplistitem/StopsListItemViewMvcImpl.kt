package com.wen.android.mtabuscomparison.ui.stopmap.stoplistitem

import android.view.LayoutInflater
import android.view.ViewGroup
import com.wen.android.mtabuscomparison.R
import com.wen.android.mtabuscomparison.databinding.ListItemBusBinding
import com.wen.android.mtabuscomparison.feature.stopmonitoring.StopInfo
import com.wen.android.mtabuscomparison.ui.commom.BaseObservableViewMvc
import com.wen.android.mtabuscomparison.util.getColorFromAttr

class StopsListItemViewMvcImpl(inflater: LayoutInflater, parent: ViewGroup) :
    BaseObservableViewMvc<StopsListItemViewMvc.Listener>(), StopsListItemViewMvc {

    private lateinit var mStopInfo: StopInfo
    private var _binding: ListItemBusBinding? = null
    private val binding get() = _binding!!

    init {
        _binding = ListItemBusBinding.inflate(inflater, parent, false)
        setRootView(binding.root)

        binding.listItemCardview.setOnClickListener {
            for (listener in getListeners())
                listener.onStopClicked(mStopInfo)
        }
    }

    override fun bindStops(stopInfo: StopInfo) {
        mStopInfo = stopInfo
        binding.itemStopName.text = stopInfo.intersections
        binding.itemReadStopCode1.text = stopInfo.stopCode
        binding.itemRoutes.text = stopInfo.routes
    }

    override fun highlightSelected(isSelected: Boolean) {
        binding.listItemCardview.setBackgroundColor(
            if (isSelected) getContext().getColorFromAttr(R.attr.colorSecondary)
            else getContext().getColorFromAttr(R.attr.colorSurface)
        )
    }
}