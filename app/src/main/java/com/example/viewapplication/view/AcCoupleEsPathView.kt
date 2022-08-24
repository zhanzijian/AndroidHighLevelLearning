package com.example.viewapplication.view

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import android.view.animation.AnimationSet
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
    private val inverterToCenterArrowPath by lazy(::Path)
    private val centerToTopArrowPath by lazy(::Path)
    private val centerToAcArrowPath by lazy(::Path)
    private val centerToGridLoadArrowPath by lazy(::Path)
    private val batteryToAcArrowPath by lazy(::Path)
    private val acToBackupArrowPath by lazy(::Path)
    private val topCenterToGridArrowPath by lazy(::Path)

    /**
     * 箭头内凹长度
     */
    private val arrowConcaveLength by lazy { (ARROW_WIDTH / 2f / tan(Math.toRadians((ARROW_CONCAVE_ANGLE / 2).toDouble()))).toFloat() }

    // ************************** 动画 *************************** //

    private val animatorSet by lazy { AnimatorSet() }

    // ************* PV-逆变器 ************** //
    private var pvToInverterDy: Float = 0f
        set(value) {
            field = value
            invalidate()
        }
    private var pvToInverterAnimatorLength = 0f
    private val pvToInverterAnimator: ObjectAnimator by lazy {
        ObjectAnimator.ofFloat(this, "pvToInverterDy", 0f, pvToInverterAnimatorLength)
            .animatorConfig()
    }

    // ************* 逆变器-中间 ************** //
    private var inverterToCenterDx: Float = 0f
        set(value) {
            field = value
            invalidate()
        }
    private var inverterToCenterAnimatorLength = 0f
    private val inverterToCenterAnimator by lazy {
        ObjectAnimator.ofFloat(this, "inverterToCenterDx", 0f, inverterToCenterAnimatorLength)
            .animatorConfig()
    }

    // ************* 中间-顶部 ************** //
    private var centerToTopDy: Float = 0f
        set(value) {
            field = value
            invalidate()
        }
    private var centerToTopAnimatorLength = 0f
    private val centerToTopAnimator by lazy {
        ObjectAnimator.ofFloat(this, "centerToTopDy", 0f ,centerToTopAnimatorLength)
            .animatorConfig()
    }

    // ************* 中间-底部 ************** //
    private var centerToAcDy: Float = 0f
        set(value) {
            field = value
            invalidate()
        }
    private var centerToAcAnimatorLength = 0f
    private val centerToAcAnimator by lazy {
        ObjectAnimator.ofFloat(this, "centerToAcDy", 0f ,centerToAcAnimatorLength)
            .animatorConfig()
    }

    // ************* 中间-电网负载 ************** //
    private var centerToGridLoadDx: Float = 0f
        set(value) {
            field = value
            invalidate()
        }
    private var centerToGridLoadAnimatorLength = 0f
    private val centerToGridLoadAnimator by lazy {
        ObjectAnimator.ofFloat(this, "centerToGridLoadDx", 0f ,centerToGridLoadAnimatorLength)
            .animatorConfig()
    }

    // ************* 电池-AC ************** //
    private var batteryToAcDx: Float = 0f
        set(value) {
            field = value
            invalidate()
        }
    private var batteryToAcAnimatorLength = 0f
    private val batteryToAcAnimator by lazy {
        ObjectAnimator.ofFloat(this, "batteryToAcDx", 0f ,batteryToAcAnimatorLength)
            .animatorConfig()
    }

    // ************* AC-backup负载 ************** //
    private var acToBackupDx: Float = 0f
        set(value) {
            field = value
            invalidate()
        }
    private var acToBackupAnimatorLength = 0f
    private val acToBackupAnimator by lazy {
        ObjectAnimator.ofFloat(this, "acToBackupDx", 0f ,acToBackupAnimatorLength)
            .animatorConfig()
    }

    // ************* 中间顶部-电网负载 ************** //
    private var topCenterToGridDx: Float = 0f
        set(value) {
            field = value
            invalidate()
        }
    private var topCenterToGridAnimatorLength = 0f
    private val topCenterToGridAnimator by lazy {
        ObjectAnimator.ofFloat(this, "topCenterToGridDx", 0f ,topCenterToGridAnimatorLength)
            .animatorConfig()
    }



    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        // 一些基础变量初始化
        dx = VIEW_WIDTH / 2f
        dy = VIEW_HEIGHT / 2f
        centerX = width / 2f
        centerY = height / 2f
    }

    private fun ObjectAnimator.isAnimating() = isStarted || isRunning

    override fun onDraw(canvas: Canvas) {

        // PV -> 逆变器
        canvas.drawLine(centerX - dx, centerY - dy, centerX - dx, centerY, linePaint)
        if (pvToInverterAnimator.isAnimating()) {
            initPvToInverterArrowPath()
            canvas.drawPath(pvToInverterArrowPath, arrowPaint)
        }

        // 逆变器 -> 中心
        canvas.drawLine(centerX - dx, centerY, centerX, centerY, linePaint)
        if (inverterToCenterAnimator.isAnimating()) {
            initInverterToCenterArrowPath()
            canvas.drawPath(inverterToCenterArrowPath, arrowPaint)
        }

        // 中心 -> 顶部
        canvas.drawLine(centerX, centerY, centerX, centerY - dy, linePaint)
        if (centerToTopAnimator.isAnimating()) {
            initCenterToTopArrowPath()
            canvas.drawPath(centerToTopArrowPath, arrowPaint)
        }

        // 中心 -> 电网负载
        canvas.drawLine(centerX, centerY, centerX + dx, centerY, linePaint)
        if (centerToGridLoadAnimator.isAnimating()) {
            initCenterToGridLoadArrowPath()
            canvas.drawPath(centerToGridLoadArrowPath, arrowPaint)
        }

        // 中心 -> Ac Couple
        canvas.drawLine(centerX, centerY, centerX, centerY + dy, linePaint)
        if (centerToAcAnimator.isAnimating()) {
            initCenterToAcArrowPath()
            canvas.drawPath(centerToAcArrowPath, arrowPaint)
        }

        // 电池 -> Ac Couple
        canvas.drawLine(centerX - dx, centerY + dy, centerX, centerY + dy, linePaint)
        if (batteryToAcAnimator.isAnimating()) {
            initBatteryToAcArrowPath()
            canvas.drawPath(batteryToAcArrowPath, arrowPaint)
        }

        // Ac Couple -> backup 负载
        canvas.drawLine(centerX, centerY + dy, centerX + dx, centerY + dy, linePaint)
        if (acToBackupAnimator.isAnimating()) {
            initAcToBackupArrowPath()
            canvas.drawPath(acToBackupArrowPath, arrowPaint)
        }

        // 中心顶部 -> 电网
        canvas.drawLine(centerX, centerY - dy, centerX + dx, centerY - dy, linePaint)
        if (topCenterToGridAnimator.isAnimating()) {
            initTopCenterToGridArrowPath()
            canvas.drawPath(topCenterToGridArrowPath, arrowPaint)
        }

        // 中心原点
        canvas.drawCircle(centerX, centerY, CENTER_CIRCLE_RADIUS, circlePaint)
    }

    /**
     * 初始化 pv-逆变器 箭头的path
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
     * 初始化 逆变器-中心原点 箭头的path
     */
    private fun initInverterToCenterArrowPath() {
        if (!inverterToCenterArrowPath.isEmpty) {
            inverterToCenterArrowPath.reset()
        }
        inverterToCenterArrowPath.moveTo(
            centerX - dx + ELEMENT_CIRCLE_RADIUS + arrowConcaveLength + inverterToCenterDx,
            centerY
        )
        inverterToCenterArrowPath.rLineTo(-arrowConcaveLength, -ARROW_WIDTH / 2f)
        inverterToCenterArrowPath.rLineTo(ARROW_HEIGHT, ARROW_WIDTH / 2f)
        inverterToCenterArrowPath.rLineTo(-ARROW_HEIGHT, ARROW_WIDTH / 2f)
        inverterToCenterArrowPath.close()
    }

    /**
     * 初始化 中心-顶部 箭头的path
     */
    private fun initCenterToTopArrowPath() {
        if (!centerToTopArrowPath.isEmpty) {
            centerToTopArrowPath.reset()
        }
        centerToTopArrowPath.moveTo(
            centerX,
            centerY - arrowConcaveLength - centerToTopDy
        )
        centerToTopArrowPath.rLineTo(-ARROW_WIDTH / 2f, arrowConcaveLength)
        centerToTopArrowPath.rLineTo(ARROW_WIDTH / 2f, -ARROW_HEIGHT)
        centerToTopArrowPath.rLineTo(ARROW_WIDTH / 2f, ARROW_HEIGHT)
        centerToTopArrowPath.close()
    }

    /**
     * 初始化 中心-AC 箭头的path
     */
    private fun initCenterToAcArrowPath() {
        if (!centerToAcArrowPath.isEmpty) {
            centerToAcArrowPath.reset()
        }
        centerToAcArrowPath.moveTo(
            centerX,
            centerY + arrowConcaveLength + centerToAcDy
        )
        centerToAcArrowPath.rLineTo(-ARROW_WIDTH / 2f, -arrowConcaveLength)
        centerToAcArrowPath.rLineTo(ARROW_WIDTH / 2f, ARROW_HEIGHT)
        centerToAcArrowPath.rLineTo(ARROW_WIDTH / 2f, -ARROW_HEIGHT)
        centerToAcArrowPath.close()
    }

    /**
     * 初始化 中心-AC 箭头的path
     */
    private fun initCenterToGridLoadArrowPath() {
        if (!centerToGridLoadArrowPath.isEmpty) {
            centerToGridLoadArrowPath.reset()
        }
        centerToGridLoadArrowPath.moveTo(
            centerX + arrowConcaveLength + centerToGridLoadDx,
            centerY
        )
        centerToGridLoadArrowPath.rLineTo(-arrowConcaveLength, -ARROW_WIDTH / 2f)
        centerToGridLoadArrowPath.rLineTo(ARROW_HEIGHT, ARROW_WIDTH / 2f)
        centerToGridLoadArrowPath.rLineTo(-ARROW_HEIGHT, ARROW_WIDTH / 2f)
        centerToGridLoadArrowPath.close()
    }

    /**
     * 初始化 中心-AC 箭头的path
     */
    private fun initBatteryToAcArrowPath() {
        if (!batteryToAcArrowPath.isEmpty) {
            batteryToAcArrowPath.reset()
        }
        batteryToAcArrowPath.moveTo(
            centerX - dx + ELEMENT_CIRCLE_RADIUS - ARROW_HEIGHT + batteryToAcDx,
            centerY + dy
        )
        batteryToAcArrowPath.rLineTo(-arrowConcaveLength, -ARROW_WIDTH / 2f)
        batteryToAcArrowPath.rLineTo(ARROW_HEIGHT, ARROW_WIDTH / 2f)
        batteryToAcArrowPath.rLineTo(-ARROW_HEIGHT, ARROW_WIDTH / 2f)
        batteryToAcArrowPath.close()
    }

    /**
     * 初始化 中心-AC 箭头的path
     */
    private fun initAcToBackupArrowPath() {
        if (!acToBackupArrowPath.isEmpty) {
            acToBackupArrowPath.reset()
        }
        acToBackupArrowPath.moveTo(
            centerX + ELEMENT_CIRCLE_RADIUS - ARROW_HEIGHT + batteryToAcDx,
            centerY + dy
        )
        acToBackupArrowPath.rLineTo(-arrowConcaveLength, -ARROW_WIDTH / 2f)
        acToBackupArrowPath.rLineTo(ARROW_HEIGHT, ARROW_WIDTH / 2f)
        acToBackupArrowPath.rLineTo(-ARROW_HEIGHT, ARROW_WIDTH / 2f)
        acToBackupArrowPath.close()
    }

    /**
     * 初始化 中心顶部-电网 箭头的path
     */
    private fun initTopCenterToGridArrowPath() {
        if (!topCenterToGridArrowPath.isEmpty) {
            topCenterToGridArrowPath.reset()
        }
        topCenterToGridArrowPath.moveTo(
            centerX + topCenterToGridDx,
            centerY - dy
        )
        topCenterToGridArrowPath.rLineTo(-arrowConcaveLength, -ARROW_WIDTH / 2f)
        topCenterToGridArrowPath.rLineTo(ARROW_HEIGHT, ARROW_WIDTH / 2f)
        topCenterToGridArrowPath.rLineTo(-ARROW_HEIGHT, ARROW_WIDTH / 2f)
        topCenterToGridArrowPath.close()
    }

    // ---------------- 动画启动和取消区域开始 -------------------- //

    /**
     * 重置动画的一些属性
     */
    private fun resetAnimatorConfig(arrowDirection: ArrowDirection) {
        when(arrowDirection) {
            ArrowDirection.PV_TO_INVERTER -> {
                pvToInverterAnimatorLength = dy - ELEMENT_CIRCLE_RADIUS * 2
                pvToInverterAnimator.apply {
                    setFloatValues(0f, pvToInverterAnimatorLength)
                    duration = getAnimatorDuration(pvToInverterAnimatorLength)
                }
            }
            ArrowDirection.INVERTER_TO_CENTER -> {
                inverterToCenterAnimatorLength = dx - ELEMENT_CIRCLE_RADIUS - ARROW_HEIGHT
                inverterToCenterAnimator.apply {
                    setFloatValues(0f, inverterToCenterAnimatorLength)
                    duration = getAnimatorDuration(inverterToCenterAnimatorLength)
                }
            }
            ArrowDirection.CENTER_TO_TOP -> {
                centerToTopAnimatorLength = dy - ARROW_HEIGHT
                centerToTopAnimator.apply {
                    setFloatValues(0f, centerToTopAnimatorLength)
                    duration = getAnimatorDuration(centerToTopAnimatorLength)
                }
            }
            ArrowDirection.CENTER_TO_AC -> {
                centerToAcAnimatorLength = dy - ELEMENT_CIRCLE_RADIUS
                centerToAcAnimator.apply {
                    setFloatValues(0f, centerToAcAnimatorLength)
                    duration = getAnimatorDuration(centerToAcAnimatorLength)
                }
            }
            ArrowDirection.CENTER_TO_GRID_LOAD -> {
                centerToGridLoadAnimatorLength = dx - ARROW_HEIGHT
                centerToGridLoadAnimator.apply {
                    setFloatValues(0f, centerToGridLoadAnimatorLength)
                    duration = getAnimatorDuration(centerToGridLoadAnimatorLength)
                }
            }
            ArrowDirection.BATTERY_TO_AC -> {
                batteryToAcAnimatorLength = dx - ELEMENT_CIRCLE_RADIUS * 2 + ARROW_HEIGHT
                batteryToAcAnimator.apply {
                    setFloatValues(0f, batteryToAcAnimatorLength)
                    duration = getAnimatorDuration(batteryToAcAnimatorLength)
                }
            }
            ArrowDirection.AC_TO_BACK_UP_LOAD -> {
                acToBackupAnimatorLength = dx - ELEMENT_CIRCLE_RADIUS * 2 + ARROW_HEIGHT
                acToBackupAnimator.apply {
                    setFloatValues(0f, acToBackupAnimatorLength)
                    duration = getAnimatorDuration(acToBackupAnimatorLength)
                }
            }
            ArrowDirection.TOP_CENTER_TO_GRID -> {
                topCenterToGridAnimatorLength = dx - ELEMENT_CIRCLE_RADIUS
                topCenterToGridAnimator.apply {
                    setFloatValues(0f, topCenterToGridAnimatorLength)
                    duration = getAnimatorDuration(topCenterToGridAnimatorLength)
                }
            }
        }
    }

    /**
     * Start arrow animations
     * 开启一些箭头动画
     * @param arrowDirections
     */
    fun startArrowAnimations(vararg arrowDirections: ArrowDirection) {
        if (arrowDirections.isEmpty()) return
        val animatorList = mutableListOf<Animator>()
        arrowDirections.forEach {
            resetAnimatorConfig(it)
            when (it) {
                ArrowDirection.PV_TO_INVERTER -> animatorList.add(pvToInverterAnimator)
                ArrowDirection.INVERTER_TO_CENTER -> animatorList.add(inverterToCenterAnimator)
                ArrowDirection.CENTER_TO_TOP -> animatorList.add(centerToTopAnimator)
                ArrowDirection.CENTER_TO_AC -> animatorList.add(centerToAcAnimator)
                ArrowDirection.CENTER_TO_GRID_LOAD -> animatorList.add(centerToGridLoadAnimator)
                ArrowDirection.BATTERY_TO_AC -> animatorList.add(batteryToAcAnimator)
                ArrowDirection.AC_TO_BACK_UP_LOAD -> animatorList.add(acToBackupAnimator)
                ArrowDirection.TOP_CENTER_TO_GRID -> animatorList.add(topCenterToGridAnimator)
            }
        }
        animatorSet.playTogether(animatorList)
        animatorSet.start()
    }

    /**
     * End arrow animation
     * 取消某个箭头动画
     * @param arrowDirection
     */
    fun endArrowAnimation(arrowDirection: ArrowDirection) =  when (arrowDirection) {
        ArrowDirection.PV_TO_INVERTER -> pvToInverterAnimator.end()
        ArrowDirection.INVERTER_TO_CENTER -> inverterToCenterAnimator.end()
        ArrowDirection.CENTER_TO_TOP -> centerToTopAnimator.end()
        ArrowDirection.CENTER_TO_AC -> centerToAcAnimator.end()
        ArrowDirection.CENTER_TO_GRID_LOAD -> centerToGridLoadAnimator.end()
        ArrowDirection.BATTERY_TO_AC -> batteryToAcAnimator.end()
        ArrowDirection.AC_TO_BACK_UP_LOAD -> acToBackupAnimator.end()
        ArrowDirection.TOP_CENTER_TO_GRID -> topCenterToGridAnimator.end()
    }

    /**
     * End arrow animations
     * 取消某些箭头动画
     * @param arrowDirections
     */
    fun endArrowAnimations(vararg arrowDirections: ArrowDirection) {
        if (arrowDirections.isEmpty()) {
            return
        }
        arrowDirections.forEach {
            endArrowAnimation(it)
        }
    }

    /**
     * End all arrow animations
     * 取消所有箭头动画
     */
    fun endAllArrowAnimations() = animatorSet.end()

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

    // ---------------- 动画启动和取消区域结束 -------------------- //


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // 留出空间放各大组件
        setMeasuredDimension(
            (VIEW_WIDTH + ELEMENT_CIRCLE_RADIUS * 2).toInt(),
            (VIEW_HEIGHT + ELEMENT_CIRCLE_RADIUS * 2).toInt()
        )
    }

    enum class ArrowDirection {
        PV_TO_INVERTER, // PV - 逆变器
        INVERTER_TO_CENTER, // 逆变器 - 中间
        CENTER_TO_TOP, // 中间 - 顶部
        CENTER_TO_AC, // 中间 - AC Couple
        CENTER_TO_GRID_LOAD, // 中间 - 电网负载
        BATTERY_TO_AC, // 电池 - AC Couple
        AC_TO_BACK_UP_LOAD, // AC Couple - Backup 负载
        TOP_CENTER_TO_GRID // 中间顶部 - 电网
    }

}