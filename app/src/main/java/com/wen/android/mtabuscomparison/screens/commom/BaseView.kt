package com.wen.android.mtabuscomparison.screens.commom

import android.view.View
import androidx.annotation.StringRes

abstract class BaseView: IView{

    private lateinit var mRootView: View

    override fun getRootView() = mRootView

    protected fun setRootView(rootView: View) {
        mRootView = rootView
    }

    protected fun <T : View>findViewById(id: Int): T = getRootView().findViewById(id)

    protected fun getContext() = getRootView().context!!

    protected fun getString(@StringRes id: Int) = getContext().getString(id)
}