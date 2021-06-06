package com.wen.android.mtabuscomparison.ui.stopmap

import androidx.annotation.RawRes
import androidx.databinding.BindingAdapter
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.MapStyleOptions

@BindingAdapter("mapStyle")
fun mapStyle(mapView: MapView, @RawRes resId: Int) {
    if (resId != 0) {
        mapView.getMapAsync { map ->
            map.setMapStyle(MapStyleOptions.loadRawResourceStyle(mapView.context, resId))
        }
    }
}

@BindingAdapter("isMapToolbarEnabled")
fun isMapToolbarEnabled(mapView: MapView, isMapToolBarEnabled: Boolean?) {
    if (isMapToolBarEnabled != null) {
        mapView.getMapAsync {
            it.uiSettings.isMapToolbarEnabled = isMapToolBarEnabled
        }
    }
}