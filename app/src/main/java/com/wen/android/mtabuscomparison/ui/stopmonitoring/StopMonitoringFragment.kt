package com.wen.android.mtabuscomparison.ui.stopmonitoring

import android.content.res.Configuration
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.wen.android.mtabuscomparison.R
import com.wen.android.mtabuscomparison.common.Result
import com.wen.android.mtabuscomparison.databinding.FragmentStopMonitoringBinding
import com.wen.android.mtabuscomparison.feature.favorite.FavoriteStop
import com.wen.android.mtabuscomparison.feature.stopmonitoring.BusDatabase
import com.wen.android.mtabuscomparison.feature.stopmonitoring.Stop
import com.wen.android.mtabuscomparison.feature.stopmonitoring.StopMonitoringData
import com.wen.android.mtabuscomparison.feature.stopmonitoring.StopMonitoringListItem
import com.wen.android.mtabuscomparison.util.bitmapDescriptorFromVector
import com.wen.android.mtabuscomparison.util.dpToPx
import com.wen.android.mtabuscomparison.util.fragment.repeatOnViewLifecycle
import com.wen.android.mtabuscomparison.util.pxToDp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.*
import java.util.concurrent.Executors
import javax.inject.Inject

@AndroidEntryPoint
class StopMonitoringFragment : Fragment(),
    StopMonitoringViewMvc.FavoriteListener {

    @Inject
    lateinit var busDatabase: BusDatabase

    private var mGoogleMap: GoogleMap? = null

    private lateinit var mStopPointName: String

    private val mStopIds: MutableSet<String> = mutableSetOf()

    private lateinit var apiKey: String

    private var mRowId: Int = Integer.MAX_VALUE

    private val mViewModel: StopMonitoringViewModel by viewModels()

    private lateinit var binding: FragmentStopMonitoringBinding
//    by viewBindingLazy()

    private val args: StopMonitoringFragmentArgs by navArgs()

    private val busMarkers: MutableList<Marker> = mutableListOf()

    private lateinit var supportMapFragment: SupportMapFragment

    private var mStop: Stop? = null

    private lateinit var mAlertView: TextView
    private lateinit var mAdview: AdView
    private lateinit var mFavoriteButton: AppCompatImageView
    private var mFavorite: Boolean = false
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StopMonitoringAdapter
    private lateinit var appBar: Toolbar
    private lateinit var tooBar: Toolbar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentStopMonitoringBinding.inflate(layoutInflater, container, false).apply {
            stopMonitoringRecyclerView.apply {
                addItemDecoration(TopCornerItemDecoration())
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Timber.i("onViewCreated")

        repeatOnViewLifecycle {
            mViewModel.stopStopMonitoringData.collect {
                when (it) {
                    Result.Loading -> {
                        Timber.v("stopMonitoringData received loading")
                        binding.stopMonitoringProgressBar.show()

                    }
                    is Result.Success -> {
                        Timber.v("stopMonitoringData received success")
                        onStopMonitoringFetched(it.data)
                        binding.stopMonitoringProgressBar.hide()
                        binding.stopMonitoringProgressBar.visibility = View.GONE
                    }
                    is Result.Failure -> {
                        Timber.v("stopMonitoringData received failure")
                        binding.stopMonitoringProgressBar.hide()
                        binding.stopMonitoringProgressBar.visibility = View.GONE

                    }
                }
            }
        }

        apiKey = getString(R.string.mta_bus_api_key)

        appBar = binding.stopMonitoringAppBar
        tooBar = binding.stopMonitoringToolBar
        mFavoriteButton = binding.stopFavorite
        mFavoriteButton.setOnClickListener {
            onClickedFavorite()
        }
        recyclerView = binding.stopMonitoringRecyclerView
        adapter = StopMonitoringAdapter(emptyList())
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val adRequest = AdRequest.Builder().build()

        mAdview = binding.stopMonitoringAd
        mAdview.loadAd(adRequest)

        mRowId = args.dbRowId
        setFavorite(args.isFavorite)
        val bottomSheetBehavior = BottomSheetBehavior.from(binding.stopMonitoringBsContainer)

        binding.stopMonitoringBusCodeRv.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }

        repeatOnViewLifecycle {
            mViewModel.publishedLineAdapterData.collect {
                if (it.size > 0) {
                    binding.stopMonitoringBusCodeRv.adapter = BusCodeAdapter(it) { busCode ->
                        mViewModel.onPublishLineClicked(busCode)
                    }
                }
            }
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {

            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                Timber.i("bottomsheet top: ${bottomSheet.height.pxToDp}")
                val maxPadding = bottomSheet.height.pxToDp - 150
                when (bottomSheetBehavior.state) {
                    BottomSheetBehavior.STATE_DRAGGING -> {
                        if (mStop != null) {
                            mGoogleMap?.moveCamera(
                                CameraUpdateFactory.newLatLng(
                                    LatLng(
                                        mStop!!.stopLat,
                                        mStop!!.stopLon
                                    )
                                )
                            )
                        }
                        setMapPaddingBottom(slideOffset, maxPadding)
                    }
                    BottomSheetBehavior.STATE_SETTLING -> {
                        if (mStop != null) {
                            mGoogleMap?.moveCamera(
                                CameraUpdateFactory.newLatLng(
                                    LatLng(
                                        mStop!!.stopLat,
                                        mStop!!.stopLon
                                    )
                                )
                            )
                        }
                        setMapPaddingBottom(slideOffset, maxPadding)
                    }
                    else -> {
                    }
                }
            }

        })

        supportMapFragment = (childFragmentManager.findFragmentById(R.id.stop_monitoring_map) as SupportMapFragment)
        supportMapFragment.getMapAsync { googleMap ->
            mGoogleMap = googleMap

            mGoogleMap!!.uiSettings.isMapToolbarEnabled = false
            mGoogleMap!!.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(),
                    R.raw.stop_monitoring_map
                )
            )

            Timber.i("google map is ready")
            repeatOnViewLifecycle {
                mViewModel.stop.collect {
                    mStop = it
                    setTitle(mStop?.stopName.orEmpty())
                    Timber.i("stop latlng: ${it.toString()}")
                    if (mStop != null) {
                        googleMap.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(
                                    mStop!!.stopLat,
                                    mStop!!.stopLon
                                ), 15f
                            )
                        )
                        googleMap.addMarker(
                            MarkerOptions()
                                .position(LatLng(mStop!!.stopLat, mStop!!.stopLon))
                                .icon(
                                    bitmapDescriptorFromVector(
                                        requireContext(),
                                        R.drawable.ic_directions_bus_stop_48dp
                                    )
                                )
                                .title("stop")
                        )
                    }

                }
            }


            repeatOnViewLifecycle {
                mViewModel.targetVehicleLocation.collect {
                    clearOldBusMarker()
                    if (it != null) {
                        busMarkers.add(
                            googleMap.addMarker(
                                MarkerOptions()
                                    .position(it)
                                    .icon(
                                        bitmapDescriptorFromVector(
                                            requireContext(),
                                            R.drawable.ic_directions_bus_filled_48dp
                                        )
                                    )
                                    .title(mViewModel.targetPublishedLineName.value)
                            )
                        )
                    }
                }

            }

            repeatOnViewLifecycle {
                mViewModel.vehicleAndStopBounds.collect {
                    mGoogleMap!!.moveCamera(
                        CameraUpdateFactory.newLatLngBounds(
                            it,
                            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) 300
                            else 0 // map doesn't have enough space in landscape mode and the padding will crash the app
                            //Error using newLatLngBounds(LatLngBounds, int): View size is too small after padding is applied.
                        )
                    )
                }

            }
        }

    }

    override fun onResume() {
        super.onResume()
        Timber.i("onResume")

        mAdview.resume()
    }

    override fun onPause() {
        mAdview.pause()
        super.onPause()
    }

    override fun onDestroy() {
        Timber.v("onDestroy")
        mAdview.removeAllViews()
        binding.stopMonitoringAdContainer.removeAllViews()
        mAdview.destroy()

        super.onDestroy()
    }

    private fun setMapPaddingBottom(slideOffset: Float, maxPadding: Int) {
        mGoogleMap?.setPadding(0, 0, 0, (slideOffset * maxPadding).toInt().dpToPx)
    }

    override fun onStart() {
        super.onStart()
        mViewModel.loadStopMonitoringData(
            apiKey,
            stopId = args.stopId
        )
        Timber.i("onStart")
    }


    override fun onStop() {
        Timber.v("onStop")
        super.onStop()
    }

    override fun onDestroyView() {
        Timber.v("onDestroyView")
        if (mGoogleMap != null) {
            mGoogleMap!!.clear()
            mGoogleMap = null

        }
        supportMapFragment.onDestroyView()
        supportMapFragment.onDestroy()

        super.onDestroyView()
    }

    private fun onStopMonitoringFetched(stopMonitoringData: StopMonitoringData) {
        if (stopMonitoringData.busMonitoring.isNotEmpty()) {
            mStopIds.add(stopMonitoringData.busMonitoring[0].stopNumber)
            mStopPointName = stopMonitoringData.busMonitoring[0].stopPointName
        }
        checkError(stopMonitoringData)
        setAdapterData(stopMonitoringData.busMonitoring)
    }

    fun addToFavorite() {
        //todo disable this button if no info
        if (!mStopIds.isNullOrEmpty()) {
            val favorite = FavoriteStop(
                mStopIds.elementAtOrElse(0) { "" },
                mStopIds.elementAtOrElse(1) { "" },
                mStopIds.elementAtOrElse(2) { "" },
                "",
                "",
                mStopPointName,
                Date()
            )
            viewLifecycleOwner.lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    mRowId = busDatabase.favoriteStopDao().insert(favorite).toInt()
                }
            }
        }
    }

    fun removeFromFavorite() {
        Executors.newSingleThreadExecutor().execute {
            busDatabase.favoriteStopDao().delete(mRowId)
        }
    }

    override fun onClickedFavorite() {
        if (getFavorite()) {
            setFavorite(false)
            removeFromFavorite()
        } else {
            setFavorite(true)
            addToFavorite()
        }
    }

    private fun clearOldBusMarker() {
        for (marker in busMarkers) {
            marker.remove()
        }
        busMarkers.clear()
    }

    fun setTitle(stopName: String) {
        appBar.title = stopName
    }

    fun setAdapterData(busMonitoring: List<StopMonitoringListItem>) {
        recyclerView.adapter = StopMonitoringAdapter(busMonitoring)
    }

    fun checkError(stopMonitoringData: StopMonitoringData) {
        if (stopMonitoringData.situations != null) {
            tooBar.visibility = View.VISIBLE
            binding.stopMonitoringAlertImg.apply {
                visibility = View.VISIBLE
            }
            mAlertView = binding.stopMonitoringStopAdvisory
            mAlertView.visibility = View.VISIBLE
            mAlertView.bringToFront()
            mAlertView.setOnClickListener {
                var alertSummary = ""
                for (situation in stopMonitoringData.situations.PtSituationElement) {
                    alertSummary += situation.Description + "\n\n\n"
                }
                AlertDialog.Builder(requireContext()).setMessage(alertSummary).create().show()
            }
        }
        if (stopMonitoringData.errorMessage.isNotEmpty()) {
            Snackbar.make(
                binding.stopMonitoringCoordinator,
                stopMonitoringData.errorMessage,
                Snackbar.LENGTH_LONG
            ).show()
        } else if (stopMonitoringData.busMonitoring.isEmpty()) {
            Snackbar.make(
                binding.stopMonitoringCoordinator,
                "Sorry, MTA is not providing stop monitoring data for this stop right now:(",
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    fun getFavorite(): Boolean = mFavorite
    fun setFavorite(isFavorite: Boolean) {
        mFavorite = isFavorite
        when (mFavorite) {
            true -> mFavoriteButton.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_favorite_pink_24dp)
            else -> mFavoriteButton.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_favorite_border_pink_24dp)
        }
    }
}

class TopCornerItemDecoration : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        if (parent.getChildAdapterPosition(view) == 0) {
            view.background = ContextCompat.getDrawable(view.context, R.drawable.recycler_item_corner)
        }
    }
}