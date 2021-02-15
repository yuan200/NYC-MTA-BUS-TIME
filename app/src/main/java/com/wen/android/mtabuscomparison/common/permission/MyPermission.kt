package com.wen.android.mtabuscomparison.common.permission

import android.Manifest

enum class MyPermission(val androidPermission: String) {
    // declare runtime permissions specific to your app here (don't keep unused ones)
    FINE_LOCATION(Manifest.permission.ACCESS_FINE_LOCATION),
    COARSE_LOCATION(Manifest.permission.ACCESS_COARSE_LOCATION);

    companion object {
        fun fromAndroidPermission(androidPermission: String): MyPermission {
            for (permission in values()) {
                if (permission.androidPermission == androidPermission) {
                    return permission
                }
            }
            throw RuntimeException("Android permission not supported yet: $androidPermission")
        }
    }
}


