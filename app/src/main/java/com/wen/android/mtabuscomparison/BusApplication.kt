package com.wen.android.mtabuscomparison

import androidx.multidex.MultiDexApplication
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import timber.log.Timber.DebugTree

@HiltAndroidApp
class BusApplication: MultiDexApplication() {

    companion object {
        lateinit var instance: BusApplication
            private set
    }
    override fun onCreate() {
        super.onCreate()
        instance = this
        MobileAds.initialize(this)

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
//            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(false)
        }
    }
}