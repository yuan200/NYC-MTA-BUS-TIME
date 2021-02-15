package com.wen.android.mtabuscomparison.data.remote.bustime

data class PtSituationElement(
    val Affects: Affects,
    val Consequences: Consequences,
    val CreationTime: String,
    val Description: String,
    val PublicationWindow: PublicationWindow,
    val Severity: String,
    val SituationNumber: String,
    val Summary: String
)