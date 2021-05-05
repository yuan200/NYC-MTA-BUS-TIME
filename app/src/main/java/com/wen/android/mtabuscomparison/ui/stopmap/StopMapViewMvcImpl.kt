package com.wen.android.mtabuscomparison.ui.stopmap

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.cardview.widget.CardView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.doOnLayout
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.wen.android.mtabuscomparison.R
import com.wen.android.mtabuscomparison.feature.stopmonitoring.StopInfo
import com.wen.android.mtabuscomparison.ui.commom.BaseObservableViewMvc
import com.wen.android.mtabuscomparison.util.bitmapDescriptorFromVector
import com.wen.android.mtabuscomparison.util.dpToPx

class StopMapViewMvcImpl(
    inflater: LayoutInflater,
    parent: ViewGroup,
    private val fragmentManager: FragmentManager,
    private val mSearchListener: StopMapViewMvc.OnStartSearchListener,
    private val mOnMapReadyListener: StopMapViewMvc.MapListener
) :
    BaseObservableViewMvc<StopMapViewMvc.Listener>(),
    StopMapViewMvc,
    StopsRecyclerAdapter.Listener {

    private lateinit var mLayout: CoordinatorLayout
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
    private val mapListeners = mutableListOf<StopMapViewMvc.OnMovedMapListener>()
    private val noUpdateDistance = 200
    private lateinit var mSearchView: EditText
    private lateinit var mAdview: AdView
    private var mSearchIcon: AppCompatImageView
    private var mSearchLayout: CardView
    private var mSearchHideBtn: AppCompatImageView
    private var supportMapFragment: SupportMapFragment?


    init {
        setRootView(inflater.inflate(R.layout.fragment_stop_map, parent, false))

        supportMapFragment = (fragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment)

        supportMapFragment?.getMapAsync {
            mGoogleMap = it
            mGoogleMap.uiSettings.isMapToolbarEnabled = false
            mGoogleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    getContext(),
                    R.raw.google_map_no_bus_stop_style
                )
            )

            enableMyLocationButton()

            mOnMapReadyListener.onMapReady()

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
        mSearchLayout = findViewById<CardView>(R.id.search_view).apply {
            doOnLayout {
                it.translationX = it.width - (40.dpToPx).toFloat()
            }
        }
        mSearchHideBtn = findViewById<AppCompatImageView>(R.id.search_hide_btn)

        mSearchView = findViewById(R.id.search_et)

        mSearchIcon = findViewById<AppCompatImageView>(R.id.search_icon).apply {
            setOnClickListener {
                mSearchListener.onStartSearch()
            }
        }
        mLayout = findViewById(R.id.stop_map_coordinatorLayout)
//        mAdview = AdView(getContext()).apply {
//            layoutParams = FrameLayout.LayoutParams(
//                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT
//            )
//            (layoutParams as FrameLayout.LayoutParams).gravity = Gravity.CENTER
//
//        }
//        val adRequest = AdRequest.Builder().build()
//        val adUnitKey =
//            stringPreferencesKey(getContext().getString(R.string.preference_key_ad_unit))
//        val adUnitFlow  = getContext().adUnitDataStore.data
//            .map { adUnits ->
//                adUnits.adUnitsList
//            }
//        CoroutineScope(Dispatchers.IO).launch {
//            adUnitFlow
//                .take(1)
//                .collect {
//                if (it.isNotEmpty()) {
//                    val adUnit = it.singleOrNull {
//                        it.location == getString(R.string.ad_unit_location_stop_map)
//                    }
//                    if (adUnit != null) {
//                        if (adUnit.enabled) {
//                            withContext(Dispatchers.Main) {
//                                mAdview.adSize = AdSize.BANNER
//                                mAdview.adUnitId = adUnit.adUnitId
//                                mAdview.loadAd(adRequest)
//                                mLayout.addView(mAdview)
//                                mAdview.bringToFront()
//                            }
//                        }
//                    } else {
////                        withContext(Dispatchers.Main) {
////                            mAdview.adSize = AdSize.BANNER
////                            mAdview.adUnitId = getString(R.string.app_unit_id_stop_map_top)
////                            mAdview.loadAd(adRequest)
////                            mAdview.bringToFront()
////                        }
//                    }
//                }
//            }
//        }
    }

    override fun bindStopInfo(data: List<StopInfo>) {
        mAdapter = StopsRecyclerAdapter(data, this, this)
        mCurrentFocusStop = 0
        mRecyclerView.adapter = mAdapter
    }

    override fun registerMapListener(onMovedMapListener: StopMapViewMvc.OnMovedMapListener) {
        mapListeners += onMovedMapListener
    }

    override fun unregisterMapListener(onMovedMapListener: StopMapViewMvc.OnMovedMapListener) {
        mapListeners -= onMovedMapListener
    }

    override fun scrollToStop(index: Int) {
        mRecyclerView.layoutManager!!.scrollToPosition(index)
        mRecyclerView.adapter!!.notifyItemChanged(previousFocusStop)
        mRecyclerView.adapter!!.notifyItemChanged(index)
    }

    override fun addCurrentLocationMarker(location: Location) {
        val here = LatLng(location.latitude, location.longitude)
        mGoogleMap.clear()
        mGoogleMap.addMarker(
            MarkerOptions().position(here).title("You are here")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_person_pin_circle_black_36dp))
        )
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(here, 16f)
        val cameraPosition = CameraPosition.builder()
            .target(here)
            .zoom(16f)
            .build()
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    override fun removeMarkers() {
        mStopMarkList.clear()
        mGoogleMap.clear()
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

    override fun moveCameraTo(latLng: LatLng) {
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
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
            (fragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment).view!!.findViewById<View>(
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

    override fun getSearchBar(): EditText = mSearchView

    override fun onDestroyView() {
        supportMapFragment?.onDestroy()
    }
}