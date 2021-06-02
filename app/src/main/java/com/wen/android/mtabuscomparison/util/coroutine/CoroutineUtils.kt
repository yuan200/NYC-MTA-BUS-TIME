package com.wen.android.mtabuscomparison.util.coroutine

import kotlinx.coroutines.Job

fun Job?.cancelIfActive() {
    if (this?.isActive == true) {
        cancel()
    }
}