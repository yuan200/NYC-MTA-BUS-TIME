package com.wen.android.mtabuscomparison.ui

import android.content.Intent
import android.os.Bundle
import androidx.test.rule.ActivityTestRule
import com.wen.android.mtabuscomparison.ui.main.MainActivity

class MainActivityTestRule(
    private val initialNavId: Int,
    private val bundle: Bundle = Bundle.EMPTY
) : ActivityTestRule<MainActivity>(MainActivity::class.java){

    override fun getActivityIntent(): Intent {
        return Intent(Intent.ACTION_MAIN).apply {
            putExtra(MainActivity.EXTRA_NAVIGATION_ID, initialNavId)
            putExtra(MainActivity.EXTRA_NAVIGATION_BUNDLE, bundle)
        }
    }
}