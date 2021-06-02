package com.wen.android.mtabuscomparison.ui.stopmap

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Parcelable
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.firebase.analytics.FirebaseAnalytics
import com.wen.android.mtabuscomparison.R
import com.wen.android.mtabuscomparison.common.permission.MyPermission
import com.wen.android.mtabuscomparison.common.permission.PermissionHelper
import com.wen.android.mtabuscomparison.common.permission.PermissionHelper.PermissionsResult
import com.wen.android.mtabuscomparison.databinding.FragmentStopMapBinding
import com.wen.android.mtabuscomparison.feature.stopmonitoring.StopInfo
import com.wen.android.mtabuscomparison.ui.routesview.RoutesViewActivity
import com.wen.android.mtabuscomparison.ui.search.SearchActivity
import com.wen.android.mtabuscomparison.ui.stopmap.StopMapViewMvc.*
import com.wen.android.mtabuscomparison.ui.stopmonitoring.StopMonitoringActivity
import com.wen.android.mtabuscomparison.util.SearchHandler
import com.wen.android.mtabuscomparison.util.bitmapDescriptorFromVector
import com.wen.android.mtabuscomparison.util.dpToPx
import com.wen.android.mtabuscomparison.util.fragment.repeatOnViewLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.*

/**
 * Created by yuan on 4/10/2017.
 */
