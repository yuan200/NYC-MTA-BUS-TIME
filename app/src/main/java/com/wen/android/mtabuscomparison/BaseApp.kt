package com.wen.android.mtabuscomparison

import androidx.multidex.MultiDexApplication

open class BaseApp: MultiDexApplication() {

    companion object {
        lateinit var instance: BaseApp
            private set
    }

    override fun onCreate() {
        instance = this
        super.onCreate()
    }
}