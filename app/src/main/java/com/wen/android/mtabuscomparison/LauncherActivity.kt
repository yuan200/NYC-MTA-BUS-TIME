package com.wen.android.mtabuscomparison

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.wen.android.mtabuscomparison.ui.main.MainActivity

class LauncherActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainActivity.start(this)

        //remove unwanted default animation
        overridePendingTransition(0, 0)
        finish()
    }
}