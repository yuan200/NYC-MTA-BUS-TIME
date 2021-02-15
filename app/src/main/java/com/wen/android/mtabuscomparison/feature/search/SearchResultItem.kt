package com.wen.android.mtabuscomparison.feature.search

class SearchResultItem(
    val name: String,
    val stopId: String?,
    val type: SearchItemType = SearchItemType.STOP,
    val lat: Double? = null,
    val lng: Double? = null
)


