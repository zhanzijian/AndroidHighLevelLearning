package com.example.viewapplication.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.example.viewapplication.R
import com.example.viewapplication.dp
import com.example.viewapplication.getColorById

/**
 *
 * @description 能显示进度的圆环
 * @author zhanzijian
 * @date 2022/03/30 10:22
 */
private val CIRCLE_STROKE_WIDTH = 3f.dp
private val CIRCLE_RADIUS = 40f.dp

class CircleProgressView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = getColorById(context, R.color.yellow_4d_f0cf00_color)
        strokeWidth = CIRCLE_STROKE_WIDTH
        style = Paint.Style.STROKE
    }

    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = getColorById(context, R.color.yellow_f0cf00_color)
        strokeWidth = CIRCLE_STROKE_WIDTH
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas) {
        // 画圆
        canvas.drawCircle(width / 2f, height / 2f, CIRCLE_RADIUS, circlePaint)
        // 画进度
        canvas.drawArc(
            width / 2f - CIRCLE_RADIUS,
            height / 2f - CIRCLE_RADIUS,
            width / 2f + CIRCLE_RADIUS,
            height / 2f + CIRCLE_RADIUS,
            -90f,
            90f,
            false,
            progressPaint
        )
    }
}