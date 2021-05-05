package com.wen.android.mtabuscomparison.ui.search

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.analytics.FirebaseAnalytics
import com.wen.android.mtabuscomparison.R
import com.wen.android.mtabuscomparison.feature.search.SearchItemType
import com.wen.android.mtabuscomparison.feature.search.SearchResultItem
import com.wen.android.mtabuscomparison.ui.routesview.RoutesViewActivity
import com.wen.android.mtabuscomparison.util.SearchHandler


class SearchActivity : AppCompatActivity(), SearchViewMvc.Listener {
    private lateinit var mSearchView: SearchViewMvc
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mSearchView = SearchViewMvcImpl(layoutInflater, null).also {
            it.registerListener(this)
        }
        setContentView(mSearchView.getRootView())

        mSearchView.getSearchET().setOnEditorActionListener { v, _, _ ->
            displaySearchResult(v.text.toString())
            true
        }
    }

    override fun onStart() {
        super.onStart()
        mSearchView.registerListener(this)
    }

    override fun onStop() {
        super.onStop()
        mSearchView.unregisterListener(this)
    }

    override fun onSearchResultClicked(searchResult: SearchResultItem) {
        when (searchResult.type) {
            SearchItemType.STOP -> {
                finishAndSearchStop(searchResult.stopId!!)
            }
            SearchItemType.MAP -> {
                hideKeyboard(this)
                val intent = Intent().apply {
                    val latLng = LatLng(searchResult.lat!!, searchResult.lng!!)
                    putExtra(getString(R.string.SEARCH_RESULT_POINT), latLng)
                }
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }

    private fun finishAndSearchStop(stopId: String) {
        val stopCodeIntent = Intent().apply {
            putExtra(getString(R.string.SEARCH_RESULT_STOP_CODE), stopId)
        }
        setResult(Activity.RESULT_OK, stopCodeIntent)
        finish()
    }

    fun displaySearchResult(userInput: String) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.SEARCH_TERM, userInput)
        FirebaseAnalytics.getInstance(this).logEvent(FirebaseAnalytics.Event.SEARCH, bundle)
        val searchHandler = SearchHandler(userInput)
        if (searchHandler.keywordType() == 0) {
            val stopcodeArray = arrayOfNulls<String>(1)
            //get the bus code from the user input
            stopcodeArray[0] = userInput
            if (stopcodeArray[0] == null) {
                return
            }
            finishAndSearchStop(stopcodeArray[0]!!)
        } else {
            val routeEntered = userInput.toUpperCase()
            val intent = Intent(this, RoutesViewActivity::class.java)
            intent.putExtra(Intent.EXTRA_TEXT, routeEntered)
            startActivity(intent)
        }
    }
    private fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}