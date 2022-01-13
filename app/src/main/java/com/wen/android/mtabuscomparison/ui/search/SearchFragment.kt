package com.wen.android.mtabuscomparison.ui.search

import android.app.Activity
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import com.wen.android.mtabuscomparison.R
import com.wen.android.mtabuscomparison.feature.search.SearchResultItem
import com.wen.android.mtabuscomparison.feature.search.SearchType
import com.wen.android.mtabuscomparison.ui.routesview.RoutesViewActivity
import com.wen.android.mtabuscomparison.util.SearchHandler
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class SearchFragment : Fragment(), SearchViewMvc.Listener {

    @Inject
    lateinit var firebaseAnalytics:FirebaseAnalytics

    private lateinit var mSearchView: SearchViewMvc

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        mSearchView = SearchViewMvcImpl(layoutInflater, null).also {
            it.registerListener(this)
        }

        mSearchView.getSearchET().setOnEditorActionListener { v, _, _ ->
            displaySearchResult(v.text.toString())
            true
        }

        return mSearchView.getRootView()
    }

    override fun onStart() {
        super.onStart()
        mSearchView.registerListener(this)
    }

    override fun onResume() {
        super.onResume()
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, SearchFragment::class.java.simpleName)
        }
    }

    override fun onStop() {
        super.onStop()
        mSearchView.unregisterListener(this)
    }

    override fun onSearchResultClicked(searchResult: SearchResultItem) {
        when (searchResult.type) {
            SearchType.STOP -> {
                searchStop(searchResult.stopId!!)
            }
            SearchType.MAP -> {
                hideKeyboard(requireActivity())
                val latLng = LatLng(searchResult.lat!!, searchResult.lng!!)
                NavHostFragment.findNavController(this).apply {
                    previousBackStackEntry?.savedStateHandle?.set(getString(R.string.SEARCH_RESULT_POINT), latLng)
                    popBackStack()
                }
            }
        }
    }

    private fun searchStop(stopId: String) {
        NavHostFragment.findNavController(this).apply {
            navigate(SearchFragmentDirections.actionSearchFragmentToStopMonitoringFragment(stopId))
        }
    }

    private fun displaySearchResult(userInput: String) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.SEARCH_TERM, userInput)
        FirebaseAnalytics.getInstance(requireContext()).logEvent(FirebaseAnalytics.Event.SEARCH, bundle)
        val searchHandler = SearchHandler(userInput)
        if (searchHandler.keywordType() == SearchType.STOP) {
            val stopcodeArray = arrayOfNulls<String>(1)
            //get the bus code from the user input
            stopcodeArray[0] = userInput
            if (stopcodeArray[0] == null) {
                return
            }
            searchStop(stopcodeArray[0]!!)
        } else {
            val routeEntered = userInput.toUpperCase(Locale.US)
            val intent = Intent(requireContext(), RoutesViewActivity::class.java)
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