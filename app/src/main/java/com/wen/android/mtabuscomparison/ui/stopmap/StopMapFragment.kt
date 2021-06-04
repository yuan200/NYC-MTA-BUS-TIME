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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.wen.android.mtabuscomparison.R
import com.wen.android.mtabuscomparison.common.permission.MyPermission
import com.wen.android.mtabuscomparison.common.permission.PermissionHelper
import com.wen.android.mtabuscomparison.common.permission.PermissionHelper.PermissionsResult
import com.wen.android.mtabuscomparison.databinding.FragmentStopMapBinding
import com.wen.android.mtabuscomparison.feature.stopmonitoring.StopInfo
import com.wen.android.mtabuscomparison.ui.search.SearchActivity
import com.wen.android.mtabuscomparison.util.bitmapDescriptorFromVector
import com.wen.android.mtabuscomparison.util.dpToPx
import com.wen.android.mtabuscomparison.util.fragment.repeatOnViewLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Created by yuan on 4/10/2017.
 */
@AndroidEntryPoint
class StopMapFragment :
    Fragment(),
    PermissionHelper.Listener,
    StopsRecyclerAdapter.Listener {
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

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        repeatOnViewLifecycle {
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        mPermissionHelper!!.onRequestPermissionResult(requestCode, permissions, grantResults)
    }

    private fun updateNearbyStopList(nearbyStopList: List<StopInfo>) {
        bindStopInfo(nearbyStopList)
    }

    private fun onMovedMap(latLng: LatLng) {
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

    private fun onStartSearch() {
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
    private fun onMapReady() {
        if (mPermissionHelper!!.hasPermission(MyPermission.FINE_LOCATION)) {

            repeatOnViewLifecycle {
                viewModel.myLocation.collect {
                    addCurrentLocationMarker(it)
                }
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
                repeatOnViewLifecycle {
                    viewModel.myLocation.collect {
                        enableMyLocationButton()
//                        findNearByStop(location)
                        addCurrentLocationMarker(it)
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