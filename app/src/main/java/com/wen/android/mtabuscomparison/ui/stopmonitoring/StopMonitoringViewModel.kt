package com.wen.android.mtabuscomparison.ui.stopmonitoring

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.wen.android.mtabuscomparison.common.Result
import com.wen.android.mtabuscomparison.feature.stopmonitoring.Stop
import com.wen.android.mtabuscomparison.feature.stopmonitoring.StopMonitoringData
import com.wen.android.mtabuscomparison.feature.stopmonitoring.storage.repo.StopMonitoringRepository
import com.wen.android.mtabuscomparison.util.coroutine.DispatcherProvider
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
    dispatcherProvider: DispatcherProvider,
    private val repository: StopMonitoringRepository,
    savedStateHandle: SavedStateHandle
) : BusViewModel(dispatcherProvider = dispatcherProvider) {
    /**
     * what do I need in this view model
     * + stopId -- getting from saveStateHandle
     *  ++ stop coordinates  -- local repository
     *  ++ stop monitoring  -- network repository
     *   +++ vehicle coordinates  -- from stop monitoring
     *   +++ bus number -- from stop monitoring, network repository
     *   +++ selectedPublishedLineName -- the current selected bus line, user trigger event
     */

    private val vehicleLocations: MutableMap<String, LatLng> = Collections.synchronizedMap(HashMap())

    //todo handle case when stop is not in local db
    val stop: StateFlow<Stop?> by lazy {
        repository.stop(savedStateHandle["stopId"]!!)
            .filterNotNull()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = null
            )
    }

    private val _targetPublishedLineName: MutableStateFlow<String> = MutableStateFlow("")
    val targetPublishedLineName: StateFlow<String> = _targetPublishedLineName

    private val _targetVehicleLocation: MutableSharedFlow<LatLng> = MutableSharedFlow()
    val targetVehicleLocation: SharedFlow<LatLng> = _targetVehicleLocation

    private val _vehicleAndStopBounds: MutableSharedFlow<LatLngBounds> = MutableSharedFlow()
    val vehicleAndStopBounds: SharedFlow<LatLngBounds> = _vehicleAndStopBounds


    private val _stopStopMonitoringData: MutableStateFlow<Result<StopMonitoringData>> = MutableStateFlow(Result.Loading)
    val stopStopMonitoringData: StateFlow<Result<StopMonitoringData>> = _stopStopMonitoringData


    private val _publishedLineAdapterData: MutableStateFlow<MutableList<String>> = MutableStateFlow(mutableListOf())
    val publishedLineAdapterData: StateFlow<MutableList<String>> = _publishedLineAdapterData

    fun loadStopMonitoringData(key: String, stopId: String) {
        launch {
            while (true) {
                fetchStopMonitoring(key, stopId)
                delay(15 * 1000)
            }
        }
    }

    private suspend fun fetchStopMonitoring(key: String, stopId: String) {
        repository.stopMonitoring(key, stopId)
            .onEach {
                Timber.v("emit $it")
                println("emit $it")
                _stopStopMonitoringData.emit(it)
                if (it is Result.Success) {
                    handleStopMonitoring(it.data)
                }
            }.launchInViewModel()

    }

    /**
     * 1. emit publishedLineAdapterData to view
     * 2. emit vehicle coordinate to view
     */
    private fun handleStopMonitoring(stopMonitoringData: StopMonitoringData) {
        vehicleLocations.clear()
        val publishedLineList = mutableListOf<String>()
        for (item in stopMonitoringData.busMonitoring) {
            publishedLineList.add(item.publishedLineName)
            if (item.vehicleLocation != null) {
                vehicleLocations[item.publishedLineName] = LatLng(
                    item.vehicleLocation.Latitude, item.vehicleLocation.Longitude
                )
            }
        }
        _publishedLineAdapterData.value = publishedLineList

        if (_targetPublishedLineName.value.isEmpty() && stopMonitoringData.busMonitoring.isNotEmpty()) {
            stopMonitoringData.busMonitoring[0].vehicleLocation?.let {
                viewModelScope.launch {
                    _targetVehicleLocation.emit(LatLng(it.Latitude, it.Longitude))
                }
            }
        } else {
            viewModelScope.launch {
                vehicleLocations[_targetPublishedLineName.value]?.let {
                    _targetVehicleLocation.emit(it)
                }
            }
        }
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

        viewModelScope.launch {
            _vehicleAndStopBounds
                .emit(
                    createLatLngBound(vehicleLocations[busCode], stop.value)
                )
        }
    }

    /**
     * LatLngBounds can't have all points null!
     */
    private fun createLatLngBound(vehicleLatLng: LatLng?, stop: Stop?): LatLngBounds {
        val latLngBuilder = LatLngBounds.Builder()
        if (stop != null) {
            val stopLatLng = LatLng(stop.stopLat, stop.stopLon)
            latLngBuilder.include(stopLatLng)
        }
        if (vehicleLatLng != null) latLngBuilder.include(vehicleLatLng)
        return latLngBuilder.build()
    }
}