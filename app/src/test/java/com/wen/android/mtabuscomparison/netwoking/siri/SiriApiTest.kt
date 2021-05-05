package com.wen.android.mtabuscomparison.netwoking.siri

import com.wen.android.mtabuscomparison.testhelper.extensions.toJsonResponse
import io.kotest.matchers.shouldBe
import io.mockk.MockKAnnotations
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class SiriApiTest {

    private lateinit var webServer: MockWebServer
    private lateinit var serverAddress: String

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        webServer = MockWebServer()
        webServer.start()
        serverAddress = "http://${webServer.hostName}:${webServer.port}"
    }

    @AfterEach
    fun tearDown() {
        webServer.shutdown()
    }

    private fun createAPI(): SiriApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(serverAddress)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return SiriApiService(retrofit).siri()
    }

    @Test
    fun `fetch stop monitoring`() {
        val api = createAPI()

        val response = """
{
    "Siri": {
        "ServiceDelivery": {
            "ResponseTimestamp": "2021-05-05T22:54:17.544-04:00"
        }
    }
}
        """
        response.toJsonResponse().apply { webServer.enqueue(this) }

        runBlocking {
            api.stopMonitoring("key", "stopId").apply {
                body()!!.Siri.ServiceDelivery.ResponseTimestamp shouldBe "2021-05-05T22:54:17.544-04:00"
            }
        }

        val request = webServer.takeRequest(5, TimeUnit.SECONDS)!!
        request.method shouldBe "GET"
        request.path shouldBe "/stop-monitoring.json?key=key&MonitoringRef=stopId"
    }

}