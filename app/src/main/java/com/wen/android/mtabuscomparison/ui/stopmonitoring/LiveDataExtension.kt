package com.wen.android.mtabuscomparison.ui.stopmonitoring

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner? = null, onValueChange: (t: T) -> Unit) {
    val internalObserver = object : Observer<T> {
        override fun onChanged(t: T) {
            onValueChange(t)
            removeObserver(this)
        }
    }

    if (lifecycleOwner == null) {
        observeForever(internalObserver)
    } else {
        observe(lifecycleOwner, internalObserver)
    }
}

