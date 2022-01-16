package com.wen.android.mtabuscomparison.ui.search

import android.location.Geocoder
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wen.android.mtabuscomparison.R
import com.wen.android.mtabuscomparison.feature.search.SearchResultItem
import com.wen.android.mtabuscomparison.feature.search.SearchType
import com.wen.android.mtabuscomparison.feature.stopmonitoring.BusDatabase
import com.wen.android.mtabuscomparison.ui.commom.BaseObservableViewMvc
import com.wen.android.mtabuscomparison.ui.textChanges
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import timber.log.Timber

class SearchViewMvcImpl(inflater: LayoutInflater, parent: ViewGroup?) :
    BaseObservableViewMvc<SearchViewMvc.Listener>(), SearchViewMvc,
    SearchRecyclerAdapter.Listener {

    private var mSearchField: EditText
    private var mRecyclerView: RecyclerView
    private lateinit var mSearchAdapter: SearchRecyclerAdapter

    init {
        setRootView(inflater.inflate(R.layout.fragment_search, parent, false))

        mRecyclerView = findViewById(R.id.search_recycler)
        mRecyclerView.layoutManager = LinearLayoutManager(getContext())
        mSearchField = findViewById<EditText>(R.id.search_field)
        mSearchField.textChanges().debounce(300)
            .filter { !it.isNullOrBlank() }
            .flatMapConcat { getSearchResult(it.toString()) }
            .onEach {
                mSearchAdapter = SearchRecyclerAdapter(it, this@SearchViewMvcImpl)
                mRecyclerView.adapter = mSearchAdapter
            }
            .launchIn(CoroutineScope(Dispatchers.Main))
    }

    private suspend fun getSearchResult(query: String): Flow<List<SearchResultItem>> = flow {
        val searchResults = mutableListOf<SearchResultItem>()

        val route = BusDatabase.getInstance(getContext()).busStopDao().searchByRouteId(query.uppercase())
        route?.hasRoute(query)?.let {
            searchResults.add(SearchResultItem(query.uppercase(), "", SearchType.ROUTE))
        }

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
                            null, SearchType.MAP, it.latitude, it.longitude
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