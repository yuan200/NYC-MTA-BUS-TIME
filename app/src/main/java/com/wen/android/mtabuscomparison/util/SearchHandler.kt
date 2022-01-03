package com.wen.android.mtabuscomparison.util

import com.wen.android.mtabuscomparison.feature.search.SearchType


class SearchHandler(private val mKeyword: String) {

    fun keywordType(): SearchType {
        val firstCharacter = Character.toString(mKeyword[0])
        //if return true then it is a stop code otherwise is a route
        val isStopCode = firstCharacter.matches(Regex("\\d+"))
        return if (isStopCode) {
            SearchType.STOP
        } else {
            SearchType.ROUTE
        }
    }

}
