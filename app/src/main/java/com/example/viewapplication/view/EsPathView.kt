package com.example.viewapplication.view

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import com.example.viewapplication.dp

/**
 *
 * @description
 * @author zhanzijian
 * @date 2022/02/24 14:35
 */
private val HORIZONTAL_DISTANCE = 28f.dp
private val VERTICAL_DISTANCE = 130f.dp
private val ARC_RADIUS = 20f.dp
private val RADIUS = 10f.dp
private val PATH_HORIZONTAL_PADDING = 40f.dp

class EsPathView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private val path = Path()
    private val pathPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.RED
    }

    private val ballPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.BLACK
    }

    init {
        path.moveTo(RADIUS, RADIUS)
        path.lineTo(RADIUS * 2 + HORIZONTAL_DISTANCE, RADIUS)
        path.arcTo(
            RADIUS * 2 + HORIZONTAL_DISTANCE - ARC_RADIUS,
            RADIUS,
            RADIUS * 2 + HORIZONTAL_DISTANCE + ARC_RADIUS,
            ARC_RADIUS * 2 + RADIUS,
            270f,
            90f,
            false
        )
        path.lineTo(
            RADIUS * 2 + HORIZONTAL_DISTANCE + ARC_RADIUS,
            VERTICAL_DISTANCE + ARC_RADIUS + RADIUS
        )
    }

    override fun onDraw(canvas: Canvas) {

        canvas.drawCircle(pos[0], pos[1], RADIUS, ballPaint)
        canvas.drawPath(path, pathPaint)
    }

    private val pos = floatArrayOf(RADIUS, RADIUS)
    private val tan = floatArrayOf(0f, 0f)
    var isAnimatorStart = false
    fun startBallAnimator() {

        val pathMeasure = PathMeasure(path, false)
        val length = pathMeasure.length
        val valueAnimator = ValueAnimator.ofFloat(0f, length)
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.duration = 5000
        valueAnimator.repeatCount = ValueAnimator.INFINITE
        valueAnimator.addUpdateListener { animation: ValueAnimator ->
            val animatedValue = animation.animatedValue as Float
            pathMeasure.getPosTan(animatedValue, pos, tan)
            invalidate()
        }
        valueAnimator.start()
        isAnimatorStart = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = (RADIUS * 2 + HORIZONTAL_DISTANCE + ARC_RADIUS) * 2 + HORIZONTAL_DISTANCE
        val height = (RADIUS  + VERTICAL_DISTANCE) * 2
        setMeasuredDimension(width.toInt(), height.toInt())
    }
}