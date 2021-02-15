package com.wen.android.mtabuscomparison.common

import java.util.*
import java.util.concurrent.ConcurrentHashMap

abstract class BaseObservable<ListenerType> {

    private var mListeners: Set<ListenerType> = Collections.newSetFromMap(
        ConcurrentHashMap(1)
    )

    fun registerListener(listener: ListenerType) {
//        mListeners.plus(listener)
        mListeners += listener
    }

    fun unregisterListener(listener: ListenerType) {
//        mListeners.minus(listener)
        mListeners -= listener
    }

    fun getListeners() = mListeners
}