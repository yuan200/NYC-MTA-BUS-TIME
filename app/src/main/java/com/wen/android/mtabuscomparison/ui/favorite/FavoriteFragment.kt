package com.wen.android.mtabuscomparison.ui.favorite

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.wen.android.mtabuscomparison.BusApplication
import com.wen.android.mtabuscomparison.R
import com.wen.android.mtabuscomparison.databinding.FragmentFavoriteBinding
import com.wen.android.mtabuscomparison.feature.favorite.Favorite
import com.wen.android.mtabuscomparison.feature.favorite.FavoriteStop
import com.wen.android.mtabuscomparison.feature.stop.BusDatabase
import com.wen.android.mtabuscomparison.ui.stopmonitoring.StopMonitoringActivity
import com.wen.android.mtabuscomparison.util.observe2
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FavoriteFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FavoriteFragment : Fragment(), FavoriteAdapter.OnFavoriteClickedListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var mFab: FloatingActionButton? = null
    private var mBusRecyclerView: RecyclerView? = null
    private var mSaveStopView: LinearLayout? = null
    private var mAdapter: FavoriteAdapter? = null
    var mFavorite: List<Favorite> = ArrayList()
    private var FAVORITE_CHECKED = "favorite_checked"
    private var DATABASE_ROW_ID = "row_id"
    private var mBottomSheetBehavior: BottomSheetBehavior<FrameLayout?>? = null
    private val viewModel: FavoriteViewModel by viewModels()
    private lateinit var binding: FragmentFavoriteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Timber.i("onCreateView")
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        val view = binding.root
        mBusRecyclerView = view.findViewById(R.id.bus_recycler_view)
        mSaveStopView = view.findViewById(R.id.dodge_favorite_view)
        mBusRecyclerView?.layoutManager = LinearLayoutManager(activity)
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
            attachToRecyclerView(mBusRecyclerView)
        }

        mBottomSheetBehavior = BottomSheetBehavior.from(binding.favoriteBottomsheet).apply {
            state = BottomSheetBehavior.STATE_EXPANDED
            viewModel.setBackdropOpened(false)
            mOnBackPressedCallback.isEnabled = false
        }

        mFab = view.findViewById(R.id.fab)
        mFab?.setOnClickListener {
            mBottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
            mOnBackPressedCallback.isEnabled = true
            viewModel.setBackdropOpened(true)

        }

        binding.favoriteCloseBackdrop.apply {
            setOnClickListener {
                mBottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
                mOnBackPressedCallback.isEnabled = false
                viewModel.setBackdropOpened(false)
            }
        }

        binding.saveFavoriteSave.setOnClickListener {
            val stopId = binding.saveFavoriteStopId.editText?.text.toString()
            val description = binding.saveFavoriteStopDescription.editText?.text.toString()

            if (stopId.isNullOrBlank()) {
                binding.saveFavoriteStopId.error = "Stop Id can not be empty"
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.IO).launch {
                BusDatabase.getInstance(BusApplication.instance).favoriteStopDao()
                    .insert(
                        FavoriteStop(
                            stopId,
                            null,
                            null,
                            null,
                            null,
                            description.ifBlank { stopId },
                            Date()
                        )
                    )
            }
            mBottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
            mOnBackPressedCallback.isEnabled = false
            viewModel.setBackdropOpened(false)
            binding.saveFavoriteStopId.editText?.setText("")
            binding.saveFavoriteStopDescription.editText?.setText("")

        }
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            mOnBackPressedCallback
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
    }

    private val mOnBackPressedCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            mBottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
            viewModel.setBackdropOpened(false)
            isEnabled = false
        }
    }

    override fun onResume() {
        super.onResume()
        Timber.i("onResume")
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
        mFavorite = mFavoriteList
        return mFavoriteList
    }

    private fun updateUI(favoriteList: List<FavoriteStop>) {
        val favorites = getBusStopCode(favoriteList)
        mAdapter = FavoriteAdapter(favorites, this)
        mBusRecyclerView!!.adapter = mAdapter
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FavoriteFragment2.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FavoriteFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun oncFavoriteClicked(
        stopCodeArray: Array<out String>?,
        check: String?,
        rowId: String?
    ) {
        val intent = Intent(activity, StopMonitoringActivity::class.java)
        intent.putExtra(Intent.EXTRA_TEXT, stopCodeArray)
        intent.putExtra(FAVORITE_CHECKED, "favorite_check")
        intent.putExtra(DATABASE_ROW_ID, rowId)
        startActivity(intent)
    }

}