package com.wen.android.mtabuscomparison.ui.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import com.wen.android.mtabuscomparison.BaseApp
import com.wen.android.mtabuscomparison.R
import com.wen.android.mtabuscomparison.databinding.FragmentFavoriteBinding
import com.wen.android.mtabuscomparison.feature.favorite.Favorite
import com.wen.android.mtabuscomparison.feature.favorite.FavoriteStop
import com.wen.android.mtabuscomparison.feature.stopmonitoring.BusDatabase
import com.wen.android.mtabuscomparison.util.observe2
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@AndroidEntryPoint
class FavoriteFragment : Fragment(), FavoriteAdapter.OnFavoriteClickedListener {

    @Inject
    lateinit var firebaseAnalytics: FirebaseAnalytics

    private var mAdapter: FavoriteAdapter? = null

    private var _mBottomSheetBehavior: BottomSheetBehavior<FrameLayout?>? = null

    private val mBottomSheetBehavior get() = _mBottomSheetBehavior!!

    private val viewModel: FavoriteViewModel by viewModels()

    private var _binding: FragmentFavoriteBinding? = null

    private var mStopId = ""

    private var mDescription = ""

    //binding only valid between onCreatedView and onDestroyView
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Timber.i("onCreateView")
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        val view = binding.root
        binding.busRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        val simpleItemTouchCallBack =
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    mAdapter?.onItemDelete(viewHolder.adapterPosition)
                }
            }
        ItemTouchHelper(simpleItemTouchCallBack).apply {
            attachToRecyclerView(binding.busRecyclerView)
        }

        _mBottomSheetBehavior = BottomSheetBehavior.from(binding.favoriteBottomsheet).apply {
            state = BottomSheetBehavior.STATE_EXPANDED
            viewModel.setBackdropOpened(false)
            mOnBackPressedCallback!!.isEnabled = false
        }

        binding.favoriteFab.setOnClickListener {
            mBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            mOnBackPressedCallback!!.isEnabled = true
            viewModel.setBackdropOpened(true)

        }

        binding.favoriteCloseBackdrop.apply {
            setOnClickListener {
                mBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                mOnBackPressedCallback!!.isEnabled = false
                viewModel.setBackdropOpened(false)
            }
        }

        binding.saveFavoriteSave.setOnClickListener {
            mStopId = binding.saveFavoriteStopId.editText?.text.toString()
            mDescription = binding.saveFavoriteStopDescription.editText?.text.toString()

            if (mStopId.isNullOrBlank()) {
                binding.saveFavoriteStopId.error = "Stop Id can not be empty"
                return@setOnClickListener
            }
            showProgress()
            viewModel.onFetchingStopInfo(mStopId, getString(R.string.mta_bus_api_key))
        }
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            mOnBackPressedCallback!!
        )

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imm: InputMethodManager =
            ContextCompat.getSystemService(
                requireContext(),
                InputMethodManager::class.java
            ) as InputMethodManager

        viewModel.backdropOpened.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { openBackdrop ->
                if (openBackdrop) {
                    binding.favoriteCloseBackdrop.apply {
                        val cx = width / 2
                        val cy = height / 2
                        val finalRadius = Math.hypot(cx.toDouble(), cy.toDouble()).toFloat()
                        val anim =
                            ViewAnimationUtils.createCircularReveal(this, cx, cy, 0f, finalRadius)
                        visibility = View.VISIBLE
                        anim.start()
                        binding.saveFavoriteStopId.requestFocus()
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
                    }
                } else {
                    binding.favoriteCloseBackdrop.apply {
                        visibility = View.INVISIBLE
                    }
                    imm.hideSoftInputFromWindow(binding.saveFavoriteStopId.windowToken, 0)
                }
            })

        viewModel.showEmptyFavorite.observe2(this) {
            if (it) Timber.i("true")
            else Timber.i("false")
        }

        viewModel.favoriteLiveData.observe(viewLifecycleOwner, {
            updateUI(it)
        })

        viewModel.saveResult.observe(viewLifecycleOwner) {
            if (it == "OK") {
                onVerifyStopId()
                viewModel.resetSaveResult()
            } else if (it.isNotBlank()) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                viewModel.resetSaveResult()
            }
            hideProgress()
        }
    }

    private var mOnBackPressedCallback: OnBackPressedCallback? = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            mBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            viewModel.setBackdropOpened(false)
            isEnabled = false
        }
    }

    override fun onResume() {
        super.onResume()
        Timber.i("onResume")
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, FavoriteFragment::class.java.simpleName)
        }
    }

    private fun getBusStopCode(favoriteList: List<FavoriteStop>): List<Favorite>? {
        val mFavoriteList: MutableList<Favorite> = ArrayList()
        for (bus in favoriteList) {
            val favorite = Favorite()
            favorite.rowId = bus._id.toString()
            favorite.groupName = bus.groupName
            favorite.stopCode1 = bus.stopId
            favorite.stopCode2 = bus.stopId2
            favorite.stopCode3 = bus.stopId3
            mFavoriteList.add(favorite)
        }
        mFavoriteList
        return mFavoriteList
    }

    private fun updateUI(favoriteList: List<FavoriteStop>) {
        val favorites = getBusStopCode(favoriteList)
        mAdapter = FavoriteAdapter(favorites, this)
        binding.busRecyclerView.adapter = mAdapter
    }

    override fun oncFavoriteClicked(
        stopId: String,
        check: String?,
        rowId: String?
    ) {
        val action = FavoriteFragmentDirections
            .actionFavoriteFragmentToStopMonitoringFragment(stopId).apply {
                isFavorite = true
                dbRowId = rowId?.toInt() ?: -1
            }
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        binding.busRecyclerView.adapter = null
        _mBottomSheetBehavior = null
        _binding = null
        super.onDestroyView()
        Timber.v("onDestroyView")
    }

    private fun onVerifyStopId() {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                BusDatabase.getInstance(BaseApp.instance).favoriteStopDao()
                    .insert(
                        FavoriteStop(
                            mStopId,
                            null,
                            null,
                            null,
                            null,
                            mDescription.ifBlank { mStopId },
                            Date()
                        )
                    )
            }
        }
        mBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        mOnBackPressedCallback!!.isEnabled = false
        viewModel.setBackdropOpened(false)
        binding.saveFavoriteStopId.editText?.setText("")
        binding.saveFavoriteStopDescription.editText?.setText("")
    }

    private fun showProgress() {
        binding.saveFavoriteSave.isEnabled = false
        //todo indicator doesn't show before call hide()
        binding.favoriteSaveProgressIndicator.hide()
        binding.favoriteSaveProgressIndicator.show()
    }

    private fun hideProgress() {
        binding.saveFavoriteSave.isEnabled = true
        binding.favoriteSaveProgressIndicator.hide()
    }

}