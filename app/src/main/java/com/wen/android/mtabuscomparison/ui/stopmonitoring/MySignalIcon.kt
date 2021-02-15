package com.wen.android.mtabuscomparison.ui.stopmonitoring

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import androidx.core.content.ContextCompat
import com.wen.android.mtabuscomparison.R

class MySignalIcon @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    deftStyle: Int = 0
) : View(context, attributeSet, deftStyle),
ValueAnimator.AnimatorUpdateListener{

    private var innerPath: Path
    private var innerRect: RectF
    private var mAnimator: ValueAnimator? = null

    private var outerPaint: Paint
    private var innerPaint: Paint
    private var outerPath: Path
    private var outerRect: RectF
    private var width: Float = 500f
    private var primaryColor: Int
    private var darkColor: Int
    private var diff: Int = 0

    init {
        primaryColor = ContextCompat.getColor(getContext(), R.color.primaryColor)
        darkColor= ContextCompat.getColor(getContext(), R.color.primaryColor)
        outerPath = Path()
        innerPath = Path()
        outerPaint = Paint()
        innerPaint = Paint()
        innerRect = RectF()
        outerRect = RectF()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawPath(outerPath, outerPaint)
        canvas.drawPath(innerPath, innerPaint)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        width = w.toFloat()
        diff = ((width * 0.2)).toInt()
        outerRect.set(0f, 0f, width, width)
        outerRect.inset(3f,3f)
        innerRect.set(0 + diff.toFloat(), 0 + diff.toFloat(), width - diff, width -diff)

        outerPaint.style = Paint.Style.STROKE
        outerPaint.color = Color.LTGRAY
        outerPaint.strokeWidth = 3f
        outerPaint.isAntiAlias = true

        innerPaint.style = Paint.Style.STROKE
        innerPaint.color = Color.LTGRAY
        innerPaint.strokeWidth = 3f
        innerPaint.isAntiAlias = true

        outerPath.arcTo(outerRect, 0f, -90f)

        innerPath.arcTo(innerRect, 0f, -90f)

    }

    override fun onAnimationUpdate(animation: ValueAnimator) {
        var value = (animation.animatedValue as Int)
        if (value == 0) {
            innerPaint.color = primaryColor
            outerPaint.color = Color.LTGRAY
        }
        else if (value == 1){
            innerPaint.color = Color.LTGRAY
            outerPaint.color = primaryColor
        } else {
            innerPaint.color = Color.LTGRAY
            outerPaint.color = Color.LTGRAY
        }
        invalidate()
    }

    public fun startAnim() {
        mAnimator = ValueAnimator.ofInt(0, 3)
        mAnimator!!.duration = 1300
        mAnimator!!.addUpdateListener(this)
        mAnimator!!.repeatCount = Animation.INFINITE
        mAnimator!!.start()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mAnimator?.removeAllUpdateListeners()
    }
}