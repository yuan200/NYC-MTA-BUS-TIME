package com.wen.android.mtabuscomparison.data.mapper

import com.wen.android.mtabuscomparison.data.remote.bustime.StopMonitoringResponse
import com.wen.android.mtabuscomparison.feature.stop.MonitoringData
import com.wen.android.mtabuscomparison.feature.stop.StopMonitoringListItem

class ApiToUiMapper {
    fun mapToStopForId(response: StopMonitoringResponse, stopId: String): MonitoringData {
        //MonitoredStopVisit could be null if query a wrong stop number, ex: 141266
        val monitoringDelivery = response.Siri.ServiceDelivery.StopMonitoringDelivery[0]
        val monitoredVehicleList = monitoringDelivery.MonitoredStopVisit ?: emptyList()
        val situationExchange = response.Siri.ServiceDelivery.SituationExchangeDelivery
        val resultMap = mutableMapOf<String, StopMonitoringListItem>()

        for (vehicle in monitoredVehicleList) {
            if (!resultMap.containsKey(vehicle.MonitoredVehicleJourney.LineRef)) {
                val timeInfo =
                    StopMonitoringListItem()
                timeInfo.stopNumber = stopId
                timeInfo.publishedLineName = vehicle.MonitoredVehicleJourney.PublishedLineName
                timeInfo.destinationName =
                    toCamelCase(vehicle.MonitoredVehicleJourney.DestinationName)
                if (vehicle.MonitoredVehicleJourney.MonitoredCall != null) {
                    timeInfo.expectedArrivalTime =
                        vehicle.MonitoredVehicleJourney.MonitoredCall.ExpectedArrivalTime
                    timeInfo.stopPointName =
                        vehicle.MonitoredVehicleJourney.MonitoredCall.StopPointName
                    timeInfo.presentableDistance =
                        vehicle.MonitoredVehicleJourney.MonitoredCall.Extensions.Distances.PresentableDistance
                }
                resultMap[vehicle.MonitoredVehicleJourney.LineRef] = timeInfo
            } else {
                if (vehicle.MonitoredVehicleJourney.MonitoredCall != null)
                    resultMap[vehicle.MonitoredVehicleJourney.LineRef]!!.nextBusTime.add(vehicle.MonitoredVehicleJourney.MonitoredCall.ExpectedArrivalTime)
            }
        }

        val resultList = resultMap.values.toList()
        val situations =
            if (!situationExchange.isNullOrEmpty()) situationExchange[0].Situations else null
        var errorText = ""
        if (monitoringDelivery.ErrorCondition != null) {
            errorText = monitoringDelivery.ErrorCondition.Description
        }
        return MonitoringData(stopId, situations, resultList, errorText)
    }

    private fun toCamelCase(str: String): String {
        var sb = StringBuilder()
        for (s in str.split(" ")) {
            if (s.length > 1 || s == "AV") {
                sb.append(s[0])
                sb.append(s.substring(1).toLowerCase())
                sb.append(" ")
            } else {
                sb.append(s)
                sb.append(" ")
            }
        }
        return sb.toString()
    }
}