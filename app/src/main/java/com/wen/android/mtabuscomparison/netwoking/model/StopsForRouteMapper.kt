package com.wen.android.mtabuscomparison.netwoking.model

import com.wen.android.mtabuscomparison.data.mapper.DomainMapper
import com.wen.android.mtabuscomparison.feature.stopmonitoring.BusDirection
import com.wen.android.mtabuscomparison.feature.stopmonitoring.StopInfo
import timber.log.Timber

//todo change domain name BusDirection
class StopsForRouteMapper: DomainMapper<StopsForRouteDto, BusDirection> {

    override fun mapToDomainModel(dto: StopsForRouteDto): BusDirection {
        var stopInfoList0: List<StopInfo> = listOf()
        var stopInfoList1: List<StopInfo> = listOf()
        val stops = dto.stops

        try {
            stopInfoList0 = getStopInfoList(dto, stops, dto.stopGroupings[0].stopGroups[0].stopIds, 0)
            stopInfoList1 = getStopInfoList(dto, stops, dto.stopGroupings[0].stopGroups[1].stopIds, 1)

        } catch (exception: Exception) {
            Timber.e(exception)
        }

        return BusDirection(stopInfoList0, stopInfoList1)
    }

    private fun getStopInfoList(
        dto: StopsForRouteDto,
        stops: List<Stop>,
        stopIds: List<String>,
        direction: Int
    ): List<StopInfo> {
        val stopInfoList: MutableList<StopInfo> = mutableListOf()

        val directionName = dto.stopGroupings[0].stopGroups[direction].name.name
        for (stopId in stopIds) {
            val stop = StopInfo().apply {
                id = stopId
                //todo set this once
                busDirection = directionName
                stops.find {
                    it.id == stopId
                }?.let {
                    this.stopCode = it.code
                    this.intersections = it.name
                }
            }
            stopInfoList.add(stop)
        }
        return stopInfoList.toList()
    }

    override fun mapFromDomainModel(domainModel: BusDirection): StopsForRouteDto {
        TODO()
    }
}