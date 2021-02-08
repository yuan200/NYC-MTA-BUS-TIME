package com.wen.android.mtabuscomparison.common

interface Observable<ListenerType> {

    fun registerListener(listener: ListenerType)

    fun unregisterListener(listener: ListenerType)
}