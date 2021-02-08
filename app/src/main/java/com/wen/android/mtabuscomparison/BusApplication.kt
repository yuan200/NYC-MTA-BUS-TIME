package com.wen.android.mtabuscomparison

import androidx.multidex.MultiDexApplication
import timber.log.Timber
import timber.log.Timber.DebugTree

class BusApplication: MultiDexApplication() {

    companion object {
        lateinit var instance: BusApplication
            private set
    }
    override fun onCreate() {
        super.onCreate()
        instance = this

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        } else {
            //todo plant production tree here
        }
    }
}