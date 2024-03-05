package com.wen.android.mtabuscomparison.ui.favorite.coachmark

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.MeasureSpec
import android.widget.LinearLayout
import android.widget.PopupWindow
import com.wen.android.mtabuscomparison.R

@SuppressLint("InflateParams")
class Coachmark(val context: Context, private val anchorView: View) {
    private lateinit var popupWindow: PopupWindow

    init {
        val contentView = LayoutInflater.from(context).inflate(R.layout.coachmark, null)
        popupWindow = PopupWindow(
            contentView,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true)

    }

    fun show() {
        val contentView = popupWindow.contentView
        contentView.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
        val location = IntArray(2)
        anchorView.getLocationOnScreen(location)
        val popupX = contentView.measuredWidth
        var popupY =  contentView.measuredHeight
        val x = location[0]
        val y = location[1] - popupY
        popupWindow.showAtLocation(anchorView, Gravity.NO_GRAVITY, x, y)


    }
}