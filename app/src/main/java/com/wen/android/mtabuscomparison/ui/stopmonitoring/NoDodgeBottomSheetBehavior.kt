package com.wen.android.mtabuscomparison.ui.stopmonitoring

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior

class NoDodgeBottomSheetBehavior<T : View>(context: Context, attributeSet: AttributeSet) :
    BottomSheetBehavior<T>(context, attributeSet) {


    override fun layoutDependsOn(parent: CoordinatorLayout, child: T, dependency: View): Boolean {
        return false
    }
}