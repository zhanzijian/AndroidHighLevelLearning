package com.example.viewapplication.view

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import com.example.viewapplication.R
import com.example.viewapplication.dp
import com.example.viewapplication.getColorById
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

private val ARROW_WIDTH = 8f.dp // 箭头宽度
private val ARROW_HEIGHT = 8f.dp // 箭头高度
private const val ARROW_CONCAVE_ANGLE = 90f // 箭头内凹角
private val VIEW_WIDTH = 260f.dp // view 宽度
private val VIEW_HEIGHT = 210f.dp // view 高度
private const val ANIMATION_START_DELAY = 2000L // 动画启动延时
private const val ANIMATION_DURATION = 4000L // 动画运行时长
private const val TAG = "AcCoupleEsPathView"

class AcCoupleEsPathView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
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

    // ************* paint ************** //
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
            style = Paint.Style.FILL_AND_STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            strokeWidth = 2f.dp
            color = getColorById(context, R.color.blue_5f91cb_color)
        }
    }

    // ************* 箭头路径 ************** //
    private val pvToInverterArrowPath by lazy(::Path)

    /**
     * 箭头内凹长度
     */
    private val arrowConcaveLength by lazy { (ARROW_WIDTH / 2f / tan(Math.toRadians((ARROW_CONCAVE_ANGLE / 2).toDouble()))).toFloat() }

    // ************************** 动画 *************************** //

    // ************* PV-逆变器 ************** //
    /**
     * y的增量
     */
    private var pvToInverterDy: Float = 0f
        set(value) {
            field = value
            invalidate()
        }

    /**
     * 动画的距离
     */
    private var pvToInverterAnimatorLength = 0f

    /**
     * 动画
     */
    private val pvToInverterAnimator by lazy {
        ObjectAnimator.ofFloat(this, "pvToInverterDy", 0f, pvToInverterAnimatorLength)
            .animatorConfig()
    }

    // ************* 逆变器-中间 ************** //
    /**
     * x 的增量
     */
    private var inverterToCenterDx: Float = 0f
        set(value) {
            field = value
            invalidate()
        }
    /**
     * 动画的距离
     */
    private var inverterToCenterAnimatorLength = 0f
    /**
     * 动画
     */
    private val inverterToCenterAnimator by lazy {
        ObjectAnimator.ofFloat(this, "inverterToCenterDx", 0f, inverterToCenterAnimatorLength)
            .animatorConfig()
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        // 一些基础变量初始化
        dx = VIEW_WIDTH / 2f
        dy = VIEW_HEIGHT / 2f
        centerX = width / 2f
        centerY = height / 2f
    }

    override fun onDraw(canvas: Canvas) {
        // 画线
        // 中心点


        // PV -> 逆变器
        // 路径
        canvas.drawLine(centerX - dx, centerY - dy, centerX - dx, centerY, linePaint)
        // 箭头
        if (pvToInverterAnimator.isRunning || pvToInverterAnimator.isStarted) {
            initPvToInverterArrowPath()
            canvas.drawPath(pvToInverterArrowPath, arrowPaint)
        }
        canvas.drawLine(centerX - dx, centerY - dy + arrowConcaveLength, centerX - dx + ARROW_WIDTH / 2f, centerY - dy , arrowPaint)

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

    /**
     * 初始化pv-逆变器箭头的path
     */
    private fun initPvToInverterArrowPath() {
        if (!pvToInverterArrowPath.isEmpty) {
            pvToInverterArrowPath.reset()
        }
        pvToInverterArrowPath.moveTo(
            centerX - dx,
            centerY - dy + ELEMENT_CIRCLE_RADIUS + arrowConcaveLength + pvToInverterDy
        )
        pvToInverterArrowPath.rLineTo(ARROW_WIDTH / 2f, -arrowConcaveLength)
        pvToInverterArrowPath.rLineTo(-ARROW_WIDTH / 2f, ARROW_HEIGHT)
        pvToInverterArrowPath.rLineTo(-ARROW_WIDTH / 2f, -ARROW_HEIGHT)
        pvToInverterArrowPath.close()
    }

    /**
     * 启动pv-逆变器的箭头动画
     */
    fun startPvToInverterAnimation() {
        pvToInverterAnimatorLength = dy - ELEMENT_CIRCLE_RADIUS * 2
        pvToInverterAnimator.apply {
            setFloatValues(0f, pvToInverterAnimatorLength)
            duration = getAnimatorDuration(pvToInverterAnimatorLength)
        }
        pvToInverterAnimator.start()
    }

    /**
     * 结束pv-逆变器的箭头动画
     */
    fun endPvToInverterAnimation() = endArrowAnimations(pvToInverterAnimator)

    private fun endArrowAnimations(vararg animators: ObjectAnimator) = animators.forEach {
        it.end()
    }

    /**
     * 动画基础配置
     *
     * @return
     */
    private fun ObjectAnimator.animatorConfig(): ObjectAnimator {
        interpolator = LinearInterpolator()
        startDelay = ANIMATION_START_DELAY
        repeatCount = ValueAnimator.INFINITE
        return this
    }

    /**
     * 获取动画时间
     * 保证所有动画速度一致
     * S = VT
     * @param animatorLength 动画路径长度 S
     * @return
     */
    private fun getAnimatorDuration(animatorLength: Float): Long {
        return (animatorLength / dy * ANIMATION_DURATION).toLong()
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // 留出空间放各大组件
        setMeasuredDimension(
            (VIEW_WIDTH + ELEMENT_CIRCLE_RADIUS * 2).toInt(),
            (VIEW_HEIGHT + ELEMENT_CIRCLE_RADIUS * 2).toInt()
        )
    }

}