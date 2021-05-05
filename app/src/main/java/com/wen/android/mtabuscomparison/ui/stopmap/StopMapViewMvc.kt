package com.wen.android.mtabuscomparison.ui.stopmap

import android.location.Location
import android.widget.EditText
import com.google.android.gms.maps.model.LatLng
import com.wen.android.mtabuscomparison.feature.stopmonitoring.StopInfo
import com.wen.android.mtabuscomparison.ui.commom.ObservableViewMvc

interface StopMapViewMvc: ObservableViewMvc<StopMapViewMvc.Listener> {

    interface OnMovedMapListener {
        fun onMovedMap(latLng: LatLng)
    }

    interface Listener {
        fun onStopClicked(stop: StopInfo)
    }

    interface OnStartSearchListener {
        fun onStartSearch()
    }

    interface MapListener {
        fun onMapReady()
    }

    fun bindStopInfo(data: List<StopInfo>)

    fun registerMapListener(onMovedMapListener: OnMovedMapListener)

    fun unregisterMapListener(onMovedMapListener: OnMovedMapListener)

    fun scrollToStop(index: Int)

    fun addCurrentLocationMarker(location: Location)

    fun removeMarkers()

    fun addStopMarker(st: StopInfo)

    fun getFocusStop(): Int

    fun enableMyLocationButton()

    fun getSearchBar(): EditText

    fun moveCameraTo(latLng: LatLng)

    fun onDestroyView()
}