package com.wen.android.mtabuscomparison.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.wen.android.mtabuscomparison.R
import com.wen.android.mtabuscomparison.databinding.SearchListItemBinding
import com.wen.android.mtabuscomparison.feature.search.SearchResultItem
import com.wen.android.mtabuscomparison.ui.commom.BaseObservableViewMvc

class SearchListItemViewMvcImpl(
    inflater: LayoutInflater,
    parent: ViewGroup
) : BaseObservableViewMvc<SearchListItemViewMvc.Listener>(),
    SearchListItemViewMvc {

    private var mSearchResultTitle: TextView
    private var mBinding: SearchListItemBinding
    private lateinit var mSearchResults: SearchResultItem

    init {
        setRootView(inflater.inflate(R.layout.search_list_item, parent, false))
        mBinding = DataBindingUtil.bind<SearchListItemBinding>(getRootView())!!

        mSearchResultTitle = findViewById(R.id.search_result_title)
        mSearchResultTitle.setOnClickListener {
            for (listener in getListeners()) {
                listener.onClicked(mSearchResults)
            }
        }
    }

    override fun bindSearchResult(result: SearchResultItem) {
        mSearchResults = result
        mSearchResultTitle.text = result.name
        mBinding.itemType = result.type
    }
}