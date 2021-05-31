package com.wen.android.mtabuscomparison

import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import timber.log.Timber.DebugTree

@HiltAndroidApp
class BusApplication : BaseApp() {

    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this)

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
//            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(false)
        }
    }
}