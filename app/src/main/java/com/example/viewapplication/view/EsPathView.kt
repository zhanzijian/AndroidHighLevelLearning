package com.example.viewapplication.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import com.example.viewapplication.dp

/**
 *
 * @description
 * @author zhanzijian
 * @date 2022/02/24 14:35
 */
private val HORIZONTAL_DISTANCE = 28f.dp //
private val VERTICAL_DISTANCE = 130f.dp
private val ARC_RADIUS = 20f.dp
private val RADIUS = 10f.dp
private val ROUND_RADIUS = 40f.dp
private val PATH_HORIZONTAL_PADDING = 40f.dp
private val ROUND_STROKE_WIDTH = 4f.dp

class EsPathView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private val leftTopPath = Path()
    private val leftTopPathPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.RED
    }

    private val leftTopBallPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.BLACK
    }

    private val leftTopPos = floatArrayOf(RADIUS, RADIUS)
    private val leftTopTan = floatArrayOf(0f, 0f)

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        initLeftTopPath()
    }

    private fun initLeftTopPath(){
        if (!leftTopPath.isEmpty){
            leftTopPath.reset()
        }
        leftTopPath.moveTo(RADIUS, RADIUS)
        leftTopPath.lineTo(RADIUS * 2 + HORIZONTAL_DISTANCE, RADIUS)
        leftTopPath.arcTo(
            RADIUS * 2 + HORIZONTAL_DISTANCE - ARC_RADIUS,
            RADIUS,
            RADIUS * 2 + HORIZONTAL_DISTANCE + ARC_RADIUS,
            ARC_RADIUS * 2 + RADIUS,
            270f,
            90f,
            false
        )
        leftTopPath.lineTo(
            RADIUS * 2 + HORIZONTAL_DISTANCE + ARC_RADIUS,
            VERTICAL_DISTANCE + ARC_RADIUS + RADIUS
        )
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawCircle(leftTopPos[0], leftTopPos[1], RADIUS, leftTopBallPaint)
        canvas.drawPath(leftTopPath, leftTopPathPaint)
    }
    fun startBallAnimator() {
        val pathMeasure = PathMeasure(leftTopPath, false)
        val length = pathMeasure.length
        val valueAnimator = ValueAnimator.ofFloat(0f, length)
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.duration = 5000
        valueAnimator.repeatCount = ValueAnimator.INFINITE
        valueAnimator.addUpdateListener { animation: ValueAnimator ->
            val animatedValue = animation.animatedValue as Float
            pathMeasure.getPosTan(animatedValue, leftTopPos, leftTopTan)
            invalidate()
        }
        valueAnimator.start()
    }

    /**
     * On measure
     * 本控件为项目特用控件，不受开发者宽高限制，控件写死
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // 左右各增加一个圆画笔的宽度以便不挡住线
        val width =
            (RADIUS * 2 + HORIZONTAL_DISTANCE + ARC_RADIUS) * 2 + PATH_HORIZONTAL_PADDING
        val height = (RADIUS + VERTICAL_DISTANCE + ARC_RADIUS) * 2
        setMeasuredDimension(
            (width + ROUND_STROKE_WIDTH).toInt(),
            (height + ROUND_STROKE_WIDTH).toInt()
        )
    }

}