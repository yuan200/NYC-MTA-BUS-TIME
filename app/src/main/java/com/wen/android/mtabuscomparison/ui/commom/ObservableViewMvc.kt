package com.wen.android.mtabuscomparison.ui.commom

interface ObservableViewMvc<ListenerType>: ViewMvc {

    fun registerListener(listener: ListenerType)

    fun unregisterListener(listener: ListenerType)
}