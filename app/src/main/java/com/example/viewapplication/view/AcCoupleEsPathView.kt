package com.example.viewapplication.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import com.example.viewapplication.R
import com.example.viewapplication.dp
import com.example.viewapplication.getColorById
import kotlin.math.sin
import kotlin.math.tan

/**
 *
 * @description Ac Couple 储能电站路径图
 * pvToInverter  pv -> 逆变器
 * InverterToCenter  逆变器 -> 中心
 * CenterToCenterTop  中心 -> 中上
 * CenterTopToGridLoad  中心 -> 电网负载
 * CenterToAc  中心 -> Ac Couple
 * BatteryToAc  电池 -> Ac Couple
 * AcToBackupLoad  Ac Couple -> backup 负载
 * CenterTopToGrid  中上 -> 电网
 * @author zijian.zhan
 * @date 2022/08/12 11:05
 */

private val PATH_STROKE_WIDTH = 2f.dp
private val CENTER_CIRCLE_RADIUS = 5f.dp // 中心圆半径
private val ELEMENT_CIRCLE_RADIUS = 32f.dp // 组件圆半径

private val ARROW_WIDTH = 30f.dp // 箭头宽度
private val ARROW_HEIGHT = 30f.dp // 箭头高度
private const val ARROW_CONCAVE_ANGLE = 80f // 箭头内凹角
private val VIEW_WIDTH = 260f.dp // view 宽度
private val VIEW_HEIGHT = 210f.dp // view 高度

class AcCoupleEsPathView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private val linePaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = PATH_STROKE_WIDTH
            color = getColorById(context, R.color.blue_5f91cb_color)
        }
    }

    private val circlePaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = getColorById(context, R.color.blue_5f91cb_color)
        }
    }

    private val arrowPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = getColorById(context, R.color.orange_fda23a_color)
        }
    }

    // ************* 箭头路径 ************** //
    private val pvToInverterArrowPath by lazy(::Path)

    /**
     * 箭头内凹长度
     */
    private val arrowConcaveLength by lazy { (ARROW_WIDTH / 2f / tan(Math.toRadians((ARROW_CONCAVE_ANGLE / 2).toDouble()))).toFloat() }

    /**
     * 线在X轴上的间距
     */
    private var dx = 0f

    /**
     * 线在Y轴上的间距
     */
    private var dy = 0f

    /**
     * view 中心坐标 X
     */
    private var centerX = 0f

    /**
     * view 中心坐标 Y
     */
    private var centerY = 0f

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        dx = VIEW_WIDTH / 2f
        dy = VIEW_HEIGHT / 2f
        centerX = width / 2f
        centerY = height / 2f

        pvToInverterArrowPath.moveTo(centerX - dx, centerY - dy + arrowConcaveLength)
        pvToInverterArrowPath.lineTo(centerX - dx - ARROW_WIDTH / 2f, centerY - dy)
        pvToInverterArrowPath.lineTo(centerX - dx + ARROW_WIDTH / 2f, centerY - dy)
//        pvToInverterArrowPath.lineTo(centerX - dx, centerY - dy + ARROW_HEIGHT)
    }

    override fun onDraw(canvas: Canvas) {
        // 画线
        // 中心点


        // PV -> 逆变器
        // 路径
        canvas.drawLine(centerX - dx, centerY - dy, centerX - dx, centerY, linePaint)
        // 箭头
        canvas.drawPath(pvToInverterArrowPath, arrowPaint)

        // 逆变器 -> 中心
        canvas.drawLine(centerX - dx, centerY, centerX, centerY, linePaint)

        // 中心 -> 中上
        canvas.drawLine(centerX, centerY, centerX, centerY - dy, linePaint)

        // 中心 -> 电网负载
        canvas.drawLine(centerX, centerY, centerX + dx, centerY, linePaint)
        // 中心 -> Ac Couple
        canvas.drawLine(centerX, centerY, centerX, centerY + dy, linePaint)

        // 电池 -> Ac Couple
        canvas.drawLine(centerX - dx, centerY + dy, centerX, centerY + dy, linePaint)

        // Ac Couple -> backup 负载
        canvas.drawLine(centerX, centerY + dy, centerX + dx, centerY + dy, linePaint)

        // 中上 -> 电网
        canvas.drawLine(centerX, centerY - dy, centerX + dx, centerY - dy, linePaint)

        // 中心原点
        canvas.drawCircle(centerX, centerY, CENTER_CIRCLE_RADIUS, circlePaint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(
            (VIEW_WIDTH + ELEMENT_CIRCLE_RADIUS * 2).toInt(),
            (VIEW_HEIGHT + ELEMENT_CIRCLE_RADIUS * 2).toInt()
        )
    }

}