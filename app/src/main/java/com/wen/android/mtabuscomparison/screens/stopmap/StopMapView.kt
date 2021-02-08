package com.wen.android.mtabuscomparison.screens.stopmap

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import com.wen.android.mtabuscomparison.screens.commom.IObservableView
import com.wen.android.mtabuscomparison.stop.StopInfo

interface StopMapView: IObservableView<StopMapView.Listener> {

    interface OnMovedMapListener {
        fun onMovedMap(latLng: LatLng)
    }

    interface Listener {
        fun onStopClicked(stop: StopInfo)
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
}