package com.wen.android.mtabuscomparison.feature.stopmap

import com.wen.android.mtabuscomparison.common.FlowUseCase
import com.wen.android.mtabuscomparison.common.Result
import com.wen.android.mtabuscomparison.feature.stopmonitoring.Stop
import com.wen.android.mtabuscomparison.injection.IoDispatcher
import com.wen.android.mtabuscomparison.ui.stopmap.NearByRange
import com.wen.android.mtabuscomparison.ui.stopmap.NearByRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LoadNearByStopUseCase
@Inject constructor(
    private val nearByRepository: NearByRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : FlowUseCase<NearByRange, List<Stop>>(dispatcher) {
    override fun execute(parameters: NearByRange): Flow<Result<List<Stop>>> {
        val (latitude1, longitude1, latitude2, longitude2) = parameters
        return nearByRepository.getNearByStops(latitude1, longitude1, latitude2, longitude2).map {
            Result.Success(it)
        }
    }

}