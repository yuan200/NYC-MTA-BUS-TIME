package com.wen.android.mtabuscomparison.ui.search

import com.wen.android.mtabuscomparison.feature.search.SearchResultItem
import com.wen.android.mtabuscomparison.ui.commom.ObservableViewMvc

interface SearchListItemViewMvc : ObservableViewMvc<SearchListItemViewMvc.Listener> {

    interface Listener {
        fun onClicked(searchResult: SearchResultItem)
    }

    fun bindSearchResult(result: SearchResultItem)
}