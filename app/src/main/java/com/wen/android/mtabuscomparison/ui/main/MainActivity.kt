package com.wen.android.mtabuscomparison.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.wen.android.mtabuscomparison.R
import com.wen.android.mtabuscomparison.databinding.ActivityMainBinding
import com.wen.android.mtabuscomparison.util.findNavController
import com.wen.android.mtabuscomparison.util.setupWithNavController2
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    companion object {
        /* Key for an int extra defining the initial navigation target. */
        const val EXTRA_NAVIGATION_ID = "extra.NAVIGATION_ID"
        const val EXTRA_NAVIGATION_BUNDLE = "extra.NAVIGATION_BUNDLE"

        private const val NAV_ID_NONE = -1

        fun start(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }
    }

    private val currentNavid = NAV_ID_NONE

    private val vm: MainActivityViewModel by viewModels()

    private val navController by lazy { supportFragmentManager.findNavController(R.id.nav_host_fragment) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigation.setupWithNavController2(navController)

        /*  for UI test */
        if (savedInstanceState == null) {
            val currentNavId = navController.graph.startDestination
            val requestedNavId = intent.getIntExtra(EXTRA_NAVIGATION_ID, currentNavId)
            val requestedBundle = intent.getBundleExtra(EXTRA_NAVIGATION_BUNDLE) ?: Bundle.EMPTY
            navigateTo(requestedNavId, requestedBundle)
        }
    }

    /*  for UI test */
    private fun navigateTo(navId: Int, bundle: Bundle = Bundle.EMPTY) {
        if (navId == currentNavid) {
            return
        }
        navController.navigate(navId, bundle)
    }
}