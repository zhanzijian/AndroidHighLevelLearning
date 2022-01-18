package com.example.viewapplication

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.sin

/**
 *
 * @description 饼图
 * @author zhanzijian
 * @date 2022/01/18 21:30
 */
private val RADIUS = 100f.dp
private val PIE_MOVE_LENGTH = 10f.dp

class PieView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val angelArray = floatArrayOf(60f, 120f, 90f, 90f)
    private val colorArray = intArrayOf(Color.BLUE, Color.CYAN, Color.GREEN, Color.YELLOW)
    override fun onDraw(canvas: Canvas) {
        var startAngel = 0f
        for ((index, angel) in angelArray.withIndex()) {
            paint.color = colorArray[index]
            if (index == 0) {
                canvas.save()
                //注意这里角度的计算 startAngel + angel.toDouble()/2
                val dx = cos(Math.toRadians(startAngel + angel.toDouble() / 2)) * PIE_MOVE_LENGTH
                val dy = sin(Math.toRadians(startAngel + angel.toDouble() / 2)) * PIE_MOVE_LENGTH
                canvas.translate(dx.toFloat(), dy.toFloat())
            }
            canvas.drawArc(
                width / 2 - RADIUS,
                height / 2 - RADIUS,
                width / 2 + RADIUS,
                height / 2 + RADIUS,
                startAngel,
                angel,
                true,
                paint
            )
            startAngel += angel
            if (index == 0) {
                canvas.restore()
            }
        }
    }
}