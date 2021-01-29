package com.wen.android.mtabuscomparison.screens.stopmap

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.wen.android.mtabuscomparison.R
import com.wen.android.mtabuscomparison.screens.commom.BaseObservableView
import com.wen.android.mtabuscomparison.stop.StopInfo
import com.wen.android.mtabuscomparison.utilities.bitmapDescriptorFromVector

class StopMapViewImpl(
    inflater: LayoutInflater,
    parent: ViewGroup,
    fragmentManager: FragmentManager
) :
    BaseObservableView<StopMapView.Listener>(),
    StopMapView,
    StopsRecyclerAdapter.Listener {

    private var mCurrentFocusStop = 0
    private var previousFocusStop = Integer.MAX_VALUE
    private lateinit var mGoogleMap: GoogleMap
    private var mRecyclerView: RecyclerView
    private lateinit var mAdapter: StopsRecyclerAdapter
    private val mStopMarkList = mutableListOf<Marker>()
    private var mPreviousCameraLocation = Location("").apply {
        this.latitude = 0.0
        this.longitude = 0.0
    }
    private val mapListeners = mutableListOf<StopMapView.OnMovedMapListener>()
    private val noUpdateDistance = 200

    init {
        setRootView(inflater.inflate(R.layout.fragment_stop_map, parent, false))

        (fragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment).getMapAsync {
            mGoogleMap = it
            mGoogleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    getContext(),
                    R.raw.google_map_no_bus_stop_style
                )
            )

            enableMyLocationButton()

            mGoogleMap.setOnCameraIdleListener {
                val currentCameraLocation = Location("").apply {
                    this.latitude = it.cameraPosition.target.latitude
                    this.longitude = it.cameraPosition.target.longitude
                }
                if (mPreviousCameraLocation.distanceTo(currentCameraLocation) > noUpdateDistance) {
                    for (listen in mapListeners) {
                        listen.onMovedMap(it.cameraPosition.target)
                    }
                }
                mPreviousCameraLocation = currentCameraLocation
            }
        }
        mRecyclerView = getRootView().findViewById(R.id.nearbyRecycleView)!!
        mRecyclerView.layoutManager = LinearLayoutManager(getContext())
    }

    override fun bindStopInfo(data: List<StopInfo>) {
        mAdapter = StopsRecyclerAdapter(data, this, this)
        mCurrentFocusStop = 0
        mRecyclerView.adapter = mAdapter
    }

    override fun registerMapListener(onMovedMapListener: StopMapView.OnMovedMapListener) {
        mapListeners += onMovedMapListener
    }

    override fun unregisterMapListener(onMovedMapListener: StopMapView.OnMovedMapListener) {
        mapListeners -= onMovedMapListener
    }

    override fun scrollToStop(index: Int) {
        mRecyclerView.layoutManager!!.scrollToPosition(index)
        mRecyclerView.adapter!!.notifyItemChanged(previousFocusStop)
        mRecyclerView.adapter!!.notifyItemChanged(index)
    }

    override fun addCurrentLocationMarker(location: Location) {
        val here = LatLng(location.latitude, location.longitude)
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(here, 16f)
        mGoogleMap.clear()
        mGoogleMap.addMarker(
            MarkerOptions().position(here).title("You are here")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_person_pin_circle_black_36dp))
        )
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(here))
        mGoogleMap.animateCamera(cameraUpdate)
    }

    override fun removeMarkers() {
        mStopMarkList.clear()
    }

    override fun addStopMarker(st: StopInfo) {
        val nearbyStopLatLng = LatLng(st.location.latitude, st.location.longitude)
        mStopMarkList.add(
            mGoogleMap.addMarker(
                MarkerOptions()
                    .position(nearbyStopLatLng)
                    .icon(bitmapDescriptorFromVector(getContext(), R.drawable.ic_bus_blue_20dp))
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

    override fun onStopClicked(stop: StopInfo) {
        for (listener in getListeners()) {
            listener.onStopClicked(stop)
        }
    }

    override fun getFocusStop() = mCurrentFocusStop

    override fun enableMyLocationButton() {
        if (ActivityCompat.checkSelfPermission(
                getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mGoogleMap.isMyLocationEnabled = true
        }
    }
}