package com.atom.android.muvik.view

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import com.atom.android.muvik.R

class CircleProgressBar(context: Context, attrs: AttributeSet) :
    View(context, attrs) {

    private var strokeWidth = 4f
        set(value) {
            field = strokeWidth
            backgroundPaint.strokeWidth = field
            foregroundPaint.strokeWidth = field
            invalidate()
            requestLayout()
        }

    private var progress = 0f
        set(value) {
            field = progress
            invalidate()
        }

    private var min = 0
        set(value) {
            field = min
            invalidate()
        }
    public var max = 100
        set(value) {
            field = max
            invalidate()
        }

    private val startAngle = -90
    private var color = Color.DKGRAY
        set(value) {
            field = color
            backgroundPaint.color = adjustAlpha(field, 0.3f)
            foregroundPaint.color = field
            invalidate()
            requestLayout()
        }
    private var rectF: RectF? = null
    private var backgroundPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var foregroundPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)


    private fun init(context: Context, attrs: AttributeSet) {
        rectF = RectF()
        val typedArray = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.CircleProgressBar,
            0, 0
        )
        //Reading values from the XML layout
        try {
            strokeWidth = typedArray.getDimension(
                R.styleable.CircleProgressBar_progressBarThickness,
                strokeWidth
            )
            progress = typedArray.getFloat(R.styleable.CircleProgressBar_progress, progress)
            color = typedArray.getInt(R.styleable.CircleProgressBar_progressbarColor, color)
            min = typedArray.getInt(R.styleable.CircleProgressBar_min, min)
            max = typedArray.getInt(R.styleable.CircleProgressBar_max, max)
        } finally {
            typedArray.recycle()
        }

        backgroundPaint.color = adjustAlpha(color, 0.3f)
        backgroundPaint.style = Paint.Style.STROKE
        backgroundPaint.strokeWidth = strokeWidth
        foregroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        foregroundPaint.color = color
        foregroundPaint.style = Paint.Style.STROKE
        foregroundPaint.strokeWidth = strokeWidth
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawOval(rectF!!, backgroundPaint!!)
        val angle = 360 * progress / max
        canvas.drawArc(rectF!!, startAngle.toFloat(), angle, false, foregroundPaint!!)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val height = getDefaultSize(suggestedMinimumHeight, heightMeasureSpec)
        val width = getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)
        val min = Math.min(width, height)
        setMeasuredDimension(min, min)
        rectF?.set(0 + strokeWidth / 2, 0 + strokeWidth / 2, min - strokeWidth / 2,
            min - strokeWidth / 2
        )
    }


    fun lightenColor(color: Int, factor: Float): Int {
        val r = Color.red(color) * factor
        val g = Color.green(color) * factor
        val b = Color.blue(color) * factor
        val ir = Math.min(255, r.toInt())
        val ig = Math.min(255, g.toInt())
        val ib = Math.min(255, b.toInt())
        val ia = Color.alpha(color)
        return Color.argb(ia, ir, ig, ib)
    }


    fun adjustAlpha(color: Int, factor: Float): Int {
        val alpha = Math.round(Color.alpha(color) * factor)
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        return Color.argb(alpha, red, green, blue)
    }


    fun setProgressWithAnimation(progress: Float) {
        val objectAnimator = ObjectAnimator.ofFloat(this, "progress", progress)
        objectAnimator.duration = 1000
        objectAnimator.interpolator = DecelerateInterpolator()
        objectAnimator.start()
    }

    init {
        init(context, attrs)
    }
}
