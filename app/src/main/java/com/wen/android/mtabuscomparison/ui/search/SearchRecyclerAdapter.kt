package com.wen.android.mtabuscomparison.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wen.android.mtabuscomparison.feature.search.SearchResultItem

class SearchRecyclerAdapter(
    private val mResults: List<SearchResultItem>,
    private val mListener: Listener
    ): RecyclerView.Adapter<SearchRecyclerAdapter.SearchItemHolder>(),
SearchListItemViewMvc.Listener{

    interface Listener {
        fun onSearchResultClicked(searchResult: SearchResultItem)
    }

    class SearchItemHolder(val searchItem:SearchListItemViewMvc): RecyclerView.ViewHolder(searchItem.getRootView())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchItemHolder {
        val searchListItemView = SearchListItemViewMvcImpl(LayoutInflater.from(parent.context), parent).also {
            it.registerListener(this)
        }
        return SearchItemHolder(searchListItemView)
    }

    override fun onBindViewHolder(holderSearch: SearchItemHolder, position: Int) {
        holderSearch.searchItem.bindSearchResult(mResults[position])
    }

    override fun getItemCount(): Int = mResults.size

    override fun onClicked(searchResult: SearchResultItem) {
        mListener.onSearchResultClicked(searchResult)
    }
}