package com.wen.android.mtabuscomparison.data.remote.bustime

data class MonitoredVehicleJourney(
    val Bearing: Double,
    val BlockRef: String,
    val DestinationName: String,
    val DirectionRef: String,
    val FramedVehicleJourneyRef: FramedVehicleJourneyRef,
    val JourneyPatternRef: String,
    val LineRef: String,
    val Monitored: Boolean,
    val MonitoredCall: MonitoredCall?,
    val OnwardCalls: OnwardCalls,
    val OperatorRef: String,
    val OriginAimedDepartureTime: String,
    val OriginRef: String,
    val ProgressRate: String,
    val ProgressStatus: String,
    val PublishedLineName: String,
    val SituationRef: List<SituationRef>,
    val VehicleLocation: VehicleLocation,
    val VehicleRef: String
)