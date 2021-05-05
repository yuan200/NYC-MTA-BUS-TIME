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
        fun start(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }
    }

    private val vm: MainActivityViewModel by viewModels()

    private val navController by lazy { supportFragmentManager.findNavController(R.id.nav_host_fragment)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigation.setupWithNavController2(navController)

    }
}