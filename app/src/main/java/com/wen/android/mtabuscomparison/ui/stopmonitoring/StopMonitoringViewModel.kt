package com.wen.android.mtabuscomparison.ui.stopmonitoring

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.wen.android.mtabuscomparison.common.Result
import com.wen.android.mtabuscomparison.feature.stopmonitoring.MonitoringData
import com.wen.android.mtabuscomparison.feature.stopmonitoring.Stop
import com.wen.android.mtabuscomparison.feature.stopmonitoring.storage.repo.StopMonitoringRepository
import com.wen.android.mtabuscomparison.util.viewmodel.BusViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@HiltViewModel
class StopMonitoringViewModel
@Inject constructor(
    private val repository: StopMonitoringRepository,
    savedStateHandle: SavedStateHandle
) : BusViewModel() {
    /**
     * what do I need in this view model
     * + stopId -- getting from saveStateHandle
     *  ++ stop coordinates  -- local repository
     *  ++ stop monitoring  -- network repository
     *   +++ vehicle coordinates  -- from stop monitoring
     *   +++ bus number -- network repository
     *   +++ selectedPublishedLineName -- the current selected bus line, user trigger event
     */

    init {
        launch {
            //todo handle case when stop in not in local db
            stop = repository.stop(savedStateHandle["stopId"]!!)
                .filterNotNull()
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(),
                    initialValue = null
                )
        }
    }

    private val vehicleLocations: MutableMap<String, LatLng> = Collections.synchronizedMap(HashMap())

    lateinit var stop: StateFlow<Stop?>

    private val _targetPublishedLineName: MutableStateFlow<String> = MutableStateFlow("")
    val targetPublishedLineName: StateFlow<String> = _targetPublishedLineName

    private val _targetVehicleLocation: MutableSharedFlow<LatLng> = MutableSharedFlow()
    val targetVehicleLocation: SharedFlow<LatLng> = _targetVehicleLocation

    private val _vehicleAndStopBounds: MutableSharedFlow<LatLngBounds> = MutableSharedFlow()
    val vehicleAndStopBounds: SharedFlow<LatLngBounds> = _vehicleAndStopBounds


    private val _stopMonitoringData: MutableSharedFlow<Result<MonitoringData>> = MutableSharedFlow()
    val stopMonitoringData: SharedFlow<Result<MonitoringData>> = _stopMonitoringData


    private val _publishLineNames: MutableStateFlow<MutableList<String>> = MutableStateFlow(mutableListOf())
    val publishLineNames: StateFlow<MutableList<String>> = _publishLineNames

    fun loadStopMonitoringData(key: String, stopId: String) {
        viewModelScope.launch {
            while (true) {
                fetchStopMonitoringData(key, stopId)
                delay(15 * 1000)
            }
        }
    }

    private suspend fun fetchStopMonitoringData(key: String, stopId: String) {
        _stopMonitoringData.emit(Result.Loading)
        vehicleLocations.clear()
        val monitoringData = repository.stopMonitoring(key, stopId)
        _stopMonitoringData.emit(Result.Success(monitoringData))
        val busList = mutableListOf<String>()
        for (item in monitoringData.busMonitoring) {
            busList.add(item.publishedLineName)
            if (item.vehicleLocation != null) {
                vehicleLocations[item.publishedLineName] = LatLng(
                    item.vehicleLocation.Latitude, item.vehicleLocation.Longitude
                )
            }
        }
        _publishLineNames.value = busList
        if (_targetPublishedLineName.value.isEmpty() && monitoringData.busMonitoring.isNotEmpty()) {
            monitoringData.busMonitoring[0].vehicleLocation.let {
                viewModelScope.launch {
                    _targetVehicleLocation.emit(LatLng(it.Latitude, it.Longitude))
                }
            }
        } else {
            viewModelScope.launch {
                vehicleLocations[_targetPublishedLineName.value]?.let { _targetVehicleLocation.emit(it) }
            }
        }
        Timber.i("bus code: ${_publishLineNames.value.toString()}")
    }

    /**
     *  0. get target publish line
     *  1. show the target vehicle location marker on map
     *  2. create a latLngBound with stop and target vehicle location
     */
    fun onPublishLineClicked(busCode: String) {
        _targetPublishedLineName.value = busCode

        viewModelScope.launch {
            vehicleLocations[busCode]?.let { _targetVehicleLocation.emit(it) }
        }

        //todo _selectedBusLocation is null and cause crash
        //https://console.firebase.google.com/u/0/project/mtabuscomparison/crashlytics/app/android:com.wen.android.mtabuscomparison/issues/e9564cd816a405bc2177a36f9633458b?time=last-seven-days&sessionEventKey=60A190A101BF000115498963A681D980_1541766486889509292
        viewModelScope.launch {
            _vehicleAndStopBounds.emit(
                createLatLngBound(vehicleLocations[busCode], stop.value)
            )
        }
    }

    //todo test what happen if both are null, will it crash?
    private fun createLatLngBound(busLatLng: LatLng?, stop: Stop?): LatLngBounds {
        val latLngBuilder = LatLngBounds.Builder()
        if (stop != null) {
            val stopLatLng = LatLng(stop.stopLat, stop.stopLon)
            latLngBuilder.include(stopLatLng)
        }
        if (busLatLng != null) latLngBuilder.include(busLatLng)
        return latLngBuilder.build()
    }
}