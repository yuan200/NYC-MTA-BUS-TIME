package com.wen.android.mtabuscomparison.screens.commom

interface IObservableView<ListenerType>: IView {

    fun registerListener(listener: ListenerType)

    fun unregisterListener(listener: ListenerType)
}