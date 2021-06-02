package com.wen.android.mtabuscomparison.ui.stopmap

import android.location.Location
import androidx.lifecycle.viewModelScope
import com.wen.android.mtabuscomparison.common.Result
import com.wen.android.mtabuscomparison.feature.stopmap.LoadNearByStopUseCase
import com.wen.android.mtabuscomparison.feature.stopmonitoring.StopInfo
import com.wen.android.mtabuscomparison.util.coroutine.DispatcherProvider
import com.wen.android.mtabuscomparison.util.coroutine.cancelIfActive
import com.wen.android.mtabuscomparison.util.viewmodel.BusViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.cos

@HiltViewModel
class StopMapViewModel
@Inject constructor(
    private val loadNearByStopUseCase: LoadNearByStopUseCase,
    dispatcherProvider: DispatcherProvider
) : BusViewModel(dispatcherProvider) {

    private var _location: Location? = null

    private var loadNearByStopJob: Job? = null

    private val _nearByStop: MutableStateFlow<List<StopInfo>> = MutableStateFlow(emptyList())

    val nearByStop: StateFlow<List<StopInfo>> = _nearByStop.asStateFlow()

    fun loadNearByStop(location: Location) {
        _location = location
        val stopList = ArrayList<StopInfo>()
        loadNearByStopJob.cancelIfActive()
        loadNearByStopJob = viewModelScope.launch {
            loadNearByStopUseCase(getNearByRange()).collect {
                if (it is Result.Success) {
                    for ((stopId, stopName, stopLat, stopLon, routeId) in it.data) {
                        val stop = StopInfo()
                        val tempLocation = Location("tempLocation")
                        tempLocation.latitude = stopLat
                        tempLocation.longitude = stopLon
                        val distance = location.distanceTo(tempLocation)
                        stop.stopCode = stopId
                        stop.intersections = stopName
                        stop.routes = routeId
                        stop.location = tempLocation
                        stop.distance = distance
                        stopList.add(stop)
                        stopList.sort()
                    }
                    _nearByStop.value = stopList
                }
            }
        }
    }

    private fun getNearByRange(): NearByRange {
        val radiusInMeters = 800.0
        val radiusNeg = 0 - radiusInMeters
        val coefPlus = radiusInMeters * 0.0000089
        val coefNeg = radiusNeg * 0.0000089
        val newLatitude1: Double
        val newLatitude2: Double
        val newLongitude1: Double
        val newLongitude2: Double
        val currentLatitude = _location.orDummy.latitude
        val currentLongitude = _location.orDummy.longitude
        newLatitude1 = currentLatitude + coefNeg
        newLatitude2 = currentLatitude + coefPlus
        newLongitude1 = currentLongitude + coefPlus / cos(currentLatitude * 0.018)
        newLongitude2 = currentLongitude + coefNeg / cos(currentLatitude * 0.018)
        return NearByRange(newLatitude1, newLatitude2, newLongitude2, newLongitude1)
    }

}