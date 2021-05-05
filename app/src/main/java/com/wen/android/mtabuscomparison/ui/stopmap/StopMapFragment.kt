package com.wen.android.mtabuscomparison.ui.stopmap

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.Parcelable
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.analytics.FirebaseAnalytics
import com.wen.android.mtabuscomparison.R
import com.wen.android.mtabuscomparison.common.permission.MyPermission
import com.wen.android.mtabuscomparison.common.permission.PermissionHelper
import com.wen.android.mtabuscomparison.common.permission.PermissionHelper.PermissionsResult
import com.wen.android.mtabuscomparison.feature.stopmonitoring.BusDatabase.Companion.getInstance
import com.wen.android.mtabuscomparison.feature.stopmonitoring.StopInfo
import com.wen.android.mtabuscomparison.ui.routesview.RoutesViewActivity
import com.wen.android.mtabuscomparison.ui.search.SearchActivity
import com.wen.android.mtabuscomparison.ui.stopmap.StopMapViewMvc.*
import com.wen.android.mtabuscomparison.ui.stopmonitoring.StopMonitoringActivity
import com.wen.android.mtabuscomparison.util.SearchHandler
import timber.log.Timber
import java.util.concurrent.Executors

/**
 * Created by yuan on 4/10/2017.
 */
class StopMapFragment : Fragment(), OnMovedMapListener, Listener, OnStartSearchListener, MapListener,
    PermissionHelper.Listener {
    private val PERMISSION_ACCESS_FINE_LOCATION = 1
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var mPermissionHelper: PermissionHelper? = null
    private lateinit var mStopMapView: StopMapViewMvc
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPermissionHelper = PermissionHelper(requireActivity())
        if (!mPermissionHelper!!.hasPermission(MyPermission.FINE_LOCATION)) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_ACCESS_FINE_LOCATION
            )
        } else {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mStopMapView = StopMapViewMvcImpl(inflater, container!!, childFragmentManager, this, this)
        setHasOptionsMenu(true)
        mStopMapView.getSearchBar().setOnEditorActionListener { v: TextView, actionId: Int, event: KeyEvent? ->
            displaySearchResult(v.text.toString())
            true
        }
        return mStopMapView.getRootView()
    }

    override fun onResume() {
        Timber.i("onResume")
        super.onResume()
    }

    override fun onStart() {
        Timber.i("onStart")
        super.onStart()
        mStopMapView!!.registerMapListener(this)
        mStopMapView!!.registerListener(this)
        mPermissionHelper!!.registerListener(this)
    }

    override fun onPause() {
        Timber.i("onPause")
        super.onPause()
    }

    override fun onStop() {
        Timber.i("onStop")
        super.onStop()
        mStopMapView!!.unregisterMapListener(this)
        mStopMapView!!.unregisterListener(this)
        mPermissionHelper!!.unregisterListener(this)
    }

    override fun onDestroyView() {
        Timber.v("onDestroyView()")
        mStopMapView!!.onDestroyView()
        super.onDestroyView()
    }

    override fun onDestroy() {
        Timber.v("onDestroy()")
        super.onDestroy()
    }

    /**
     * start a new activity and display the search result
     */
    fun displaySearchResult(userInput: String) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.SEARCH_TERM, userInput)
        FirebaseAnalytics.getInstance(context).logEvent(FirebaseAnalytics.Event.SEARCH, bundle)
        val searchHandler = SearchHandler(userInput)
        if (searchHandler.keywordType() == 0) {
            val stopcodeArray = arrayOfNulls<String>(1)
            //get the bus code from the user input
            stopcodeArray[0] = userInput
            if (stopcodeArray[0] == null) {
                return
            }
            val intent = Intent(activity, StopMonitoringActivity::class.java)
            intent.putExtra(Intent.EXTRA_TEXT, stopcodeArray)
            startActivity(intent)
        } else {
            val routeEntered = userInput.toUpperCase()
            val intent = Intent(activity, RoutesViewActivity::class.java)
            intent.putExtra(Intent.EXTRA_TEXT, routeEntered)
            startActivity(intent)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        mPermissionHelper!!.onRequestPermissionResult(requestCode, permissions, grantResults)
    }

    /**
     * get the current location and then find nearby stop from database
     */
    private fun findNearByStop(location: Location) {
        val stopList = ArrayList<StopInfo>()
        //change radius_in_meters if we want to change the range of the nearby stop
        val radius_in_meters = 800.0
        val radius_neg = 0 - radius_in_meters
        val coef_plus = radius_in_meters * 0.0000089
        val coef_neg = radius_neg * 0.0000089
        val new_latitude1: Double
        val new_latitude2: Double
        val new_longitude1: Double
        val new_longitude2: Double
        val current_latitude = location.latitude
        val current_longitude = location.longitude
        new_latitude1 = current_latitude + coef_neg
        new_latitude2 = current_latitude + coef_plus
        new_longitude1 = current_longitude + coef_plus / Math.cos(current_latitude * 0.018)
        new_longitude2 = current_longitude + coef_neg / Math.cos(current_latitude * 0.018)
        Executors.newSingleThreadExecutor().execute {
            val bustList =
                getInstance(
                    requireContext()
                ).busStopDao().getStopsInRange(new_latitude1, new_latitude2, new_longitude2, new_longitude1)
            for ((stopId, stopName, stopLat, stopLon, routeId) in bustList) {
                val stop = StopInfo()
                val tempLocation = Location("tempLocation")
                tempLocation.latitude = stopLat
                tempLocation.longitude = stopLon
                val distance = location.distanceTo(tempLocation)
                stop.stopCode = stopId
                stop.intersections = stopName
                stop.routes = routeId
                stop.location = tempLocation
                stop.distance = distance
                stopList.add(stop)
                stopList.sort()
            }
            if (activity != null) {
                requireActivity().runOnUiThread {
                    mStopMapView.removeMarkers()
                    for (st in stopList) {
                        mStopMapView.addStopMarker(st)
                    }
                    updateNearbyStopList(stopList)
                }
            }
        }
    }

    private fun updateNearbyStopList(nearbyStopList: List<StopInfo>) {
        mStopMapView.bindStopInfo(nearbyStopList)
    }

    override fun onMovedMap(latLng: LatLng) {
        val location = Location("")
        location.latitude = latLng.latitude
        location.longitude = latLng.longitude
        findNearByStop(location)
    }

    override fun onStopClicked(stop: StopInfo) {
        val navController = NavHostFragment.findNavController(this)
        navController.navigate(
            StopMapFragmentDirections.actionStopMapFragmentToStopMonitoringFragment(stop.stopCode)
        )
    }

    override fun onStartSearch() {
        startActivityForResult(
            Intent(context, SearchActivity::class.java),
            SEARCH_ACTIVITY_REQUEST_CODE,
            ActivityOptions.makeSceneTransitionAnimation(activity).toBundle()
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == SEARCH_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data!!.hasExtra(getString(R.string.SEARCH_RESULT_STOP_CODE))) {
                    val navController = NavHostFragment.findNavController(this)
                    navController.navigate(
                        StopMapFragmentDirections.actionStopMapFragmentToStopMonitoringFragment(
                            data.getStringExtra(getString(R.string.SEARCH_RESULT_STOP_CODE))
                        )
                    )
                } else {
                    val latlng = data.getParcelableExtra<Parcelable>(getString(R.string.SEARCH_RESULT_POINT)) as LatLng
                    mStopMapView.moveCameraTo(latlng)
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady() {
        if (mPermissionHelper!!.hasPermission(MyPermission.FINE_LOCATION)) {
            if (fusedLocationClient == null) {
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
            }
            fusedLocationClient!!.lastLocation.addOnSuccessListener { location: Location? ->
                Timber.i("last known location: $location")
                mStopMapView.addCurrentLocationMarker(location.orDummy)
                findNearByStop(location.orDummy)
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, result: PermissionsResult) {
        when (requestCode) {
            PERMISSION_ACCESS_FINE_LOCATION -> if (result.granted != null && result.granted.size > 0 && result.granted.contains(
                    MyPermission.FINE_LOCATION
                )
            ) {
                Timber.i("permission granted")
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
                fusedLocationClient!!.lastLocation.addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        //todo what would happen if location is null, does it break other things?
                        mStopMapView.enableMyLocationButton()
                        findNearByStop(location)
                        mStopMapView.addCurrentLocationMarker(location)
                    }
                }
            } else {
                Toast.makeText(context, "Need Location permission", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    override fun onPermissionsRequestCancelled(requestCode: Int) {}

    companion object {
        const val SEARCH_ACTIVITY_REQUEST_CODE = 199
    }
}