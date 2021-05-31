package com.wen.android.mtabuscomparison

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.CustomTestApplication

@CustomTestApplication(BusTestApplication::class)
class CustomTestRunner : AndroidJUnitRunner() {

    override fun newApplication(cl: ClassLoader?, className: String?, context: Context?): Application {
        /**
         * [CustomTestRunner_Application] generates at runtime!!
         */
        return super.newApplication(cl, CustomTestRunner_Application::class.java.name, context)
    }
}