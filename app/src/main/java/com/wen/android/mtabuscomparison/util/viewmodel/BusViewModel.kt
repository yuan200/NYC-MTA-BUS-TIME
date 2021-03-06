package com.wen.android.mtabuscomparison.util.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wen.android.mtabuscomparison.util.coroutine.DefaultDispatcherProvider
import com.wen.android.mtabuscomparison.util.coroutine.DispatcherProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

abstract class BusViewModel constructor(
    private val dispatcherProvider: DispatcherProvider = DefaultDispatcherProvider()
) : ViewModel() {

    private val tag: String = this::class.simpleName!!
    var launchErrorHandler: CoroutineExceptionHandler? = null

    init {
        Timber.tag(tag).v("Initialized")
    }

    /**
     * this launches a coroutine on another thread
     * Remember to switch to the main thread if you want to update the UI directly
     */
    fun launch(
        scope: CoroutineScope = viewModelScope,
        context: CoroutineContext = dispatcherProvider.Default,
        errorHandler: CoroutineExceptionHandler? = launchErrorHandler,
        block: suspend CoroutineScope.() -> Unit
    ) {
        val combineContext = errorHandler?.let { context + it } ?: context
        try {
            scope.launch(context = combineContext, block = block)
        } catch (e: CancellationException) {
            Timber.w(e, "launch()ed coroutine was canceled (scope=%s).", scope)
        }
    }

    fun <T> Flow<T>.launchInViewModel() = this.launchIn(viewModelScope + dispatcherProvider.Default)
}