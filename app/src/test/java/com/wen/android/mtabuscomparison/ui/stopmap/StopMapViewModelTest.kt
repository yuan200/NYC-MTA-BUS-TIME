package com.wen.android.mtabuscomparison.ui.stopmap

import android.location.Location
import app.cash.turbine.test
import com.wen.android.mtabuscomparison.common.Result
import com.wen.android.mtabuscomparison.feature.stopmap.LoadNearByStopUseCase
import com.wen.android.mtabuscomparison.feature.stopmap.LocationRepository
import com.wen.android.mtabuscomparison.feature.stopmonitoring.Stop
import com.wen.android.mtabuscomparison.testhelper.extensions.CoroutineTestRule
import com.wen.android.mtabuscomparison.testhelper.extensions.TestDispatcherProvider
import com.wen.android.mtabuscomparison.testhelper.extensions.runBlockingTest
import io.kotest.matchers.shouldBe
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.Rule
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.time.ExperimentalTime

@ExperimentalTime
class StopMapViewModelTest {

    @get:Rule
    var coroutineTestRule = CoroutineTestRule()

    lateinit var viewModel: StopMapViewModel

    @MockK
    lateinit var loadNearByUseCase: LoadNearByStopUseCase

    @MockK
    lateinit var locationRepository: LocationRepository

    lateinit var stops: List<Stop>

    @MockK(relaxed = true)
    lateinit var location: Location

//    val location = spyk(Location("test"), recordPrivateCalls = true)

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        Dispatchers.setMain(coroutineTestRule.testDispatcher)

        every { location.latitude } returns 40.744441
        every { location.longitude } returns -73.992744
        every { location.latitude = any() } just runs
        every { location.longitude = any() } just runs

        coEvery { locationRepository.locations } answers {
            flow { emit(location) }
        }
        coEvery { loadNearByUseCase(any()) } returns flow {
            emit(Result.Success(stops))
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
        viewModel = StopMapViewModel(
            loadNearByStopUseCase = loadNearByUseCase,
            locationRepository = locationRepository,
            dispatcherProvider = TestDispatcherProvider()
        )
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadNearByStop should update flow`() = coroutineTestRule.runBlockingTest {
        viewModel.apply {
            loadNearByStop(location)

            nearByStop.test {
                expectItem()[0].stopCode = "400007"
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `my location should return the current location`() = coroutineTestRule.runBlockingTest {
        viewModel.myLocation
        viewModel.apply {
            myLocation.test {
                expectItem().apply {
                    latitude shouldBe 40.744441
                    longitude shouldBe -73.992744
                }
            }

        }
    }

}