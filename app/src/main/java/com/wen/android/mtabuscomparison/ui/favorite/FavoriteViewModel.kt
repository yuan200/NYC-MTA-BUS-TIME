package com.wen.android.mtabuscomparison.ui.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.wen.android.mtabuscomparison.common.Result
import com.wen.android.mtabuscomparison.feature.favorite.storage.repo.DefaultFavoriteRepository
import com.wen.android.mtabuscomparison.feature.stopmonitoring.storage.repo.StopMonitoringRepository
import com.wen.android.mtabuscomparison.util.viewmodel.BusViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel
@Inject constructor(
    private val stopMonitoringRepository: StopMonitoringRepository
) : BusViewModel() {

    private val _saveResult: MutableLiveData<String> = MutableLiveData()

    private val favoriteRepository = DefaultFavoriteRepository()

    private val _backdropOpened: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>().apply {
            value = false
        }
    }

    val backdropOpened: LiveData<Boolean> = _backdropOpened

    fun setBackdropOpened(isOpen: Boolean) {
        _backdropOpened.value = isOpen
    }

    val favoriteLiveData = favoriteRepository.favorites
        .asLiveData(Dispatchers.IO)

    val showEmptyFavorite: LiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        addSource(favoriteLiveData) {
            Timber.i(if (it.isNullOrEmpty()) "true" else "false")
            this.value = it.isNullOrEmpty()
        }
    }

    val saveResult: LiveData<String> = _saveResult

    fun resetSaveResult() {
        _saveResult.value = ""
    }

    fun onFetchingStopInfo(stopId: String, apiKey: String) {
        launch {
            stopMonitoringRepository.stopMonitoring(apiKey, stopId).collect {
                withContext(Dispatchers.Main) {
                    if (it is Result.Failure) {
                        _saveResult.value = "Error: " + it.msg
                    } else {
                        _saveResult.value = "OK"
                    }
                }
            }


        }
    }
}