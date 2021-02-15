package com.wen.android.mtabuscomparison.ui.commom

import java.util.*
import kotlin.collections.HashSet

abstract class BaseObservableViewMvc<ListenerType>: BaseViewMvc(), ObservableViewMvc<ListenerType> {

    private val mListeners = HashSet<ListenerType>()

    override fun registerListener(listener: ListenerType) {
        mListeners += listener
    }
    override fun unregisterListener(listener: ListenerType) {
        mListeners -= listener
    }

    protected fun getListeners() = Collections.unmodifiableSet(mListeners)
}