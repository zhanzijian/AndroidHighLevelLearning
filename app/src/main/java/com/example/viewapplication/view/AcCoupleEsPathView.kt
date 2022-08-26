package com.example.viewapplication.view

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.os.Build
import android.util.AttributeSet
import android.util.SparseArray
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.util.*
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
private const val ANIMATION_DURATION = 2000L // 动画运行时长

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
    private val blueLinePaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = PATH_STROKE_WIDTH
            color = getColorById(context, R.color.blue_5f91cb_color)
        }
    }
    private val greenLinePaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = PATH_STROKE_WIDTH
            color = getColorById(context, R.color.green_aed681_color)
        }
    }

    private val circlePaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = getColorById(context, R.color.green_aed681_color)
        }
    }

    private val blueArrowPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL_AND_STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            strokeWidth = 2f.dp
            color = getColorById(context, R.color.blue_5f91cb_color)
        }
    }

    private val greenArrowPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL_AND_STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            strokeWidth = 2f.dp
            color = getColorById(context, R.color.green_aed681_color)
        }
    }

    // ************* 箭头路径 ************** //
    private val pvToInverterArrowPath by lazy(::Path)
    private val inverterToCenterArrowPath by lazy(::Path)
    private val centerToTopArrowPath by lazy(::Path)
    private val topToCenterArrowPath by lazy(::Path)
    private val centerToAcArrowPath by lazy(::Path)
    private val acToCenterArrowPath by lazy(::Path)
    private val centerToGridLoadArrowPath by lazy(::Path)
    private val batteryToAcArrowPath by lazy(::Path)
    private val acToBatteryArrowPath by lazy(::Path)
    private val acToBackupArrowPath by lazy(::Path)
    private val topCenterToGridArrowPath by lazy(::Path)
    private val gridToTopCenterArrowPath by lazy(::Path)

    /**
     * 箭头内凹长度
     */
    private val arrowConcaveLength by lazy { (ARROW_WIDTH / 2f / tan(Math.toRadians((ARROW_CONCAVE_ANGLE / 2).toDouble()))).toFloat() }

    // ************************** 动画 *************************** //

    /**
     * PV-逆变器 Y 方向移动距离
     */
    private var pvToInverterDy: Float = 0f
        set(value) {
            field = value
            invalidate()
        }

    /**
     * 逆变器-中间 X 方向移动距离
     */
    private var inverterToCenterDx: Float = 0f
        set(value) {
            field = value
            invalidate()
        }

    /**
     * 中间-顶部 Y 方向移动距离
     */
    private var centerToTopDy: Float = 0f
        set(value) {
            field = value
            invalidate()
        }

    /**
     * 顶部-中间 Y 方向移动距离
     */
    private var topToCenterDy: Float = 0f
        set(value) {
            field = value
            invalidate()
        }

    /**
     * 中间-AC Y 方向移动距离
     */
    private var centerToAcDy: Float = 0f
        set(value) {
            field = value
            invalidate()
        }

    /**
     * AC-中间 Y 方向移动距离
     */
    private var acToCenterDy: Float = 0f
        set(value) {
            field = value
            invalidate()
        }

    /**
     * 中间-电网负载 X 方向移动距离
     */
    private var centerToGridLoadDx: Float = 0f
        set(value) {
            field = value
            invalidate()
        }

    /**
     * 电池-AC X 方向移动距离
     */
    private var batteryToAcDx: Float = 0f
        set(value) {
            field = value
            invalidate()
        }

    /**
     * AC-电池 X 方向移动距离
     */
    private var acToBatteryDx: Float = 0f
        set(value) {
            field = value
            invalidate()
        }

    /**
     * AC-backup负载 X 方向移动距离
     */
    private var acToBackupDx: Float = 0f
        set(value) {
            field = value
            invalidate()
        }

    /**
     * 中间顶部-电网负载 X 方向移动距离
     */
    private var topCenterToGridDx: Float = 0f
        set(value) {
            field = value
            invalidate()
        }

    /**
     * 电网负载-中间顶部 X 方向移动距离
     */
    private var gridToTopCenterDx: Float = 0f
        set(value) {
            field = value
            invalidate()
        }

    /**
     * 是否离线
     */
    var isOffline = false
        set(value) {
            field = value
            invalidate()
        }

    /**
     * 储存动画，key - ArrowDirection的序号  value - 动画对象
     */
    private val animatorArray = SparseArray<ObjectAnimator>()

    /**
     * 正在进行的动画集合
     */
    private val runningAnimatorArray = SparseArray<ObjectAnimator>()

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        // 一些基础变量初始化
        dx = VIEW_WIDTH / 2f
        dy = VIEW_HEIGHT / 2f
        centerX = width / 2f
        centerY = height / 2f
    }

    private fun isAnimating(arrowDirection: ArrowDirection): Boolean {
        if (isOffline) return false
        if (runningAnimatorArray.isEmpty()) return false
        return runningAnimatorArray.containsKey(arrowDirection.ordinal)
    }

    override fun onDraw(canvas: Canvas) {

        handleOffline()

        // PV -> 逆变器
        canvas.drawLine(centerX - dx, centerY - dy, centerX - dx, centerY, blueLinePaint)
        if (isAnimating(ArrowDirection.PV_TO_INVERTER)) {
            initPvToInverterArrowPath()
            canvas.drawPath(pvToInverterArrowPath, blueArrowPaint)
        }
        // 电池 <-> Ac Couple
        canvas.drawLine(centerX - dx, centerY + dy, centerX, centerY + dy, blueLinePaint)
        if (isAnimating(ArrowDirection.BATTERY_TO_AC)) {
            initBatteryToAcArrowPath()
            canvas.drawPath(batteryToAcArrowPath, blueArrowPaint)
        } else if (isAnimating(ArrowDirection.AC_TO_BATTERY)) {
            initAcToBatteryArrowPath()
            canvas.drawPath(acToBatteryArrowPath, blueArrowPaint)
        }

        // 逆变器 -> 中心
        canvas.drawLine(centerX - dx, centerY, centerX, centerY, greenLinePaint)
        if (isAnimating(ArrowDirection.INVERTER_TO_CENTER)) {
            initInverterToCenterArrowPath()
            canvas.drawPath(inverterToCenterArrowPath, greenArrowPaint)
        }

        // 中心 <-> 顶部
        canvas.drawLine(centerX, centerY, centerX, centerY - dy, greenLinePaint)
        if (isAnimating(ArrowDirection.CENTER_TO_TOP)) {
            initCenterToTopArrowPath()
            canvas.drawPath(centerToTopArrowPath, greenArrowPaint)
        } else if (isAnimating(ArrowDirection.TOP_TO_CENTER)) {
            initTopToCenterArrowPath()
            canvas.drawPath(topToCenterArrowPath, greenArrowPaint)
        }

        // 中心 -> 电网负载
        canvas.drawLine(centerX, centerY, centerX + dx, centerY, greenLinePaint)
        if (isAnimating(ArrowDirection.CENTER_TO_GRID_LOAD)) {
            initCenterToGridLoadArrowPath()
            canvas.drawPath(centerToGridLoadArrowPath, greenArrowPaint)
        }

        // 中心 <-> Ac Couple
        canvas.drawLine(centerX, centerY, centerX, centerY + dy, greenLinePaint)
        if (isAnimating(ArrowDirection.CENTER_TO_AC)) {
            initCenterToAcArrowPath()
            canvas.drawPath(centerToAcArrowPath, greenArrowPaint)
        } else if (isAnimating(ArrowDirection.AC_TO_CENTER)) {
            initAcToCenterArrowPath()
            canvas.drawPath(acToCenterArrowPath, greenArrowPaint)
        }


        // Ac Couple -> backup 负载
        canvas.drawLine(centerX, centerY + dy, centerX + dx, centerY + dy, greenLinePaint)
        if (isAnimating(ArrowDirection.AC_TO_BACK_UP_LOAD)) {
            initAcToBackupArrowPath()
            canvas.drawPath(acToBackupArrowPath, greenArrowPaint)
        }

        // 中心顶部 <-> 电网
        canvas.drawLine(centerX, centerY - dy, centerX + dx, centerY - dy, greenLinePaint)
        if (isAnimating(ArrowDirection.TOP_CENTER_TO_GRID)) {
            initTopCenterToGridArrowPath()
            canvas.drawPath(topCenterToGridArrowPath, greenArrowPaint)
        } else if (isAnimating(ArrowDirection.GRID_TO_TOP_CENTER)) {
            initGridToTopCenterArrowPath()
            canvas.drawPath(gridToTopCenterArrowPath, greenArrowPaint)
        }

        // 中心原点
        canvas.drawCircle(centerX, centerY, CENTER_CIRCLE_RADIUS, circlePaint)
    }

    /**
     * 处理离线情况
     */
    private fun handleOffline() {
        if (!isOffline) return
        val paintList =
            arrayListOf(blueLinePaint, blueArrowPaint, greenLinePaint, greenLinePaint, circlePaint)
        paintList.forEach {
            it.color = getColorById(context, R.color.gray_cc_color)
        }
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
     * 初始化 顶部-中心 箭头的path
     */
    private fun initTopToCenterArrowPath() {
        if (!topToCenterArrowPath.isEmpty) {
            topToCenterArrowPath.reset()
        }
        topToCenterArrowPath.moveTo(
            centerX,
            centerY - dy + topToCenterDy
        )
        topToCenterArrowPath.rLineTo(ARROW_WIDTH / 2f, -arrowConcaveLength)
        topToCenterArrowPath.rLineTo(-ARROW_WIDTH / 2f, ARROW_HEIGHT)
        topToCenterArrowPath.rLineTo(-ARROW_WIDTH / 2f, -ARROW_HEIGHT)
        topToCenterArrowPath.close()
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
     * 初始化 AC-中心 箭头的path
     */
    private fun initAcToCenterArrowPath() {
        if (!acToCenterArrowPath.isEmpty) {
            acToCenterArrowPath.reset()
        }
        acToCenterArrowPath.moveTo(
            centerX,
            centerY + dy - ELEMENT_CIRCLE_RADIUS + arrowConcaveLength - acToCenterDy
        )
        acToCenterArrowPath.rLineTo(-ARROW_WIDTH / 2f, arrowConcaveLength)
        acToCenterArrowPath.rLineTo(ARROW_WIDTH / 2f, -ARROW_HEIGHT)
        acToCenterArrowPath.rLineTo(ARROW_WIDTH / 2f, ARROW_HEIGHT)
        acToCenterArrowPath.close()
    }

    /**
     * 初始化 中心-电网负载 箭头的path
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
     * 初始化 电池-AC 箭头的path
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
     * 初始化 AC-电池 箭头的path
     */
    private fun initAcToBatteryArrowPath() {
        if (!acToBatteryArrowPath.isEmpty) {
            acToBatteryArrowPath.reset()
        }
        acToBatteryArrowPath.moveTo(
            centerX - ELEMENT_CIRCLE_RADIUS + ARROW_HEIGHT - acToBatteryDx,
            centerY + dy
        )
        acToBatteryArrowPath.rLineTo(arrowConcaveLength, -ARROW_WIDTH / 2f)
        acToBatteryArrowPath.rLineTo(-ARROW_HEIGHT, ARROW_WIDTH / 2f)
        acToBatteryArrowPath.rLineTo(ARROW_HEIGHT, ARROW_WIDTH / 2f)
        acToBatteryArrowPath.close()
    }

    /**
     * 初始化 中心-backup负载 箭头的path
     */
    private fun initAcToBackupArrowPath() {
        if (!acToBackupArrowPath.isEmpty) {
            acToBackupArrowPath.reset()
        }
        acToBackupArrowPath.moveTo(
            centerX + ELEMENT_CIRCLE_RADIUS - ARROW_HEIGHT + acToBackupDx,
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

    /**
     * 初始化 电网-中心顶部 箭头的path
     */
    private fun initGridToTopCenterArrowPath() {
        if (!gridToTopCenterArrowPath.isEmpty) {
            gridToTopCenterArrowPath.reset()
        }
        gridToTopCenterArrowPath.moveTo(
            centerX + dx - ELEMENT_CIRCLE_RADIUS + ARROW_HEIGHT - gridToTopCenterDx,
            centerY - dy
        )
        gridToTopCenterArrowPath.rLineTo(arrowConcaveLength, -ARROW_WIDTH / 2f)
        gridToTopCenterArrowPath.rLineTo(-ARROW_HEIGHT, ARROW_WIDTH / 2f)
        gridToTopCenterArrowPath.rLineTo(ARROW_HEIGHT, ARROW_WIDTH / 2f)
        gridToTopCenterArrowPath.close()
    }

    // ---------------- 动画启动和取消区域开始 -------------------- //


    /**
     * Get animator property name by direction
     * 通过箭头方向获取动画属性名称
     * @param arrowDirection
     * @return
     */
    private fun getAnimatorPropertyNameByDirection(arrowDirection: ArrowDirection): String {
        return when (arrowDirection) {
            ArrowDirection.PV_TO_INVERTER -> "pvToInverterDy"
            ArrowDirection.INVERTER_TO_CENTER -> "inverterToCenterDx"
            ArrowDirection.CENTER_TO_TOP -> "centerToTopDy"
            ArrowDirection.TOP_TO_CENTER -> "topToCenterDy"
            ArrowDirection.CENTER_TO_AC -> "centerToAcDy"
            ArrowDirection.AC_TO_CENTER -> "acToCenterDy"
            ArrowDirection.CENTER_TO_GRID_LOAD -> "centerToGridLoadDx"
            ArrowDirection.BATTERY_TO_AC -> "batteryToAcDx"
            ArrowDirection.AC_TO_BATTERY -> "acToBatteryDx"
            ArrowDirection.AC_TO_BACK_UP_LOAD -> "acToBackupDx"
            ArrowDirection.TOP_CENTER_TO_GRID -> "topCenterToGridDx"
            ArrowDirection.GRID_TO_TOP_CENTER -> "gridToTopCenterDx"
        }
    }

    /**
     * Get animator length by direction
     * 通过箭头方向获取动画的长度
     * @param arrowDirection
     * @return
     */
    private fun getAnimatorLengthByDirection(arrowDirection: ArrowDirection): Float {
        return when (arrowDirection) {
            ArrowDirection.PV_TO_INVERTER -> dy - ELEMENT_CIRCLE_RADIUS * 2
            ArrowDirection.INVERTER_TO_CENTER -> dx - ELEMENT_CIRCLE_RADIUS - ARROW_HEIGHT
            ArrowDirection.CENTER_TO_TOP -> dy - ARROW_HEIGHT
            ArrowDirection.TOP_TO_CENTER -> dy - ARROW_HEIGHT
            ArrowDirection.CENTER_TO_AC -> dy - ELEMENT_CIRCLE_RADIUS
            ArrowDirection.AC_TO_CENTER -> dy - ELEMENT_CIRCLE_RADIUS
            ArrowDirection.CENTER_TO_GRID_LOAD -> dx - ARROW_HEIGHT
            ArrowDirection.BATTERY_TO_AC -> dx - ELEMENT_CIRCLE_RADIUS * 2 + ARROW_HEIGHT
            ArrowDirection.AC_TO_BATTERY -> dx - ELEMENT_CIRCLE_RADIUS * 2 + ARROW_HEIGHT
            ArrowDirection.AC_TO_BACK_UP_LOAD -> dx - ELEMENT_CIRCLE_RADIUS * 2 + ARROW_HEIGHT
            ArrowDirection.TOP_CENTER_TO_GRID -> dx - ELEMENT_CIRCLE_RADIUS
            ArrowDirection.GRID_TO_TOP_CENTER -> dx - ELEMENT_CIRCLE_RADIUS
        }
    }

    /**
     * Get animator by direction from map
     * 从集合里取出动画，如果没有，返回null
     * @param arrowDirection
     * @return
     */
    private fun getAnimatorByDirectionFromMap(arrowDirection: ArrowDirection): ObjectAnimator? {
        if (animatorArray.isEmpty()) return null
        return animatorArray.get(arrowDirection.ordinal)
    }

    /**
     * Get animator by direction or new animator
     * 通过箭头方向获取动画（如果没有就创建一个，然后存起来）
     * @param arrowDirection
     * @return
     */
    private fun getAnimatorByDirectionIfNullCreateOne(arrowDirection: ArrowDirection): ObjectAnimator {
        val animator = getAnimatorByDirectionFromMap(arrowDirection)
        val startFloat = 0f
        val animatorLength = getAnimatorLengthByDirection(arrowDirection)
        // 还没有保存过的动画
        if (animator == null) {
            val propertyName = getAnimatorPropertyNameByDirection(arrowDirection)
            val newAnimator =
                ObjectAnimator.ofFloat(this, propertyName, startFloat, animatorLength)
                    .animatorConfig()
            animatorArray[arrowDirection.ordinal] = newAnimator
            return newAnimator
        }
        // 动画已经初始化过了，更新下动画起点和终点
        animator.setFloatValues(startFloat, animatorLength)
        return animator
    }

    private fun saveRunningAnimator(arrowDirection: ArrowDirection, objectAnimator: ObjectAnimator) {
        if (runningAnimatorArray.isEmpty()) {
            runningAnimatorArray[arrowDirection.ordinal] = objectAnimator
            return
        }
        val animator = runningAnimatorArray.get(arrowDirection.ordinal)
        if (animator == null) {
            runningAnimatorArray[arrowDirection.ordinal] = objectAnimator
        }
    }

    /**
     * Start arrow animation
     * 启动单独箭头动画
     * @param arrowDirection
     */
    private fun startArrowAnimation(arrowDirection: ArrowDirection) {
        if (isOffline) return
        // 已经启动过了，不需要再启动
        if (runningAnimatorArray.containsKey(arrowDirection.ordinal)) {
            return
        }
        val animatorByDirection = getAnimatorByDirectionIfNullCreateOne(arrowDirection)
        saveRunningAnimator(arrowDirection, animatorByDirection)
        if (animatorByDirection.isStarted) {
            return
        }
        animatorByDirection.start()
    }

    /**
     * Start arrow animations
     * 开启一些箭头动画
     * @param arrowDirections
     */
    private fun startArrowAnimations(arrowDirections: List<ArrowDirection>) {
        if (arrowDirections.isEmpty()) return
        if (isOffline) return
        // 移除多余的动画
        if (runningAnimatorArray.isNotEmpty()) {
            val valueIterator = runningAnimatorArray.valueIterator()
            valueIterator.forEach {
                val index = runningAnimatorArray.indexOfValue(it)
                val key = runningAnimatorArray.keyAt(index)
                var containsKey = false
                for (arrowDirection in arrowDirections) {
                    val ordinal = arrowDirection.ordinal
                    if (key == ordinal) {
                        containsKey = true
                        break
                    }
                }
                if (containsKey) {
                    return@forEach
                }
                // 新动画集合中不包含该动画，需要结束动画并且移出集合
                it.end()
                runningAnimatorArray.removeAt(index)
            }
        }
        arrowDirections.forEach {
            val animatorByDirection = getAnimatorByDirectionIfNullCreateOne(it)
            saveRunningAnimator(it, animatorByDirection)
            if (animatorByDirection.isStarted) {
                return@forEach
            }
            animatorByDirection.start()
        }
    }

    /**
     * Start arrow animations
     * 开启一些箭头动画
     * @param arrowDirections
     */
    fun startArrowAnimations(vararg arrowDirections: ArrowDirection) {
        startArrowAnimations(arrowDirections.toList())
    }

    /**
     * End arrow animation
     * 取消某个箭头动画
     * @param arrowDirection
     */
    fun endArrowAnimation(arrowDirection: ArrowDirection) {
        if (runningAnimatorArray.isEmpty()) {
            return
        }
        val animator = runningAnimatorArray.get(arrowDirection.ordinal) ?: return
        animator.end()
        runningAnimatorArray.remove(arrowDirection.ordinal)
    }

    /**
     * End arrow animations
     * 取消某些箭头动画
     * @param arrowDirections
     */
    fun endArrowAnimations(vararg arrowDirections: ArrowDirection) {
        if (arrowDirections.isEmpty() || runningAnimatorArray.isEmpty()) return
        // 停止动画并从正在进行的动画集合中移除
        arrowDirections.forEach {
            val animator = runningAnimatorArray.get(it.ordinal)
            animator.end()
            runningAnimatorArray.removeAt(runningAnimatorArray.indexOfValue(animator))
        }
    }

    /**
     * End all arrow animations
     * 取消所有箭头动画
     */
    fun endAllArrowAnimations() {
        if (runningAnimatorArray.isEmpty()) return
        runningAnimatorArray.forEach { _, anim ->
            anim.end()
        }
        runningAnimatorArray.clear()
    }

    /**
     * 动画基础配置
     *
     * @return
     */
    private fun ObjectAnimator.animatorConfig(): ObjectAnimator {
        interpolator = LinearInterpolator()
        repeatCount = ValueAnimator.INFINITE
        duration = ANIMATION_DURATION
        return this
    }

//    /**
//     * 获取动画时间
//     * S = VT
//     * 1、保证所有动画速度一致
//     * 2、保证时间一致
//     *
//     * @param animatorLength 动画路径长度 S
//     * @return
//     */
//    private fun getAnimatorDuration(animatorLength: Float): Long {
////        return (animatorLength / dy * ANIMATION_DURATION).toLong()
//        return ANIMATION_DURATION
//    }

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
        TOP_TO_CENTER, // 顶部 - 中间
        CENTER_TO_AC, // 中间 - AC Couple
        AC_TO_CENTER, // AC Couple - 中间
        CENTER_TO_GRID_LOAD, // 中间 - 电网负载
        BATTERY_TO_AC, // 电池 - AC Couple
        AC_TO_BATTERY, // AC Couple - 电池
        AC_TO_BACK_UP_LOAD, // AC Couple - Backup 负载
        TOP_CENTER_TO_GRID, // 中间顶部 - 电网
        GRID_TO_TOP_CENTER // 电网 - 中间顶部
    }

}