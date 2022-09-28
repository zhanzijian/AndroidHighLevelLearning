package com.example.viewapplication.view

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.util.SparseArray
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.util.*
import com.example.viewapplication.R
import com.example.viewapplication.config.AcConfiguration
import com.example.viewapplication.dp
import com.example.viewapplication.enumeration.ArrowDirection
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
private const val TAG = "AcCoupleEsPathView"
class AcCoupleEsPathView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    /**
     * 自定义的一些基础配置
     */
    private val acConfiguration: AcConfiguration = AcConfiguration.get(context)

    private val mElementCircleRadius: Float = acConfiguration.elementCircleRadius // 组件圆半径
    private val mCenterCircleRadius: Float = acConfiguration.centerCircleRadius // 中心圆半径
    private val mViewWidth: Float = acConfiguration.viewWidth // view 宽度
    private val mViewHeight: Float = acConfiguration.fullElementHeight // view 高度

    private val mArrowConcaveLength: Float = acConfiguration.arrowConcaveLength // 箭头内凹长度
    private val mArrowWidth: Float = acConfiguration.arrowWidth // 箭头宽度
    private val mArrowHeight: Float = acConfiguration.arrowHeight // 箭头高度

    private lateinit var pvInverterPaint: Paint
    private lateinit var batteryAcPaint: Paint
    private lateinit var inverterCenterPaint: Paint
    private lateinit var topCenterPaint: Paint
    private lateinit var gridLoadPaint: Paint
    private lateinit var centerAcPaint: Paint
    private lateinit var acBackupLoadPaint: Paint
    private lateinit var gridPaint: Paint
    private lateinit var mCirclePaint: Paint
    private lateinit var mOfflinePaint: Paint
    private lateinit var mBlueArrowPaint: Paint
    private lateinit var mGreenArrowPaint: Paint


    private val mAnimationDuration = acConfiguration.animationDuration

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
     * 电站是否离线
     */
    var isStationOffline = false
        set(value) {
            field = value
            invalidate()
        }

    /**
     * 所有的 AC 是否离线
     */
    var isAllAcOffline = false
        set(value) {
            field = value
            invalidate()
        }

    /**
     * 主模块的单个 AC 是否离线
     */
    var isSingleAcOffline = false
        set(value) {
            field = value
            invalidate()
        }

    /**
     * 电池 是否离线
     */
    var isBatteryOffline = false
        set(value) {
            field = value
            invalidate()
        }

    /**
     * 逆变器是否离线
     */
    var isInverterOffline = false
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

    init {
        initPaints()
    }

    private fun initPaints() {
        pvInverterPaint = acConfiguration.blueLinePaint
        batteryAcPaint = acConfiguration.blueLinePaint
        inverterCenterPaint = acConfiguration.greenLinePaint
        topCenterPaint = acConfiguration.greenLinePaint
        gridLoadPaint = acConfiguration.greenLinePaint
        centerAcPaint = acConfiguration.greenLinePaint
        acBackupLoadPaint = acConfiguration.greenLinePaint
        gridPaint = acConfiguration.greenLinePaint
        mCirclePaint = acConfiguration.circlePaint
        mOfflinePaint = acConfiguration.offlinePaint
        mBlueArrowPaint = acConfiguration.blueArrowPaint
        mGreenArrowPaint = acConfiguration.greenArrowPaint
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        // 一些基础变量初始化
        dx = mViewWidth / 2f
        dy = mViewHeight / 2f
        centerX = width / 2f
        centerY = height / 2f
        Log.d(TAG, "onSizeChanged: dx:$dx  dy:$dy centerX:$centerX centerY:$centerY")
    }

    /**
     * 动画已经启动
     *
     * @param arrowDirection
     * @return
     */
    private fun isAnimating(arrowDirection: ArrowDirection): Boolean {
        if (runningAnimatorArray.isEmpty()) return false
        return runningAnimatorArray.containsKey(arrowDirection.ordinal)
    }

    override fun onDraw(canvas: Canvas) {

        val inverterOffline = isStationOffline || isInverterOffline
        // PV -> 逆变器
        canvas.drawLine(
            centerX - dx,
            centerY - dy,
            centerX - dx,
            centerY,
            if (inverterOffline) mOfflinePaint else pvInverterPaint
        )
        if (inverterOffline) {
            endArrowAnimation(ArrowDirection.PV_TO_INVERTER)
        } else if (isAnimating(ArrowDirection.PV_TO_INVERTER)) {
            initPvToInverterArrowPath()
            canvas.drawPath(pvToInverterArrowPath, mBlueArrowPaint)
        }
        // 逆变器 -> 中心
        canvas.drawLine(
            centerX - dx,
            centerY,
            centerX,
            centerY,
            if (inverterOffline) mOfflinePaint else inverterCenterPaint
        )
        if (inverterOffline) {
            endArrowAnimation(ArrowDirection.INVERTER_TO_CENTER)
        } else if (isAnimating(ArrowDirection.INVERTER_TO_CENTER)) {
            initInverterToCenterArrowPath()
            canvas.drawPath(inverterToCenterArrowPath, mGreenArrowPaint)
        }

        val batteryOffline = isStationOffline || isBatteryOffline
        // 电池 <-> Ac Couple
        canvas.drawLine(
            centerX - dx,
            centerY + dy,
            centerX,
            centerY + dy,
            if (batteryOffline) mOfflinePaint else batteryAcPaint
        )
        if (batteryOffline) {
            endArrowAnimations(ArrowDirection.BATTERY_TO_AC, ArrowDirection.AC_TO_BATTERY)
        } else if (isAnimating(ArrowDirection.BATTERY_TO_AC)) {
            initBatteryToAcArrowPath()
            canvas.drawPath(batteryToAcArrowPath, mBlueArrowPaint)
        } else if (isAnimating(ArrowDirection.AC_TO_BATTERY)) {
            initAcToBatteryArrowPath()
            canvas.drawPath(acToBatteryArrowPath, mBlueArrowPaint)
        }

        // 所有 AC 离线，则电网和电网负载侧均离线
        val allAcOffline = isStationOffline || isAllAcOffline
        // 中心 <-> 顶部
        canvas.drawLine(
            centerX,
            centerY,
            centerX,
            centerY - dy,
            if (allAcOffline) mOfflinePaint else topCenterPaint
        )
        if (allAcOffline) {
            endArrowAnimations(ArrowDirection.CENTER_TO_TOP, ArrowDirection.TOP_TO_CENTER)
        } else if (isAnimating(ArrowDirection.CENTER_TO_TOP)) {
            initCenterToTopArrowPath()
            canvas.drawPath(centerToTopArrowPath, mGreenArrowPaint)
        } else if (isAnimating(ArrowDirection.TOP_TO_CENTER)) {
            initTopToCenterArrowPath()
            canvas.drawPath(topToCenterArrowPath, mGreenArrowPaint)
        }
        // 中心顶部 <-> 电网
        canvas.drawLine(
            centerX,
            centerY - dy,
            centerX + dx,
            centerY - dy,
            if (allAcOffline) mOfflinePaint else gridPaint
        )
        if (allAcOffline) {
            endArrowAnimations(ArrowDirection.TOP_CENTER_TO_GRID, ArrowDirection.GRID_TO_TOP_CENTER)
        } else if (isAnimating(ArrowDirection.TOP_CENTER_TO_GRID)) {
            initTopCenterToGridArrowPath()
            canvas.drawPath(topCenterToGridArrowPath, mGreenArrowPaint)
        } else if (isAnimating(ArrowDirection.GRID_TO_TOP_CENTER)) {
            initGridToTopCenterArrowPath()
            canvas.drawPath(gridToTopCenterArrowPath, mGreenArrowPaint)
        }
        // 中心 -> 电网负载
        canvas.drawLine(
            centerX,
            centerY,
            centerX + dx,
            centerY,
            if (allAcOffline) mOfflinePaint else gridLoadPaint
        )
        if (allAcOffline) {
            endArrowAnimation(ArrowDirection.CENTER_TO_GRID_LOAD)
        } else if (isAnimating(ArrowDirection.CENTER_TO_GRID_LOAD)) {
            initCenterToGridLoadArrowPath()
            canvas.drawPath(centerToGridLoadArrowPath, mGreenArrowPaint)
        }
        // 中心 <-> Ac Couple
        canvas.drawLine(
            centerX,
            centerY,
            centerX,
            centerY + dy,
            if (allAcOffline) mOfflinePaint else centerAcPaint
        )
        if (allAcOffline) {
            endArrowAnimations(ArrowDirection.CENTER_TO_AC, ArrowDirection.AC_TO_CENTER)
        } else if (isAnimating(ArrowDirection.CENTER_TO_AC)) {
            initCenterToAcArrowPath()
            canvas.drawPath(centerToAcArrowPath, mGreenArrowPaint)
        } else if (isAnimating(ArrowDirection.AC_TO_CENTER)) {
            initAcToCenterArrowPath()
            canvas.drawPath(acToCenterArrowPath, mGreenArrowPaint)
        }
        // 中心原点
        mCirclePaint.color = if (allAcOffline) acConfiguration.grayColor else acConfiguration.greenColor
        canvas.drawCircle(
            centerX,
            centerY,
            mCenterCircleRadius,
            mCirclePaint
        )

        val singleAcOffline = isStationOffline || isSingleAcOffline
        // Ac Couple -> backup 负载
        canvas.drawLine(
            centerX,
            centerY + dy,
            centerX + dx,
            centerY + dy,
            if (singleAcOffline) mOfflinePaint else acBackupLoadPaint
        )
        if (singleAcOffline) {
            endArrowAnimation(ArrowDirection.AC_TO_BACK_UP_LOAD)
        } else if (isAnimating(ArrowDirection.AC_TO_BACK_UP_LOAD)) {
            initAcToBackupArrowPath()
            canvas.drawPath(acToBackupArrowPath, mGreenArrowPaint)
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
            centerY - dy + mElementCircleRadius + mArrowConcaveLength + pvToInverterDy
        )
        pvToInverterArrowPath.rLineTo(
            mArrowWidth / 2f,
            -mArrowConcaveLength
        )
        pvToInverterArrowPath.rLineTo(-mArrowWidth / 2f, mArrowHeight)
        pvToInverterArrowPath.rLineTo(
            -mArrowWidth / 2f,
            -mArrowHeight
        )
        pvToInverterArrowPath.close()
    }

    /**
     * 初始化 逆变器-中心原点 箭头的path
     */
    private fun initInverterToCenterArrowPath() {
        if (!inverterToCenterArrowPath.isEmpty) {
            inverterToCenterArrowPath.reset()
        }
        with(inverterToCenterArrowPath) {
            moveTo(
                centerX - dx + mElementCircleRadius + mArrowConcaveLength + inverterToCenterDx,
                centerY
            )
            rLineTo(-mArrowConcaveLength, -mArrowWidth / 2f)
            rLineTo(mArrowHeight, mArrowWidth / 2f)
            rLineTo(-mArrowHeight, mArrowWidth / 2f)
            close()
        }
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
            centerY - mArrowConcaveLength - centerToTopDy
        )
        centerToTopArrowPath.rLineTo(
            -mArrowWidth / 2f,
            mArrowConcaveLength
        )
        centerToTopArrowPath.rLineTo(mArrowWidth / 2f, -mArrowHeight)
        centerToTopArrowPath.rLineTo(mArrowWidth / 2f, mArrowHeight)
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
        topToCenterArrowPath.rLineTo(
            mArrowWidth / 2f,
            -mArrowConcaveLength
        )
        topToCenterArrowPath.rLineTo(-mArrowWidth / 2f, mArrowHeight)
        topToCenterArrowPath.rLineTo(-mArrowWidth / 2f, -mArrowHeight)
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
            centerY + mArrowConcaveLength + centerToAcDy
        )
        centerToAcArrowPath.rLineTo(
            -mArrowWidth / 2f,
            -mArrowConcaveLength
        )
        centerToAcArrowPath.rLineTo(mArrowWidth / 2f, mArrowHeight)
        centerToAcArrowPath.rLineTo(mArrowWidth / 2f, -mArrowHeight)
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
            centerY + dy - mElementCircleRadius + mArrowConcaveLength - acToCenterDy
        )
        acToCenterArrowPath.rLineTo(
            -mArrowWidth / 2f,
            mArrowConcaveLength
        )
        acToCenterArrowPath.rLineTo(mArrowWidth / 2f, -mArrowHeight)
        acToCenterArrowPath.rLineTo(mArrowWidth / 2f, mArrowHeight)
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
            centerX + mArrowConcaveLength + centerToGridLoadDx,
            centerY
        )
        centerToGridLoadArrowPath.rLineTo(
            -mArrowConcaveLength,
            -mArrowWidth / 2f
        )
        centerToGridLoadArrowPath.rLineTo(
            mArrowHeight,
            mArrowWidth / 2f
        )
        centerToGridLoadArrowPath.rLineTo(
            -mArrowHeight,
            mArrowWidth / 2f
        )
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
            centerX - dx + mElementCircleRadius - mArrowHeight + batteryToAcDx,
            centerY + dy
        )
        batteryToAcArrowPath.rLineTo(
            -mArrowConcaveLength,
            -mArrowWidth / 2f
        )
        batteryToAcArrowPath.rLineTo(mArrowHeight, mArrowWidth / 2f)
        batteryToAcArrowPath.rLineTo(-mArrowHeight, mArrowWidth / 2f)
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
            centerX - mElementCircleRadius + mArrowHeight - acToBatteryDx,
            centerY + dy
        )
        acToBatteryArrowPath.rLineTo(
            mArrowConcaveLength,
            -mArrowWidth / 2f
        )
        acToBatteryArrowPath.rLineTo(-mArrowHeight, mArrowWidth / 2f)
        acToBatteryArrowPath.rLineTo(mArrowHeight, mArrowWidth / 2f)
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
            centerX + mElementCircleRadius - mArrowHeight + acToBackupDx,
            centerY + dy
        )
        acToBackupArrowPath.rLineTo(
            -mArrowConcaveLength,
            -mArrowWidth / 2f
        )
        acToBackupArrowPath.rLineTo(mArrowHeight, mArrowWidth / 2f)
        acToBackupArrowPath.rLineTo(-mArrowHeight, mArrowWidth / 2f)
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
        topCenterToGridArrowPath.rLineTo(
            -mArrowConcaveLength,
            -mArrowWidth / 2f
        )
        topCenterToGridArrowPath.rLineTo(
            mArrowHeight,
            mArrowWidth / 2f
        )
        topCenterToGridArrowPath.rLineTo(
            -mArrowHeight,
            mArrowWidth / 2f
        )
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
            centerX + dx - mElementCircleRadius + mArrowHeight - gridToTopCenterDx,
            centerY - dy
        )
        gridToTopCenterArrowPath.rLineTo(
            mArrowConcaveLength,
            -mArrowWidth / 2f
        )
        gridToTopCenterArrowPath.rLineTo(
            -mArrowHeight,
            mArrowWidth / 2f
        )
        gridToTopCenterArrowPath.rLineTo(
            mArrowHeight,
            mArrowWidth / 2f
        )
        gridToTopCenterArrowPath.close()
    }

    // ----------- 动画启动和取消区域开始（注意，动画需要主动结束，如启动双向互斥动画中的一个，需先结束其互斥的动画） ------------ //


    /**
     * Get animator property name by direction
     * 通过箭头方向获取动画属性名称
     * @param arrowDirection
     * @return
     */
    private fun getAnimatorPropertyNameByDirection(arrowDirection: ArrowDirection): String? {
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
            else -> null
        }
    }

    /**
     * Get animator length by direction
     * 通过箭头方向获取动画的长度
     * @param arrowDirection
     * @return
     */
    private fun getAnimatorLengthByDirection(arrowDirection: ArrowDirection): Float? {
        return when (arrowDirection) {
            ArrowDirection.PV_TO_INVERTER -> dy - mElementCircleRadius * 2
            ArrowDirection.INVERTER_TO_CENTER -> dx - mElementCircleRadius - mArrowHeight
            ArrowDirection.CENTER_TO_TOP -> dy - mArrowHeight
            ArrowDirection.TOP_TO_CENTER -> dy - mArrowHeight
            ArrowDirection.CENTER_TO_AC -> dy - mElementCircleRadius
            ArrowDirection.AC_TO_CENTER -> dy - mElementCircleRadius
            ArrowDirection.CENTER_TO_GRID_LOAD -> dx - mArrowHeight
            ArrowDirection.BATTERY_TO_AC, ArrowDirection.AC_TO_BATTERY, ArrowDirection.AC_TO_BACK_UP_LOAD
            -> dx - mElementCircleRadius * 2 + mArrowHeight
            ArrowDirection.TOP_CENTER_TO_GRID -> dx - mElementCircleRadius
            ArrowDirection.GRID_TO_TOP_CENTER -> dx - mElementCircleRadius
            else -> null
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
    private fun getAnimatorByDirectionIfNullCreateOne(arrowDirection: ArrowDirection): ObjectAnimator? {
        val animator = getAnimatorByDirectionFromMap(arrowDirection)
        val startFloat = 0f
        val animatorLength = getAnimatorLengthByDirection(arrowDirection) ?: return null
        // 还没有保存过的动画
        if (animator == null) {
            val propertyName = getAnimatorPropertyNameByDirection(arrowDirection) ?: return null
            Log.d(
                TAG,
                "getAnimatorByDirectionIfNullCreateOne: propertyName:$propertyName  animatorLength:$animatorLength"
            )
            val newAnimator =
                ObjectAnimator.ofFloat(this, propertyName, startFloat, animatorLength)
                    .animatorConfig()
            animatorArray.put(arrowDirection.ordinal, newAnimator)
            return newAnimator
        }
        // 动画已经初始化过了，更新下动画起点和终点
        animator.setFloatValues(startFloat, animatorLength)
        return animator
    }

    private fun saveRunningAnimator(
        arrowDirection: ArrowDirection,
        objectAnimator: ObjectAnimator
    ) {
        if (runningAnimatorArray.isEmpty()) {
            runningAnimatorArray.put(arrowDirection.ordinal, objectAnimator)
            return
        }
        val animator = runningAnimatorArray.get(arrowDirection.ordinal)
        if (animator == null) {
            runningAnimatorArray.put(arrowDirection.ordinal, objectAnimator)
        }
    }

    /**
     * Start arrow animation
     * 启动单独箭头动画
     * @param arrowDirection
     */
    fun startArrowAnimation(arrowDirection: ArrowDirection) {
        startArrowAnimations(arrowDirection)
    }

    /**
     * Start arrow animations
     * 开启一些箭头动画
     * @param arrowDirections
     */
    fun startArrowAnimations(arrowDirections: List<ArrowDirection>) {
        if (arrowDirections.isEmpty()) return
        if (isStationOffline) return
        post {
            arrowDirections.forEach {
                Log.d(
                    TAG,
                    "startArrowAnimation: ArrowDirection name: ${it.name} + ordinal: ${it.ordinal}"
                )
                val animatorByDirection =
                    getAnimatorByDirectionIfNullCreateOne(it) ?: return@forEach
                saveRunningAnimator(it, animatorByDirection)
                if (animatorByDirection.isStarted) {
                    return@forEach
                }
                animatorByDirection.start()
            }
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
        runningAnimatorArray.remove(arrowDirection.ordinal)
        post {
            animator.end()
        }
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
            Log.d(
                TAG,
                "startArrowAnimation: ArrowDirection name: ${it.name} + ordinal: ${it.ordinal}"
            )
            val animator = runningAnimatorArray.get(it.ordinal) ?: return@forEach
            runningAnimatorArray.removeAt(runningAnimatorArray.indexOfValue(animator))
            post {
                animator.end()
            }
        }
    }

    /**
     * End all arrow animations
     * 取消所有箭头动画
     */
    fun endAllArrowAnimations() {
        if (runningAnimatorArray.isEmpty()) return
        runningAnimatorArray.forEach { _, anim ->
            anim.cancel()
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
//        startDelay = ANIMATION_START_DELAY
        duration = mAnimationDuration
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
////        return (animatorLength / dy * mAnimationDuration).toLong()
//        return mAnimationDuration
//    }

    // ---------------- 动画启动和取消区域结束 -------------------- //


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // 留出空间放各大组件
        setMeasuredDimension(
            (mViewWidth + mElementCircleRadius * 2).toInt(),
            (mViewHeight + mElementCircleRadius * 2).toInt()
        )
    }
}