package com.wen.android.mtabuscomparison.ui.search

import android.location.Geocoder
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wen.android.mtabuscomparison.R
import com.wen.android.mtabuscomparison.feature.search.SearchItemType
import com.wen.android.mtabuscomparison.feature.search.SearchResultItem
import com.wen.android.mtabuscomparison.feature.stopmonitoring.BusDatabase
import com.wen.android.mtabuscomparison.ui.commom.BaseObservableViewMvc
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber

class SearchViewMvcImpl(inflater: LayoutInflater, parent: ViewGroup?) :
    BaseObservableViewMvc<SearchViewMvc.Listener>(), SearchViewMvc,
    SearchRecyclerAdapter.Listener {

    private var mSearchField: EditText
    private var mRecyclerView: RecyclerView
    private lateinit var mSearchAdapter: SearchRecyclerAdapter

    init {
        setRootView(inflater.inflate(R.layout.activity_search, parent, false))

        mRecyclerView = findViewById(R.id.search_recycler)
        mRecyclerView.layoutManager = LinearLayoutManager(getContext())
        mSearchField = findViewById<EditText>(R.id.search_field).apply {
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    if (s != null && s.toString().length > 2) {
                        CoroutineScope(Dispatchers.Main).launch {
                            getSearchResult(s.toString())
                                .debounce(300)
                                .collectLatest {
                                    mSearchAdapter =
                                        SearchRecyclerAdapter(it, this@SearchViewMvcImpl)
                                    mRecyclerView.adapter = mSearchAdapter
                                }
                        }
                    }
                }

            })

        }
    }

    private suspend fun getSearchResult(query: String): Flow<List<SearchResultItem>> = flow {
        val searchResults = mutableListOf<SearchResultItem>()
        try {
            if (Geocoder.isPresent()) {
                Geocoder.isPresent()
                val geocoder = Geocoder(getContext())
                val addresses = geocoder.getFromLocationName(
                    query.toString(), 2,
                    40.496094, -74.295208,
                    41.020723, -73.563419
                )
                    .map {
                        SearchResultItem(
                            "${((it.subThoroughfare ?: "") + " " + (it.thoroughfare ?: "")).ifBlank { it.featureName }}",
                            null, SearchItemType.MAP, it.latitude, it.longitude
                        )
                    }
                searchResults.addAll(addresses)

            }
        } catch (e: Exception) {
            Timber.e(e.message)
        }
        val stops = BusDatabase.getInstance(getContext()).busStopDao()
            .searchByStopNameOrId(query.toString())
        val results =
            stops.map { SearchResultItem(it.stopName ?: "", it.stopId) }
        searchResults.addAll(results)

        emit(searchResults)
    }.flowOn(Dispatchers.IO)

    override fun onSearchResultClicked(searchResult: SearchResultItem) {
        for (listener in getListeners()) {
            listener.onSearchResultClicked(searchResult)
        }
    }

    override fun getSearchET(): EditText {
        return mSearchField
    }
}