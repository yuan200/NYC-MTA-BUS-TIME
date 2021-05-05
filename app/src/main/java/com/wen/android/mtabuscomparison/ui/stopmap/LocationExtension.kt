package com.wen.android.mtabuscomparison.ui.stopmap

import android.location.Location

val Location?.orDummy: Location
    get() = this
        ?: Location("Dummy").apply {

            //Let's go to Manhattan!
            latitude = 40.74859491079061
            longitude = -73.98564294403914
        }