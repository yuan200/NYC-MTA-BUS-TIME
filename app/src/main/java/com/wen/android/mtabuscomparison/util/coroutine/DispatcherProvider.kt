package com.wen.android.mtabuscomparison.util.coroutine

import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

interface DispatcherProvider {
    val Default: CoroutineContext
        get() = Dispatchers.Default
    val Main: CoroutineContext
        get() = Dispatchers.Main
    val MainImmediate: CoroutineContext
        get() = Dispatchers.Main.immediate
    val Unconfined: CoroutineContext
        get() = Dispatchers.Unconfined
    val IO: CoroutineContext
        get() = Dispatchers.IO
}