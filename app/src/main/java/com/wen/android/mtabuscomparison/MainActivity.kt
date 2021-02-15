package com.wen.android.mtabuscomparison

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.setupWithNavController
import com.wen.android.mtabuscomparison.databinding.ActivityMainBinding
import com.wen.android.mtabuscomparison.feature.ad.FetchAdUnitUseCase
import com.wen.android.mtabuscomparison.util.findNavController
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    companion object {
        fun start(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }
    }

    @Inject lateinit var fetchAdUnitUseCase: FetchAdUnitUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = supportFragmentManager.findNavController(R.id.nav_host_fragment)
        binding.bottomNavigation.setupWithNavController(navController)

        fetchAdUnitUseCase.fetchAdUnit()

    }
}