@AndroidEntryPoint
class StopMapFragment : Fragment(), OnMovedMapListener, Listener, OnStartSearchListener, MapListener,
    PermissionHelper.Listener, StopsRecyclerAdapter.Listener {
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var mPermissionHelper: PermissionHelper? = null
    private var mCurrentFocusStop = 0
    private var previousFocusStop = Integer.MAX_VALUE
    private lateinit var mGoogleMap: GoogleMap
    private lateinit var mAdapter: StopsRecyclerAdapter
    private val mStopMarkList = mutableListOf<Marker>()
    private var mPreviousCameraLocation = Location("").apply {
        this.latitude = 0.0
        this.longitude = 0.0
    }
    private val noUpdateDistance = 200

    private val viewModel: StopMapViewModel by viewModels()

    private var _binding: FragmentStopMapBinding? = null

    private val binding get() = _binding!!

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStopMapBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        binding.stopMapMapView.onCreate(savedInstanceState)
        binding.stopMapMapView.getMapAsync {
            mGoogleMap = it
            mGoogleMap.uiSettings.isMapToolbarEnabled = false
            mGoogleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(),
                    R.raw.google_map_no_bus_stop_style
                )
            )

            enableMyLocationButton()

            onMapReady()

            mGoogleMap.setOnCameraIdleListener {
                val currentCameraLocation = Location("").apply {
                    this.latitude = it.cameraPosition.target.latitude
                    this.longitude = it.cameraPosition.target.longitude
                }
                if (mPreviousCameraLocation.distanceTo(currentCameraLocation) > noUpdateDistance) {
                    onMovedMap(it.cameraPosition.target)
                }
                mPreviousCameraLocation = currentCameraLocation
            }
        }
        binding.apply {
            nearbyRecycleView.layoutManager = LinearLayoutManager(context)
            searchView.apply {
                doOnLayout {
                    it.translationX = it.width - (40.dpToPx).toFloat()
                }
            }
            stopMapSearchIcon.setOnClickListener { onStartSearch() }
        }

        binding.searchEt.setOnEditorActionListener { v: TextView, actionId: Int, event: KeyEvent? ->
            displaySearchResult(v.text.toString())
            true
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        repeatOnViewLifecycle(Lifecycle.State.STARTED) {
            viewModel.nearByStop.collect {
                withContext(Dispatchers.Main) {
                    if (mStopMarkList.isNotEmpty()) {
                        removeMarkers()
                    }
                    for (st in it) {
                        addStopMarker(st)
                    }
                    updateNearbyStopList(it)
                }

            }
        }
    }

    override fun onResume() {
        Timber.i("onResume")
        super.onResume()
        binding.stopMapMapView.onResume()
    }

    override fun onStart() {
        Timber.i("onStart")
        super.onStart()
        binding.stopMapMapView.onStart()
        mPermissionHelper!!.registerListener(this)
    }

    override fun onPause() {
        Timber.i("onPause")
        super.onPause()
        binding.stopMapMapView.onPause()
    }

    override fun onStop() {
        Timber.i("onStop")
        super.onStop()
        binding.stopMapMapView.onStop()
        mPermissionHelper!!.unregisterListener(this)
    }

    override fun onDestroyView() {
        Timber.v("onDestroyView()")
        mStopMarkList.clear()
//        mGoogleMap.clear()
        binding.nearbyRecycleView.adapter = null
        binding.stopMapMapView.onDestroy()
        binding.stopMapMapView.removeAllViews()
        _binding = null
        super.onDestroyView()
    }

    override fun onDestroy() {
        Timber.v("onDestroy()")
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.stopMapMapView.onLowMemory()
    }

    private fun bindStopInfo(data: List<StopInfo>) {
        mAdapter = StopsRecyclerAdapter(data, this, this)
        mCurrentFocusStop = 0
        binding.nearbyRecycleView.adapter = mAdapter
    }

    private fun scrollToStop(index: Int) {
        binding.nearbyRecycleView.layoutManager!!.scrollToPosition(index)
        binding.nearbyRecycleView.adapter!!.notifyItemChanged(previousFocusStop)
        binding.nearbyRecycleView.adapter!!.notifyItemChanged(index)
    }

    private fun addCurrentLocationMarker(location: Location) {
        val here = LatLng(location.latitude, location.longitude)
        mGoogleMap.clear()

        val cameraPosition = CameraPosition.builder()
            .target(here)
            .zoom(16f)
            .build()
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    private fun removeMarkers() {
        mStopMarkList.clear()
        mGoogleMap.clear()
    }

    private fun addStopMarker(st: StopInfo) {
        val nearbyStopLatLng = LatLng(st.location.latitude, st.location.longitude)
        mStopMarkList.add(
            mGoogleMap.addMarker(
                MarkerOptions()
                    .position(nearbyStopLatLng)
                    .icon(bitmapDescriptorFromVector(requireContext(), R.drawable.ic_bus_blue_20dp))
                    .title(st.intersections)
            )
        )
        if (mStopMarkList.size == 1) mStopMarkList[0].showInfoWindow()
        mGoogleMap.setOnMarkerClickListener { marker ->
            val stopList = mAdapter.mStops
            for (position in stopList.indices) {
                val lat = marker.position.latitude.compareTo(stopList[position].location.latitude)
                val lon = marker.position.longitude.compareTo(stopList[position].location.longitude)
                if (lat == 0 && lon == 0) {
                    previousFocusStop = mCurrentFocusStop
                    mCurrentFocusStop = position
                    scrollToStop(position)
                }
            }
            false
        }
    }

    private fun moveCameraTo(latLng: LatLng) {
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
    }

    fun getFocusStop() = mCurrentFocusStop

    @SuppressLint("ResourceType")
    fun enableMyLocationButton() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mGoogleMap.isMyLocationEnabled = true
            binding.stopMapMapView.findViewById<View>(
                0x2
            ).apply {
                val params = layoutParams as RelativeLayout.LayoutParams
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0)
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
                params.bottomMargin = 50.dpToPx
                params.rightMargin = 0.dpToPx
                layoutParams = params
            }
        }
    }

    /**
     * start a new activity and display the search result
     */
    private fun displaySearchResult(userInput: String) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.SEARCH_TERM, userInput)
        FirebaseAnalytics.getInstance(requireContext()).logEvent(FirebaseAnalytics.Event.SEARCH, bundle)
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
            val routeEntered = userInput.toUpperCase(Locale.US)
            val intent = Intent(activity, RoutesViewActivity::class.java)
            intent.putExtra(Intent.EXTRA_TEXT, routeEntered)
            startActivity(intent)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        mPermissionHelper!!.onRequestPermissionResult(requestCode, permissions, grantResults)
    }

    private fun updateNearbyStopList(nearbyStopList: List<StopInfo>) {
        bindStopInfo(nearbyStopList)
    }

    override fun onMovedMap(latLng: LatLng) {
        val location = Location("")
        location.latitude = latLng.latitude
        location.longitude = latLng.longitude
//        findNearByStop(location)
        viewModel.loadNearByStop(location)
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
                    moveCameraTo(latlng)
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
                addCurrentLocationMarker(location.orDummy)
//                findNearByStop(location.orDummy)
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
                        enableMyLocationButton()
//                        findNearByStop(location)
                        addCurrentLocationMarker(location)
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
        private const val PERMISSION_ACCESS_FINE_LOCATION = 1
    }
}