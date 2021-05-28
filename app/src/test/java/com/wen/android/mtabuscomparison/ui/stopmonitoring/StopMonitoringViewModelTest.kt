package com.wen.android.mtabuscomparison.ui.stopmonitoring

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.wen.android.mtabuscomparison.common.Result
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
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

        stop = Stop("400555", "E 32 ST/5 AV", 0.0, 0.0, "routeId")

        every { apiResult.busMonitoring } returns listOf(StopMonitoringListItem().apply {
            publishedLineName = "publishLineName"
        })
        every { savedStateHandle.get(any()) as String? } returns ""
        coEvery { repository.stopMonitoring(any(), "400555") } returns flow {
            emit(Result.Success(apiResult))
        }

        viewModel = StopMonitoringViewModel(
            TestDispatcherProvider(),
            repository,
            savedStateHandle
        )
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    fun `stop flow should return data from repository`() = runBlockingTest {
        coEvery { repository.stop(any()) } returns flow { emit(stop) }

        viewModel.apply {
            stop.test {
                expectItem()?.stopId shouldBe "400555"
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `load stop monitoring data should update flow`() = runBlockingTest {
        viewModel.apply {
            loadStopMonitoringData("key", "400555")

            stopStopMonitoringData.test {
                expectItem() should beInstanceOf<Result.Success<StopMonitoringData>>()
                cancelAndIgnoreRemainingEvents()
            }

            launch {
                publishedLineAdapterData.test {
                    expectItem() shouldBe "publishLineName"
                }
            }
        }
    }

    @Test
    fun `load stop monitoring data should returns error result`() = runBlockingTest {

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
    fun `on publish line clicked`() {
        runBlocking {
            viewModel.apply {
                onPublishLineClicked("400555")
                val publishedLine = targetPublishedLineName.first()
                publishedLine shouldBe "400555"

            }
        }
    }

}