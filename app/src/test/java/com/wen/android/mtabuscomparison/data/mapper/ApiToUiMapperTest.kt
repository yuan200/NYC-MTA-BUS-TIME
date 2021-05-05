package com.wen.android.mtabuscomparison.data.mapper

import com.wen.android.mtabuscomparison.data.remote.bustime.StopMonitoringResponse
import com.wen.android.mtabuscomparison.testhelper.extensions.toObject
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class ApiToUiMapperTest {

    private lateinit var validResponse: StopMonitoringResponse
    private lateinit var apiToUiMapper: ApiToUiMapper
    private lateinit var validResponseWithSituation: StopMonitoringResponse

    @BeforeEach
    fun setUp() {
        validResponse = VALID_RESPONSE.toObject()
        validResponseWithSituation = VALID_RESPONSE_WITH_SITUATIONS.toObject()
        validResponseWithSituation
        apiToUiMapper = ApiToUiMapper()
    }

    @Test
    fun `map a valid response`() {
        val monitoringData = apiToUiMapper.mapToStopMonitoring(validResponse, "400555")
        monitoringData.stopId shouldBe "400555"
        monitoringData.busMonitoring[0].destinationName shouldBe "Jackson Hts Northern-81 St via Roosevelt "
        monitoringData.busMonitoring[0].expectedArrivalTime shouldBe  "2021-05-07T08:49:33.676-04:00"
    }

    @Test
    fun `map situation message`() {
        val monitoringData = apiToUiMapper.mapToStopMonitoring(validResponseWithSituation, "401844")
        monitoringData.stopId shouldBe "401844"
        monitoringData.situations!!.PtSituationElement[0].Description shouldBe "You may experience longer waits for this bus. We're running as much service as we can with the operators we have available."

    }


    companion object {
        private val VALID_RESPONSE =
            """
                {
                    "Siri": {
                        "ServiceDelivery": {
                            "ResponseTimestamp": "2021-05-07T08:17:32.977-04:00",
                            "StopMonitoringDelivery": [
                                {
                                    "MonitoredStopVisit": [
                                        {
                                            "MonitoredVehicleJourney": {
                                                "LineRef": "MTA NYCT_Q32",
                                                "DirectionRef": "0",
                                                "FramedVehicleJourneyRef": {
                                                    "DataFrameRef": "2021-05-07",
                                                    "DatedVehicleJourneyRef": "MTA NYCT_CS_B1-Weekday-SDon-052800_Q32_762"
                                                },
                                                "JourneyPatternRef": "MTA_Q320048",
                                                "PublishedLineName": "Q32",
                                                "OperatorRef": "MTA NYCT",
                                                "OriginRef": "MTA_400552",
                                                "DestinationName": "JACKSON HTS NORTHERN-81 ST via ROOSEVELT",
                                                "OriginAimedDepartureTime": "2021-05-07T08:48:00.000-04:00",
                                                "SituationRef": [],
                                                "Monitored": true,
                                                "VehicleLocation": {
                                                    "Longitude": -73.981206,
                                                    "Latitude": 40.753028
                                                },
                                                "Bearing": 233.91856,
                                                "ProgressRate": "normalProgress",
                                                "ProgressStatus": "prevTrip",
                                                "BlockRef": "MTA NYCT_CS_B1-Weekday-SDon_E_CS_25260_Q32-762",
                                                "VehicleRef": "MTA NYCT_4183",
                                                "MonitoredCall": {
                                                    "AimedArrivalTime": "2021-05-07T08:52:01.986-04:00",
                                                    "ExpectedArrivalTime": "2021-05-07T08:49:33.676-04:00",
                                                    "ExpectedDepartureTime": "2021-05-07T08:49:33.676-04:00",
                                                    "Extensions": {
                                                        "Distances": {
                                                            "PresentableDistance": "1.2 miles away",
                                                            "DistanceFromCall": 1932.3,
                                                            "StopsFromCall": 3,
                                                            "CallDistanceAlongRoute": 468.07
                                                        }
                                                    },
                                                    "StopPointRef": "MTA_400555",
                                                    "VisitNumber": 1,
                                                    "StopPointName": "E 32 ST/5 AV"
                                                },
                                                "OnwardCalls": {}
                                            },
                                            "RecordedAtTime": "2021-05-07T08:17:09.000-04:00"
                                        },
                                        {
                                            "MonitoredVehicleJourney": {
                                                "LineRef": "MTA NYCT_Q32",
                                                "DirectionRef": "0",
                                                "FramedVehicleJourneyRef": {
                                                    "DataFrameRef": "2021-05-07",
                                                    "DatedVehicleJourneyRef": "MTA NYCT_CS_B1-Weekday-SDon-057600_Q32_763"
                                                },
                                                "JourneyPatternRef": "MTA_Q320048",
                                                "PublishedLineName": "Q32",
                                                "OperatorRef": "MTA NYCT",
                                                "OriginRef": "MTA_400552",
                                                "DestinationName": "JACKSON HTS NORTHERN-81 ST via ROOSEVELT",
                                                "OriginAimedDepartureTime": "2021-05-07T09:36:00.000-04:00",
                                                "SituationRef": [],
                                                "Monitored": true,
                                                "VehicleLocation": {
                                                    "Longitude": -73.924034,
                                                    "Latitude": 40.743939
                                                },
                                                "Bearing": 173.23761,
                                                "ProgressRate": "normalProgress",
                                                "ProgressStatus": "prevTrip",
                                                "BlockRef": "MTA NYCT_CS_B1-Weekday-SDon_E_CS_14460_Q32-751",
                                                "VehicleRef": "MTA NYCT_7402",
                                                "MonitoredCall": {
                                                    "AimedArrivalTime": "2021-05-07T09:40:13.997-04:00",
                                                    "Extensions": {
                                                        "Distances": {
                                                            "PresentableDistance": "5.3 miles away",
                                                            "DistanceFromCall": 8456.86,
                                                            "StopsFromCall": 3,
                                                            "CallDistanceAlongRoute": 468.07
                                                        }
                                                    },
                                                    "StopPointRef": "MTA_400555",
                                                    "VisitNumber": 1,
                                                    "StopPointName": "E 32 ST/5 AV"
                                                },
                                                "OnwardCalls": {}
                                            },
                                            "RecordedAtTime": "2021-05-07T08:17:00.000-04:00"
                                        }
                                    ],
                                    "ResponseTimestamp": "2021-05-07T08:17:32.977-04:00",
                                    "ValidUntil": "2021-05-07T08:18:32.977-04:00"
                                }
                            ],
                            "SituationExchangeDelivery": []
                        }
                    }
                }
            """.trimIndent()
    }

    private val VALID_RESPONSE_WITH_SITUATIONS =
        """
            {
                "Siri": {
                    "ServiceDelivery": {
                        "ResponseTimestamp": "2021-05-07T09:40:41.310-04:00",
                        "StopMonitoringDelivery": [
                            {
                                "MonitoredStopVisit": [
                                    {
                                        "MonitoredVehicleJourney": {
                                            "LineRef": "MTABC_QM2",
                                            "DirectionRef": "1",
                                            "FramedVehicleJourneyRef": {
                                                "DataFrameRef": "2021-05-07",
                                                "DatedVehicleJourneyRef": "MTABC_30721888-CPPB1-CP_B1-Weekday-90-SDon"
                                            },
                                            "JourneyPatternRef": "MTA_QM20234",
                                            "PublishedLineName": "QM2",
                                            "OperatorRef": "MTABC",
                                            "OriginRef": "MTA_552984",
                                            "DestinationName": "MIDTOWN 57 ST via 6 AV",
                                            "SituationRef": [],
                                            "Monitored": true,
                                            "VehicleLocation": {
                                                "Longitude": -73.980145,
                                                "Latitude": 40.746612
                                            },
                                            "Bearing": 156.9705,
                                            "ProgressRate": "normalProgress",
                                            "BlockRef": "MTABC_CPPB1-CP_B1-Weekday-90-SDon_5906621",
                                            "VehicleRef": "MTABC_3044",
                                            "MonitoredCall": {
                                                "AimedArrivalTime": "2021-05-07T09:49:08.330-04:00",
                                                "ExpectedArrivalTime": "2021-05-07T09:42:36.934-04:00",
                                                "ExpectedDepartureTime": "2021-05-07T09:42:36.934-04:00",
                                                "Extensions": {
                                                    "Distances": {
                                                        "PresentableDistance": "1 stop away",
                                                        "DistanceFromCall": 462.14,
                                                        "StopsFromCall": 1,
                                                        "CallDistanceAlongRoute": 26699.39
                                                    }
                                                },
                                                "StopPointRef": "MTA_401844",
                                                "VisitNumber": 1,
                                                "StopPointName": "W 34 ST/5 AV"
                                            },
                                            "OnwardCalls": {}
                                        },
                                        "RecordedAtTime": "2021-05-07T09:40:25.000-04:00"
                                    }
                                ],
                                "ResponseTimestamp": "2021-05-07T09:40:41.310-04:00",
                                "ValidUntil": "2021-05-07T09:41:41.310-04:00"
                            }
                        ],
                        "SituationExchangeDelivery": [
                            {
                                "Situations": {
                                    "PtSituationElement": [
                                        {
                                            "PublicationWindow": {
                                                "StartTime": "2021-05-07T07:24:00.000-04:00"
                                            },
                                            "Severity": "undefined",
                                            "Summary": "You may experience longer waits for this bus. We're running as much service as we can with the operators we have available.",
                                            "Description": "You may experience longer waits for this bus. We're running as much service as we can with the operators we have available.",
                                            "Affects": {
                                                "VehicleJourneys": {
                                                    "AffectedVehicleJourney": [
                                                        {
                                                            "LineRef": "MTA NYCT_Q20A",
                                                            "DirectionRef": "1"
                                                        }
                                                    ]
                                                }
                                            },
                                            "Consequences": {
                                                "Consequence": [
                                                    {
                                                        "Condition": "delayed"
                                                    }
                                                ]
                                            },
                                            "CreationTime": "2021-05-07T08:05:20.793-04:00",
                                            "SituationNumber": "MTA NYCT_d5d401a9-5078-48ae-b33d-ea6c7c933619"
                                        }
                                    ]
                                }
                            }
                        ]
                    }
                }
            }
        """.trimIndent()
}



