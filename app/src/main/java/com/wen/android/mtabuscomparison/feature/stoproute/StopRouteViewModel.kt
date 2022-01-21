package com.wen.android.mtabuscomparison.feature.stoproute

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wen.android.mtabuscomparison.common.Result
import com.wen.android.mtabuscomparison.feature.stopmonitoring.BusDirection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StopRouteViewModel
@Inject constructor(private val stopRouteRepository: StopRouteRepository) : ViewModel() {

    val busDirection: MutableState<BusDirection?> = mutableStateOf(null)

    val direction: MutableState<Int> = mutableStateOf(0)

    val errorMessage: MutableState<String> = mutableStateOf("")

    fun getStopRoute(route: String, key: String) {
        viewModelScope.launch {
            val route = stopRouteRepository.getStopRoute(route, key)
            when (route) {
                is Result.Success -> {
                    busDirection.value = route.data
                }
                is Result.Failure -> {
                    errorMessage.value = route.msg
                }
                Result.Loading -> {

                }
            }
        }
    }
}