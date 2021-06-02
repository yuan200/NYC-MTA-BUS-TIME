package com.wen.android.mtabuscomparison.ui.stopmap

import android.location.Location
import app.cash.turbine.test
import com.wen.android.mtabuscomparison.common.Result
import com.wen.android.mtabuscomparison.feature.stopmap.LoadNearByStopUseCase
import com.wen.android.mtabuscomparison.feature.stopmonitoring.Stop
import com.wen.android.mtabuscomparison.testhelper.extensions.CoroutineTestRule
import com.wen.android.mtabuscomparison.testhelper.extensions.TestDispatcherProvider
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Rule
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.time.ExperimentalTime

@ExperimentalTime
internal class StopMapViewModelTest {

    @get:Rule
    var coroutineTestRule = CoroutineTestRule()

    lateinit var viewModel: StopMapViewModel

    @MockK
    lateinit var loadNearByUseCase: LoadNearByStopUseCase

    lateinit var stops: List<Stop>

    lateinit var location: Location

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        viewModel = StopMapViewModel(loadNearByUseCase, TestDispatcherProvider())

        location = Location("test").apply {
            latitude = 40.744441
            longitude = -73.992744
        }
        stops = listOf(
            Stop(
                stopId = "400007",
                stopName = "PARK AV S/E 21 ST",
                stopLat = 40.738976,
                stopLon = -73.987053,
                routeId = "M2 M3 M1"
            )
        )
        coEvery { loadNearByUseCase(any()) } returns flow { emit(Result.Success(stops)) }
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    fun `loadNearByStop should update flow`() = coroutineTestRule.testDispatcher.runBlockingTest {
        viewModel.apply {
            loadNearByStop(location)

            nearByStop.test {
                expectItem()[0].stopCode = "400007"
                cancelAndIgnoreRemainingEvents()
            }
        }

    }

}