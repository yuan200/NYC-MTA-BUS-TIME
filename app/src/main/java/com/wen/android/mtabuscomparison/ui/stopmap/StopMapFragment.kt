package com.wen.android.mtabuscomparison.ui.stopmap

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.view.doOnLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.maps.android.ktx.awaitMap
import com.wen.android.mtabuscomparison.R
import com.wen.android.mtabuscomparison.common.permission.MyPermission
import com.wen.android.mtabuscomparison.common.permission.PermissionHelper
import com.wen.android.mtabuscomparison.common.permission.PermissionHelper.PermissionsResult
import com.wen.android.mtabuscomparison.databinding.FragmentStopMapBinding
import com.wen.android.mtabuscomparison.feature.stopmonitoring.StopInfo
import com.wen.android.mtabuscomparison.util.bitmapDescriptorFromVector
import com.wen.android.mtabuscomparison.util.dpToPx
import com.wen.android.mtabuscomparison.util.fragment.repeatOnViewLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by yuan on 4/10/2017.
 */
@AndroidEntryPoint
class StopMapFragment :
    Fragment(),
    PermissionHelper.Listener,
    StopsRecyclerAdapter.Listener {

    @Inject
    lateinit var permissionHelper: PermissionHelper

    @Inject
    lateinit var firebaseAnalytics: FirebaseAnalytics

    private var currentFocusStop = 0
    private var previousFocusStop = Integer.MAX_VALUE

    //    private val stopMarkerList = mutableListOf<Marker>()
    private val noUpdateDistance = 200
    private var previousCameraLocation = Location("").apply {
        this.latitude = 0.0
        this.longitude = 0.0
    }

    private lateinit var stopAdapter: StopsRecyclerAdapter

    private val viewModel: StopMapViewModel by viewModels()

    private var myLocationJob: Job? = null
    private var myLocationOnPermissionJob: Job? = null

    private var _binding: FragmentStopMapBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.v("onCreate")

        /**
         * request permission
         * nearby stop is the only functionality that needs location permission now
         * if permission is rejected, just disable nearby stop
         */
        if (!permissionHelper.hasPermission(MyPermission.FINE_LOCATION)) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                MyLocationRationaleFragment()
                    .show(childFragmentManager, FRAGMENT_MY_LOCATION_RATIONAL)
                return
            }
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_ACCESS_FINE_LOCATION
            )
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        Timber.v("onCreateView")
        _binding = FragmentStopMapBinding.inflate(inflater, container, false).apply {
            viewModel = this@StopMapFragment.viewModel
        }

        binding.stopMapMapView.apply {
            onCreate(savedInstanceState)
        }

        binding.apply {
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

        NavHostFragment.findNavController(this).apply {
            currentBackStackEntry?.savedStateHandle?.getLiveData<LatLng>(getString(R.string.SEARCH_RESULT_POINT))
                ?.observe(viewLifecycleOwner) { latLng ->
                    Timber.i("result from search $latLng")
                    currentBackStackEntry?.savedStateHandle?.remove<LatLng>(getString(R.string.SEARCH_RESULT_POINT))
                    Handler().postDelayed(Runnable {
                        moveCameraTo(latLng)
                    }, 300)
                }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            binding.stopMapMapView.awaitMap().apply {

                setOnMarkerClickListener { marker ->
                    val stopList = stopAdapter.mStops
                    for (position in stopList.indices) {
                        val lat = marker.position.latitude.compareTo(stopList[position].location.latitude)
                        val lon = marker.position.longitude.compareTo(stopList[position].location.longitude)
                        if (lat == 0 && lon == 0) {
                            previousFocusStop = currentFocusStop
                            currentFocusStop = position
                            scrollToStop(position)
                        }
                    }
                    false
                }
                enableMyLocationButton(this)
                onMapReady(this)
                setOnCameraIdleListener {
                    val currentCameraLocation = Location("").also {
                        it.latitude = this.cameraPosition.target.latitude
                        it.longitude = this.cameraPosition.target.longitude
                    }
                    if (previousCameraLocation.distanceTo(currentCameraLocation) > noUpdateDistance) {
                        onMovedMap(this.cameraPosition.target)
                    }
                    previousCameraLocation = currentCameraLocation
                }
            }
        }

        repeatOnViewLifecycle {
            viewModel.nearByStop.collect {
                withContext(Dispatchers.Main) {
                    removeMarkers()
                    for (stopInfo in it) {
                        addStopMarker(stopInfo)
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

        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, StopMapFragment::class.java.simpleName)
        }
    }

    override fun onStart() {
        Timber.i("onStart")
        super.onStart()
        binding.stopMapMapView.onStart()
        permissionHelper.registerListener(this)
    }

    override fun onPause() {
        Timber.i("onPause")
        super.onPause()
        binding.stopMapMapView.onPause()
    }

    override fun onStop() {
        Timber.i("onStop")
        super.onStop()
        myLocationJob?.cancel()
        myLocationOnPermissionJob?.cancel()
        binding.stopMapMapView.onStop()
        permissionHelper.unregisterListener(this)
    }

    override fun onDestroyView() {
        Timber.v("onDestroyView()")
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
        stopAdapter = StopsRecyclerAdapter(data, this, this)
        currentFocusStop = 0
        binding.nearbyRecycleView.adapter = stopAdapter
    }

    private fun scrollToStop(index: Int) {
        binding.nearbyRecycleView.layoutManager!!.scrollToPosition(index)
        binding.nearbyRecycleView.adapter!!.notifyItemChanged(previousFocusStop)
        binding.nearbyRecycleView.adapter!!.notifyItemChanged(index)
    }

    private fun removeMarkers() {
        viewLifecycleOwner.lifecycleScope.launch {
            val map = binding.stopMapMapView.awaitMap()
            map.clear()
        }
    }

    private fun addStopMarker(st: StopInfo) {
        val nearbyStopLatLng = LatLng(st.location.latitude, st.location.longitude)
        viewLifecycleOwner.lifecycleScope.launch {
            val map = binding.stopMapMapView.awaitMap()
            map.addMarker(
                MarkerOptions()
                    .position(nearbyStopLatLng)
                    .icon(bitmapDescriptorFromVector(requireContext(), R.drawable.ic_bus_blue_20dp))
                    .title(st.intersections)
            )
        }
    }

    private fun moveCameraTo(latLng: LatLng) {
        viewLifecycleOwner.lifecycleScope.launch {
            val map = binding.stopMapMapView.awaitMap()
            Timber.i("move camera to $latLng")
            map.animateCamera(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition.builder()
                        .target(latLng)
                        .zoom(17f)
                        .build()
                )
            )
        }

    }

    fun getFocusStop() = currentFocusStop

    @SuppressLint("ResourceType")
    fun enableMyLocationButton(map: GoogleMap) {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            map.isMyLocationEnabled = true
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
        permissionHelper.onRequestPermissionResult(requestCode, permissions, grantResults)
    }

    private fun updateNearbyStopList(nearbyStopList: List<StopInfo>) {
        bindStopInfo(nearbyStopList)
    }

    private fun onMovedMap(latLng: LatLng) {
        val location = Location("")
        location.latitude = latLng.latitude
        location.longitude = latLng.longitude
        viewModel.loadNearByStop(location)
    }

    override fun onStopClicked(stop: StopInfo) {
        val navController = NavHostFragment.findNavController(this)
        navController.navigate(
            StopMapFragmentDirections.actionStopMapFragmentToStopMonitoringFragment(stop.stopCode)
        )
    }

    private fun onStartSearch() {
        NavHostFragment.findNavController(this).apply {
            navigate(StopMapFragmentDirections.actionStopMapFragmentToSearchFragment())
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == SEARCH_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data!!.hasExtra(getString(R.string.SEARCH_RESULT_STOP_CODE))) {
                    goToStopMonitoringFragment(data.getStringExtra(getString(R.string.SEARCH_RESULT_STOP_CODE))!!)
                } else {
                    val latlng = data.getParcelableExtra<Parcelable>(getString(R.string.SEARCH_RESULT_POINT)) as LatLng
                    moveCameraTo(latlng)
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun goToStopMonitoringFragment(stopCode: String) {
        val navController = NavHostFragment.findNavController(this)
        navController.navigate(
            StopMapFragmentDirections.actionStopMapFragmentToStopMonitoringFragment(stopCode)
        )
    }

    @SuppressLint("MissingPermission")
    private fun onMapReady(map: GoogleMap) {
        Timber.v("onMapReady()")
        if (permissionHelper.hasPermission(MyPermission.FINE_LOCATION)) {
            /**
             * one shot update
             * doesn't want to repeat this after onStop
             *
             */
            myLocationJob = viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.myLocationCameraUpdate
                    .collect {
                        it?.let {
                            Timber.v("collecting from myLocationCameraUpdate, onMapReady()")
                            map.moveCamera(it)
                        }
                    }
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, result: PermissionsResult) {
        when (requestCode) {
            PERMISSION_ACCESS_FINE_LOCATION -> if (result.granted != null
                && result.granted.isNotEmpty()
                && result.granted.contains(
                    MyPermission.FINE_LOCATION
                )
            ) {
                Timber.i("permission granted")

                /**
                 * one shot update
                 * doesn't want to repeat this after onStop
                 */
                myLocationOnPermissionJob = viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                    binding.stopMapMapView.awaitMap().apply {
                        enableMyLocationButton(this)
                        viewModel.myLocationCameraUpdate
                            .collect {
                                it?.let {
                                    Timber.v("collecting from myLocationCameraUpdate")
                                    this.moveCamera(it)
                                }
                            }
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
        private const val FRAGMENT_MY_LOCATION_RATIONAL = "my_location_rational"
    }

    class MyLocationRationaleFragment : DialogFragment() {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            return MaterialAlertDialogBuilder(requireContext())
                .setMessage(R.string.my_location_rational)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    requireParentFragment().requestPermissions(
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        PERMISSION_ACCESS_FINE_LOCATION
                    )
                }
                .setNegativeButton(android.R.string.cancel, null)
                .create()
        }
    }
}