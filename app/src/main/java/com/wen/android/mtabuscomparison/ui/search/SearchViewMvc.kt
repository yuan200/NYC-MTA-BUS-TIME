package com.wen.android.mtabuscomparison.ui.search

import android.widget.EditText
import com.wen.android.mtabuscomparison.feature.search.SearchResultItem
import com.wen.android.mtabuscomparison.ui.commom.ObservableViewMvc

interface SearchViewMvc: ObservableViewMvc<SearchViewMvc.Listener>{

    interface Listener {
        fun onSearchResultClicked(searchResult: SearchResultItem)
    }

    fun getSearchET(): EditText

}