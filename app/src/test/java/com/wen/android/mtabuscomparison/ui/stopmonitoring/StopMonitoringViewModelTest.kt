package com.wen.android.mtabuscomparison.ui.stopmonitoring

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.wen.android.mtabuscomparison.common.Result
import com.wen.android.mtabuscomparison.data.remote.bustime.VehicleLocation
import com.wen.android.mtabuscomparison.feature.stopmonitoring.Stop
import com.wen.android.mtabuscomparison.feature.stopmonitoring.StopMonitoringData
import com.wen.android.mtabuscomparison.feature.stopmonitoring.StopMonitoringListItem
import com.wen.android.mtabuscomparison.feature.stopmonitoring.storage.repo.StopMonitoringRepository
import com.wen.android.mtabuscomparison.testhelper.extensions.CoroutineTestRule
import com.wen.android.mtabuscomparison.testhelper.extensions.TestDispatcherProvider
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.beInstanceOf
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.Rule
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.time.ExperimentalTime

@ExperimentalTime
internal class StopMonitoringViewModelTest {

    @get:Rule
    var coroutineTestResult = CoroutineTestRule()

    @MockK
    lateinit var viewModel: StopMonitoringViewModel

    @MockK
    lateinit var repository: StopMonitoringRepository

    @MockK
    lateinit var savedStateHandle: SavedStateHandle

    lateinit var stop: Stop

    @MockK
    lateinit var apiResult: StopMonitoringData

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        Dispatchers.setMain(coroutineTestResult.testDispatcher)

        stop = Stop("400555", "E 32 ST/5 AV", 0.0, 0.0, "routeId")

        every { apiResult.busMonitoring } returns listOf(StopMonitoringListItem().apply {
            publishedLineName = "Q18"
            vehicleLocation = VehicleLocation(0.0, 0.0)
        })
        every { savedStateHandle.get(any()) as String? } returns ""
        coEvery { repository.stopMonitoring(any(), "400555") } returns flow {
            emit(Result.Success(apiResult))
        }
        coEvery { repository.stop(any()) } returns flow { emit(stop) }

        viewModel = StopMonitoringViewModel(
            TestDispatcherProvider(),
            repository,
            savedStateHandle
        )
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `stop flow should return data from repository`() = coroutineTestResult.testDispatcher.runBlockingTest {

        viewModel.apply {
            stop.test {
                expectItem()?.stopId shouldBe "400555"
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `load stop monitoring data should update flow`() = coroutineTestResult.testDispatcher.runBlockingTest {
        viewModel.apply {
            loadStopMonitoringData("key", "400555")

            stopStopMonitoringData.test {
                expectItem() should beInstanceOf<Result.Success<StopMonitoringData>>()
                cancelAndIgnoreRemainingEvents()
            }

            publishedLineAdapterData.test {
                expectItem() shouldBe listOf("Q18")
            }

            targetVehicleLocation.test {
                expectItem().also {
                    it.latitude shouldBe 0.0
                    it.latitude shouldBe 0.0
                }
            }
        }
    }

    @Test
    fun `load stop monitoring data should returns error result`() = coroutineTestResult.testDispatcher.runBlockingTest {

        coEvery { repository.stopMonitoring(any(), "error") } returns flow {
            emit(Result.Failure("error message"))
        }
        viewModel.apply {
            loadStopMonitoringData("key", "error")

            stopStopMonitoringData.test {
                expectItem() should beInstanceOf<Result.Failure>()
            }
        }
    }

    @Test
    fun `on publish line clicked`() = coroutineTestResult.testDispatcher.runBlockingTest {
        viewModel.apply {
            stop.test {
                expectItem()?.stopId shouldBe "400555"
            }
            onPublishLineClicked("400555")
            val publishedLine = targetPublishedLineName.first()
            publishedLine shouldBe "400555"
        }
    }

}