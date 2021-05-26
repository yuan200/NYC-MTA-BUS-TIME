package com.wen.android.mtabuscomparison.ui.stopmonitoring

import androidx.lifecycle.SavedStateHandle
import com.wen.android.mtabuscomparison.common.Result
import com.wen.android.mtabuscomparison.feature.stopmonitoring.Stop
import com.wen.android.mtabuscomparison.feature.stopmonitoring.StopMonitoringData
import com.wen.android.mtabuscomparison.feature.stopmonitoring.StopMonitoringListItem
import com.wen.android.mtabuscomparison.feature.stopmonitoring.storage.repo.StopMonitoringRepository
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.beInstanceOf
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class StopMonitoringViewModelTest {

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

        stop = Stop("stopId", "stopName", 0.0, 0.0, "routeId")

        every { apiResult.busMonitoring } returns listOf(StopMonitoringListItem().apply {
            publishedLineName = "publishLineName"
        })
        every { savedStateHandle.get(any()) as String? } returns ""
        coEvery { repository.stop(any()) } returns flow { emit(stop) }
        coEvery { repository.stopMonitoring(any(), any()) } returns flow {
            emit(Result.Loading)
            emit(Result.Success(apiResult))
        }

        viewModel = StopMonitoringViewModel(
            repository,
            savedStateHandle
        )
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    fun `load stop monitoring data should update flow`() {
        viewModel.apply {
            loadStopMonitoringData("key", "400555")

            runBlocking {
                launch {
                    val result = stopStopMonitoringData.take(2).toList()
                    result[1] should beInstanceOf<Result.Loading>()
                    result[0] should beInstanceOf<Result.Success<StopMonitoringData>>()
                    (result[0] as Result.Success).data.busMonitoring[0].publishedLineName shouldBe "publishLineName"
                }

                //drop 1 because first value is an initial null value
                publishedLineAdapterData.drop(1).first()[0] shouldBe "publishLineName"
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