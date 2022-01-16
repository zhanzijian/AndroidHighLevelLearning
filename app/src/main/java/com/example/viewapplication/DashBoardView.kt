package com.example.viewapplication

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.sin

/**
 *
 * @description 仪表盘
 * @author zhanzijian
 * @date 2022/01/16 20:34
 */
private val RADIUS = 100f.dp //圆弧半径
private const val OPEN_ANGLE = 120f //圆盘开角
private val DASH_WIDTH = 2f.dp //刻度的宽度
private val DASH_LENGTH = 5f.dp //刻度的长度
private const val TOTAL_DASH_COUNT = 20 //刻度的数量
private const val DASH_DIRECT_COUNT = 6 //刻度的数量
private val DIRECTION_LENGTH = RADIUS - DASH_LENGTH * 2 //指示器长度
class DashBoardView(context: Context,attributeSet: AttributeSet) : View(context,attributeSet){
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 2f.dp
    }
    private lateinit var pathDashPathEffect: PathDashPathEffect
    private val path = Path()
    private val dashPath = Path()

    init {
        val rectF = RectF(0f,0f,DASH_WIDTH, DASH_LENGTH)
        dashPath.addRect(rectF,Path.Direction.CCW)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        path.reset()
        path.addArc(width / 2 - RADIUS,height / 2 - RADIUS, width / 2 + RADIUS, height / 2 + RADIUS,
            OPEN_ANGLE / 2 + 90f,360f - OPEN_ANGLE)
        val pathMeasure = PathMeasure(path,false)
        pathDashPathEffect = PathDashPathEffect(dashPath,(pathMeasure.length - DASH_WIDTH) / TOTAL_DASH_COUNT,0f,PathDashPathEffect.Style.ROTATE)

    }
    override fun onDraw(canvas: Canvas) {
        //画刻度
        paint.pathEffect = pathDashPathEffect
        canvas.drawPath(path, paint)
        paint.pathEffect = null
        //画圆盘
        canvas.drawPath(path,paint)

        //画指示器
        canvas.drawLine(width / 2f,height / 2f,
            (width / 2 + cos(markToRadians(DASH_DIRECT_COUNT)) * DIRECTION_LENGTH).toFloat(),
            (height / 2 + sin(markToRadians(DASH_DIRECT_COUNT)) * DIRECTION_LENGTH).toFloat(),
            paint
        )
    }

    /**
     * Mark to radians
     * java中Math.toRadians()用于将以度为单位的角度转换为以弧度测量的大致相等的角度,也就是说将度转化为-π/2到π/2之间的弧度值。如果要将弧度转成成角度用Math.toDegrees。
     * @param mark
     * @return
     */
    private fun markToRadians(mark: Int): Double{
        return Math.toRadians((90 + (360 - OPEN_ANGLE) / TOTAL_DASH_COUNT * mark + OPEN_ANGLE / 2).toDouble())
    }
}