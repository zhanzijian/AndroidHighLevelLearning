package com.example.viewapplication.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.util.SparseArray
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.util.containsKey
import androidx.core.util.forEach
import androidx.core.util.isEmpty
import com.example.viewapplication.config.EsConfiguration
import com.example.viewapplication.enumeration.EsAnimationDirection
import kotlin.math.abs

private const val TAG = "EsPathView"

/**
 *
 * @description 储能动图路径和小球动画
 * @author zhanzijian
 * @date 2022/02/24 14:35
 */
class EsPathView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val esConfig = EsConfiguration.get(context)

    private val mHorizontalDistance = esConfig.horizontalDistance
    private val mVerticalDistance = esConfig.verticalDistance
    private val mArcRadius = esConfig.arcRadius
    private val mMaxBallRadius = esConfig.maxBallRadius
    private val mPathHorizontalPadding = esConfig.pathHorizontalPadding
    private val mExtraWidth = esConfig.extraWidth
    private val mBallInnerRadius = esConfig.ballInnerRadius
    private val mBallOuterRadius = esConfig.ballOuterRadius
    private val mCircleRadius = esConfig.circleRadius
    private val mHorizontalExtra = esConfig.horizontalExtra
    private val mGridLoadDy = esConfig.gridLoadDy

    private lateinit var strokeCirclePaint: Paint
    private lateinit var fillCirclePaint: Paint
    private lateinit var progressPaint: Paint
    private lateinit var ballPaint: Paint
    private lateinit var pathPaint: Paint
    private lateinit var offlinePaint: Paint

    private val mAnimationDuration = esConfig.animationDuration
    private val mAnimationDelay = esConfig.animationDelay


    /* -------------- PV - 逆变器 ----------------*/
    private val pvInverterPath by lazy(::Path)
    private val pvInverterPos = floatArrayOf(0f, 0f)
    /**
     * 展示 PV 路径
     */
    var showPvPath = true
        set(value) {
            field = value
            if (value && pvInverterPath.isEmpty) {
                initPvToInverterPath()
            }
            invalidate()
        }
    /**
     * PV 模块是否离线
     */
    var isPvOffline = false
        set(value) {
            field = value
            invalidate()
        }

    var pvBallPercent = 1.0f
        set(value) {
            field = value
            invalidate()
        }

    /* -------------- 电网 - 逆变器 ----------------*/
    private val gridInverterPath by lazy(::Path)
    private val gridInverterPos = floatArrayOf(0f, 0f)
    /**
     * 展示电网-逆变器路径（同逆变器-电网）
     */
    var showGridToInverterPath = true
        set(value) {
            field = value
            if (value && gridInverterPath.isEmpty) {
                initGridToInverterPath()
            }
            invalidate()
        }
    /**
     * 电网-逆变器 模块是否离线
     */
    var isGridToInverterOffline = false
        set(value) {
            field = value
            invalidate()
        }
    var gridToInverterBallPercent = 1.0f
        set(value) {
            field = value
            invalidate()
        }

    /**
     * 当日是否卖电 卖电-true 买电-false 默认卖电
     */
    var isTodayExported = true
        set(value) {
            field = value
            invalidate()
        }

    /* -------------- 电池 - 逆变器 ----------------*/
    private val batteryInverterPath by lazy(::Path)
    private val batteryInverterPos = floatArrayOf(0f, 0f)
    /**
     * 展示电池路径
     */
    var showBatteryPath = true
        set(value) {
            field = value
            if (value && batteryInverterPath.isEmpty) {
                initBatteryToInverterPath()
            }
            invalidate()
        }
    /**
     * 电池 模块是否离线
     */
    var isBatteryOffline = false
        set(value) {
            field = value
            invalidate()
        }
    var batteryBallPercent = 1.0f
        set(value) {
            field = value
            invalidate()
        }
    /**
     * 当日电池是否充电 充电-true 放电-false 默认充电
     */
    var isTodayCharge = true
        set(value) {
            field = value
            invalidate()
        }

    /* -------------- 逆变器 - backup负载 ----------------*/
    private val inverterToBackupPath by lazy(::Path)
    private val inverterToBackupPos = floatArrayOf(0f, 0f)
    /**
     * 展示 backup 负载路径
     */
    var showBackupLoadPath = true
        set(value) {
            field = value
            if (value && inverterToBackupPath.isEmpty) {
                initBackupLoadToInverterPath()
            }
            invalidate()
        }
    /**
     * backup 负载 模块是否离线
     */
    var isBackupOffline = false
        set(value) {
            field = value
            invalidate()
        }
    var backupBallPercent = 1.0f
        set(value) {
            field = value
            invalidate()
        }

    /* -------------- 电网 - 电网负载 ----------------*/
    private val gridToGridLoadPath by lazy(::Path)
    private val gridToGridLoadPos = floatArrayOf(0f, 0f)
    /**
     * 展示电网-电网负载路径
     */
    var showGridToGridLoadPath = true
        set(value) {
            field = value
            if (value && gridToGridLoadPath.isEmpty) {
                initGridToGridLoadPath()
            }
            invalidate()
        }
    /**
     * 电网-电网负载 模块是否离线
     */
    var isGridToGridLoadOffline = false
        set(value) {
            field = value
            invalidate()
        }
    var gridToGridLoadBallPercent = 1.0f
        set(value) {
            field = value
            invalidate()
        }

    /* -------------- 逆变器 - 电网负载 ----------------*/
    private val inverterToGridLoadPath by lazy(::Path)
    private val inverterToGridLoadPos = floatArrayOf(0f, 0f)
    /**
     * 展示逆变器-电网负载路径
     */
    var showGridLoadToInverterPath = true
        set(value) {
            field = value
            if (value && inverterToGridLoadPath.isEmpty) {
                initGridLoadToInverterPath()
            }
            invalidate()
        }
    /**
     * 逆变器-电网负载 模块是否离线
     */
    var isInverterToGridLoadOffline = false
        set(value) {
            field = value
            invalidate()
        }
    var inverterToGridLoadBallPercent = 1.0f
        set(value) {
            field = value
            invalidate()
        }

    /* -------------- 发电机 - 逆变器 ----------------*/
    private val genToInverterPath by lazy(::Path)
    private val genToInverterPos = floatArrayOf(0f, 0f)
    /**
     * 展示 发电机 路径
     */
    var showGenPath = false
        set(value) {
            field = value
            if (value && genToInverterPath.isEmpty) {
                initGenToInverterPath()
            }
            invalidate()
        }
    /**
     * 发电机 模块是否离线
     */
    var isGenOffline = false
        set(value) {
            field = value
            invalidate()
        }

    var genBallPercent = 1.0f
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
     * 储存动画，key - EsAnimationDirection的序号  value - 动画对象
     */
    private val animatorArray = SparseArray<ValueAnimator>()

    /**
     * 正在进行的动画集合
     */
    private val runningAnimatorArray = SparseArray<ValueAnimator>()


    /**
     * 需要反向执行动画的方向集合 （ 规定：所有小球从中心逆变器出发的均为反向）
     */
    private val reversedDirectionSet by lazy {
        arrayListOf(
            EsAnimationDirection.INVERTER_TO_BATTERY,
            EsAnimationDirection.INVERTER_TO_GRID,
            EsAnimationDirection.INVERTER_TO_BACKUP_LOAD,
            EsAnimationDirection.INVERTER_TO_GRID_LOAD
        )
    }

    init {
        initPaints()
    }

    private fun initPaints() {
        strokeCirclePaint = esConfig.strokeCirclePaint
        fillCirclePaint = esConfig.fillCirclePaint
        progressPaint = esConfig.progressPaint
        ballPaint = esConfig.ballPaint
        pathPaint = esConfig.pathPaint
        offlinePaint = esConfig.offlinePaint
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        if (showPvPath) {
            initPvToInverterPath()
        }
        if (showGridToInverterPath) {
            initGridToInverterPath()
        }
        if (showBatteryPath) {
            initBatteryToInverterPath()
        }
        if (showBackupLoadPath) {
            initBackupLoadToInverterPath()
        }
        if (showGridToGridLoadPath) {
            initGridToGridLoadPath()
        }
        if (showGridLoadToInverterPath) {
            initGridLoadToInverterPath()
        }
        if (showGenPath) {
            initGenToInverterPath()
        }
    }

    /**
     * 初始化 发电机 - 逆变器 小球运动路径
     */
    private fun initGenToInverterPath() {
        if (!genToInverterPath.isEmpty) {
            genToInverterPath.reset()
        }
        // 中心点坐标
        val centerX = width / 2f
        val centerY = height / 2f
        // path初始位置
        genToInverterPos[0] =
            centerX - (mPathHorizontalPadding / 2 + mArcRadius + mHorizontalDistance) - mMaxBallRadius - mHorizontalExtra
        genToInverterPos[1] = centerY
        genToInverterPath.moveTo(
            genToInverterPos[0],
            genToInverterPos[1]
        )
        genToInverterPath.rLineTo((mPathHorizontalPadding / 2 + mArcRadius + mHorizontalDistance) - mMaxBallRadius - mHorizontalExtra, 0f)
    }

    /**
     * 初始化 逆变器 - 电网负载 小球运动路径
     */
    private fun initGridLoadToInverterPath() {
        if (!inverterToGridLoadPath.isEmpty) {
            inverterToGridLoadPath.reset()
        }
        // 中心点坐标
        val centerX = width / 2f
        val centerY = height / 2f
        // path初始位置
        inverterToGridLoadPos[0] =
            centerX + (mPathHorizontalPadding / 2 + mArcRadius + mHorizontalDistance) + mMaxBallRadius + mHorizontalExtra
        inverterToGridLoadPos[1] = centerY - mGridLoadDy
        inverterToGridLoadPath.moveTo(inverterToGridLoadPos[0], inverterToGridLoadPos[1])
        inverterToGridLoadPath.rLineTo(
            -(mArcRadius + mHorizontalDistance + mMaxBallRadius + mHorizontalExtra),
            0f
        )
    }

    /**
     * 初始化 电网 - 电网负载 小球运动路径
     */
    private fun initGridToGridLoadPath() {
        if (!gridToGridLoadPath.isEmpty) {
            gridToGridLoadPath.reset()
        }
        // 中心点坐标
        val centerX = width / 2f
        val centerY = height / 2f

        // path初始位置
        gridToGridLoadPos[0] =
            centerX + (mPathHorizontalPadding / 2 + mArcRadius + mHorizontalDistance) + mMaxBallRadius + mHorizontalExtra
        gridToGridLoadPos[1] = centerY - (mVerticalDistance + mArcRadius)
        gridToGridLoadPath.moveTo(
            gridToGridLoadPos[0],
            gridToGridLoadPos[1]
        )
        gridToGridLoadPath.lineTo(
            centerX + (mPathHorizontalPadding / 2 + mArcRadius),
            centerY - (mVerticalDistance + mArcRadius)
        )
        gridToGridLoadPath.arcTo(
            centerX + mPathHorizontalPadding / 2,
            centerY - (mVerticalDistance + mArcRadius),
            centerX + (mPathHorizontalPadding / 2 + mArcRadius * 2),
            centerY - mVerticalDistance + mArcRadius,
            270f,
            -90f,
            false
        )
        gridToGridLoadPath.lineTo(
            centerX + mPathHorizontalPadding / 2,
            centerY - mGridLoadDy
        )
        gridToGridLoadPath.lineTo(
            gridToGridLoadPos[0],
            centerY - mGridLoadDy
        )

    }

    /**
     * 初始化 PV - 逆变器 小球运动路径
     */
    private fun initPvToInverterPath() {
        if (!pvInverterPath.isEmpty) {
            pvInverterPath.reset()
        }
        // 中心点坐标
        val centerX = width / 2f
        val centerY = height / 2f

        // path初始位置
        pvInverterPos[0] =
            centerX - (mPathHorizontalPadding / 2 + mArcRadius + mHorizontalDistance) - mMaxBallRadius - mHorizontalExtra
        pvInverterPos[1] = centerY - (mVerticalDistance + mArcRadius)
        pvInverterPath.moveTo(
            pvInverterPos[0],
            pvInverterPos[1]
        )
        pvInverterPath.lineTo(
            centerX - (mPathHorizontalPadding / 2 + mArcRadius),
            centerY - (mVerticalDistance + mArcRadius)
        )
        pvInverterPath.arcTo(
            centerX - (mPathHorizontalPadding / 2 + mArcRadius * 2),
            centerY - (mVerticalDistance + mArcRadius),
            centerX - mPathHorizontalPadding / 2,
            centerY - mVerticalDistance + mArcRadius,
            270f,
            90f,
            false
        )
        pvInverterPath.lineTo(
            centerX - mPathHorizontalPadding / 2,
            centerY
        )
    }

    /**
     * 初始化 电网 - 逆变器 小球运动路径
     */
    private fun initGridToInverterPath() {
        if (!gridInverterPath.isEmpty) {
            gridInverterPath.reset()
        }
        // 中心点坐标
        val centerX = width / 2f
        val centerY = height / 2f

        // path初始位置
        gridInverterPos[0] =
            centerX + (mPathHorizontalPadding / 2 + mArcRadius + mHorizontalDistance) + mMaxBallRadius + mHorizontalExtra
        gridInverterPos[1] = centerY - (mVerticalDistance + mArcRadius)

        gridInverterPath.moveTo(
            gridInverterPos[0],
            gridInverterPos[1]
        )
        gridInverterPath.lineTo(
            centerX + (mPathHorizontalPadding / 2 + mArcRadius),
            centerY - (mVerticalDistance + mArcRadius)
        )
        gridInverterPath.arcTo(
            centerX + mPathHorizontalPadding / 2,
            centerY - (mVerticalDistance + mArcRadius),
            centerX + (mPathHorizontalPadding / 2 + mArcRadius * 2),
            centerY - mVerticalDistance + mArcRadius,
            270f,
            -90f,
            false
        )
        gridInverterPath.lineTo(
            centerX + mPathHorizontalPadding / 2,
            centerY
        )
    }

    /**
     * 初始化 电池 - 逆变器 小球运动路径
     */
    private fun initBatteryToInverterPath() {
        if (!batteryInverterPath.isEmpty) {
            batteryInverterPath.reset()
        }
        // 中心点坐标
        val centerX = width / 2f
        val centerY = height / 2f

        // path初始位置
        batteryInverterPos[0] =
            centerX - (mPathHorizontalPadding / 2 + mArcRadius + mHorizontalDistance) - mMaxBallRadius - mHorizontalExtra
        batteryInverterPos[1] = centerY + (mVerticalDistance + mArcRadius)

        // 移动至path初始位置
        batteryInverterPath.moveTo(
            batteryInverterPos[0],
            batteryInverterPos[1]
        )
        batteryInverterPath.lineTo(
            centerX - (mPathHorizontalPadding / 2 + mArcRadius),
            centerY + (mVerticalDistance + mArcRadius)
        )
        batteryInverterPath.arcTo(
            centerX - (mPathHorizontalPadding / 2 + mArcRadius * 2),
            centerY + mVerticalDistance - mArcRadius,
            centerX - mPathHorizontalPadding / 2,
            centerY + (mVerticalDistance + mArcRadius),
            90f,
            -90f,
            false
        )
        batteryInverterPath.lineTo(
            centerX - mPathHorizontalPadding / 2,
            centerY
        )
    }

    /**
     * 初始化 Backup负载 - 电网 小球运动路径
     */
    private fun initBackupLoadToInverterPath() {
        if (!inverterToBackupPath.isEmpty) {
            inverterToBackupPath.reset()
        }
        // 中心点坐标
        val centerX = width / 2f
        val centerY = height / 2f

        // 小球初始位置
        inverterToBackupPos[0] =
            centerX + (mPathHorizontalPadding / 2 + mArcRadius + mHorizontalDistance) + mMaxBallRadius + mHorizontalExtra
        inverterToBackupPos[1] = centerY + (mVerticalDistance + mArcRadius)

        // 移动至path初始位置
        inverterToBackupPath.moveTo(
            inverterToBackupPos[0],
            inverterToBackupPos[1]
        )
        inverterToBackupPath.lineTo(
            centerX + (mPathHorizontalPadding / 2 + mArcRadius),
            centerY + (mVerticalDistance + mArcRadius)
        )
        inverterToBackupPath.arcTo(
            centerX + mPathHorizontalPadding / 2,
            centerY + mVerticalDistance - mArcRadius,
            centerX + (mPathHorizontalPadding / 2 + mArcRadius * 2),
            centerY + (mVerticalDistance + mArcRadius),
            90f,
            90f,
            false
        )
        inverterToBackupPath.lineTo(
            centerX + mPathHorizontalPadding / 2,
            centerY
        )
    }


    override fun onDraw(canvas: Canvas) {
        // 中心点坐标
        val centerX = width / 2f
        val centerY = height / 2f

        // 画 PV - 逆变器 部分
        drawPvInverter(centerX, centerY, canvas)

        // 画 发电机 - 逆变器 部分
        drawGenToInverter(centerX, centerY, canvas)

        // 电网-逆变器的路径需要优先显示 遵循离线的优化绘制的原则 防止离线的颜色盖住在线的颜色
        // 如果显示 电网-逆变器 的路径，则需要先画 电网-电网负载 和 逆变器-电网负载 的路径，再画 电网-逆变器 的路径，让 电网-逆变器 的路径盖在另外两个的上面
        // 反之则需要先画 电网-逆变器 的路径，再画 电网-电网负载 和 逆变器-电网负载 的路径，让 电网-电网负载 和 逆变器-电网负载 的路径盖在 电网-逆变器 的路径的上面
        if (isGridToInverterOffline) {
            // 画 电网 - 逆变器 部分
            drawGridToInverter(centerX, centerY, canvas)
            sortToGridLoadDrawLevel(canvas)
        } else {
            sortToGridLoadDrawLevel(canvas)
            // 画 电网 - 逆变器 部分
            drawGridToInverter(centerX, centerY, canvas)
        }

        // 画 电池 - 逆变器 部分
        drawBatteryInverter(centerX, centerY, canvas)

        // 画 backup负载 - 逆变器 部分
        drawBackupLoadInverter(centerX, centerY, canvas)
    }

    /**
     * 对到电网负载的绘制先后排序
     * @param canvas
     */
    private fun sortToGridLoadDrawLevel(canvas: Canvas) {
        if (isGridToGridLoadOffline) {
            // 电网-电网负载
            drawGridToGridLoad(canvas)
            // 逆变器-电网负载
            drawInverterToGridLoad(canvas)
        } else {
            // 逆变器-电网负载
            drawInverterToGridLoad(canvas)
            // 电网-电网负载
            drawGridToGridLoad(canvas)
        }
    }

    /**
     * Get final ball percent
     * @param ballPercent
     * @return
     */
    private fun getFinalBallPercent(ballPercent: Float): Float {
        var finalBallPercent = abs(ballPercent)
        if (finalBallPercent < EsConfiguration.MIN_BALL_PROPORTION) {
            finalBallPercent = EsConfiguration.MIN_BALL_PROPORTION
        }
        if (finalBallPercent > EsConfiguration.MAX_BALL_PROPORTION) {
            finalBallPercent = EsConfiguration.MAX_BALL_PROPORTION
        }
        return finalBallPercent
    }

    /**
     * 画运动小球内径
     *
     * @param canvas
     * @param cx
     * @param cy
     * @param ballPercent 小球比例
     */
    private fun drawInnerBall(canvas: Canvas, cx: Float, cy: Float, ballPercent: Float) {
        // 计算小球内径
        val finalBallPercent = getFinalBallPercent(ballPercent)
        val innerRadius = mBallInnerRadius * finalBallPercent
        canvas.drawCircle(
            cx,
            cy,
            innerRadius,
            ballPaint
        )
    }

    /**
     * 画运动小球外径
     *
     * @param canvas
     * @param cx
     * @param cy
     * @param ballPercent 小球比例
     */
    private fun drawOuterBall(canvas: Canvas, cx: Float, cy: Float, ballPercent: Float) {
        // 计算小球外径
        val finalBallPercent = getFinalBallPercent(ballPercent)
        val outerRadius = mBallOuterRadius * finalBallPercent
        canvas.drawCircle(
            cx,
            cy,
            outerRadius,
            ballPaint
        )
    }

    /**
     * 动画已经启动
     *
     * @param EsAnimationDirection
     * @return
     */
    private fun isAnimating(EsAnimationDirection: EsAnimationDirection): Boolean {
        if (runningAnimatorArray.isEmpty()) return false
        return runningAnimatorArray.containsKey(EsAnimationDirection.ordinal)
    }

    /**
     * Draw pv inverter
     * PV - 逆变器
     * @param centerX
     * @param centerY
     * @param canvas
     */
    private fun drawPvInverter(centerX: Float, centerY: Float, canvas: Canvas) {
        if (!showPvPath) return
        // 离线
        val pvOffline = isStationOffline || isPvOffline
        if (pvOffline) {
            canvas.drawPath(pvInverterPath, offlinePaint)
            endBallAnimation(EsAnimationDirection.PV_TO_INVERTER)
            return
        }
        // 画路径
        pathPaint.color = esConfig.yellowColor
        canvas.drawPath(pvInverterPath, pathPaint)

        // 动画未开始，不画小球
        if (!isAnimating(EsAnimationDirection.PV_TO_INVERTER)) {
            return
        }
        // 动画开始，画小球
        // 初始小球圆心x轴位置
        val pos0 = pvInverterPos[0]
        val pos1 = pvInverterPos[1]
        // 画运动小球内径
        ballPaint.color = esConfig.yellowColor
        drawInnerBall(canvas, pos0, pos1, pvBallPercent)
        // 画运动小球外径
        ballPaint.color = esConfig.p33YellowColor
        drawOuterBall(canvas, pos0, pos1, pvBallPercent)

        // 画圆
        // 先画个白色填充的圆盖住小球
        canvas.drawCircle(
            centerX - (mPathHorizontalPadding / 2 + mArcRadius + mHorizontalDistance + mCircleRadius),
            centerY - (mVerticalDistance + mArcRadius),
            mCircleRadius,
            fillCirclePaint
        )
//        // 再画个圆环
//        strokeCirclePaint.color = getColorById(context, R.color.yellow_4d_f0cf00_color)
//        canvas.drawCircle(
//            centerX - (mPathHorizontalPadding / 2 + mArcRadius + mHorizontalDistance + mCircleRadius),
//            centerY - (mVerticalDistance + mArcRadius),
//            mCircleRadius,
//            strokeCirclePaint
//        )
//        // 画进度
//        progressPaint.color = getColorById(context, R.color.yellow_f0cf00_color)
//        canvas.drawArc(
//            centerX - (mPathHorizontalPadding / 2 + mArcRadius + mHorizontalDistance + mCircleRadius) - mCircleRadius,
//            centerY - (mVerticalDistance + mArcRadius) - mCircleRadius,
//            centerX - (mPathHorizontalPadding / 2 + mArcRadius + mHorizontalDistance + mCircleRadius) + mCircleRadius,
//            centerY - (mVerticalDistance + mArcRadius) + mCircleRadius,
//            -90f,
//            90f,
//            false,
//            progressPaint
//        )
    }

    /**
     * 画发电机
     *
     * @param canvas
     */
    private fun drawGenToInverter(centerX: Float, centerY: Float, canvas: Canvas) {
        if (!showGenPath) return
        // 离线
        val genOffline = isStationOffline || isGenOffline
        if (genOffline) {
            canvas.drawPath(genToInverterPath, offlinePaint)
            endBallAnimation(EsAnimationDirection.GEN_TO_INVERTER)
            return
        }
        // 画路径
        pathPaint.color = esConfig.blue06b389Color
        canvas.drawPath(genToInverterPath, pathPaint)

        // 动画未开始，不画小球
        if (!isAnimating(EsAnimationDirection.GEN_TO_INVERTER)) {
            return
        }
        // 动画开始，画小球
        // 初始小球圆心x轴位置
        val pos0 = genToInverterPos[0]
        val pos1 = genToInverterPos[1]
        // 画运动小球内径
        ballPaint.color = esConfig.blue06b389Color
        drawInnerBall(canvas, pos0, pos1, genBallPercent)
        // 画运动小球外径
        ballPaint.color = esConfig.p33Blue06b389Color
        drawOuterBall(canvas, pos0, pos1, genBallPercent)

        // 画圆
        // 先画个白色填充的圆盖住小球
        canvas.drawCircle(
            centerX - (mPathHorizontalPadding / 2 + mArcRadius + mHorizontalDistance + mCircleRadius),
            centerY,
            mCircleRadius,
            fillCirclePaint
        )
    }

    /**
     * 逆变器 - 电网负载
     *
     * @param canvas
     */
    private fun drawInverterToGridLoad(canvas: Canvas) {
        if (!showGridLoadToInverterPath) return
        // 离线
        val offline = isStationOffline || isInverterToGridLoadOffline
        if (offline) {
            endBallAnimation(EsAnimationDirection.INVERTER_TO_GRID_LOAD)
            canvas.drawPath(inverterToGridLoadPath, offlinePaint)
            return
        }
        // 画路径
        pathPaint.color = esConfig.orangeF9AD57Color
        canvas.drawPath(inverterToGridLoadPath, pathPaint)
        // 动画未开始，不画小球
        if (!isAnimating(EsAnimationDirection.INVERTER_TO_GRID_LOAD)) {
            return
        }

        val pos0 = inverterToGridLoadPos[0]
        val pos1 = inverterToGridLoadPos[1]
        // 画运动小球内径
        ballPaint.color = esConfig.orangeF9AD57Color
        drawInnerBall(canvas, pos0, pos1, inverterToGridLoadBallPercent)
        // 画运动小球外径
        ballPaint.color = esConfig.p33OrangeF9AD57Color
        drawOuterBall(canvas, pos0, pos1, inverterToGridLoadBallPercent)

    }

    /**
     * 电网 - 电网负载
     *
     * @param canvas
     */
    private fun drawGridToGridLoad(canvas: Canvas) {
        if (!showGridToGridLoadPath) return
        // 离线
        val offline = isStationOffline || isGridToGridLoadOffline
        if (offline) {
            endBallAnimation(EsAnimationDirection.GRID_TO_GRID_LOAD)
            canvas.drawPath(gridToGridLoadPath, offlinePaint)
            return
        }
        // 画路径
        pathPaint.color = esConfig.orangeF9AD57Color
        canvas.drawPath(gridToGridLoadPath, pathPaint)
        // 动画未开始，不画小球
        if (!isAnimating(EsAnimationDirection.GRID_TO_GRID_LOAD)) {
            return
        }

        val pos0 = gridToGridLoadPos[0]
        val pos1 = gridToGridLoadPos[1]
        // 画运动小球内径
        ballPaint.color = esConfig.orangeF9AD57Color
        drawInnerBall(canvas, pos0, pos1, gridToGridLoadBallPercent)
        // 画运动小球外径
        ballPaint.color = esConfig.p33OrangeF9AD57Color
        drawOuterBall(canvas, pos0, pos1, gridToGridLoadBallPercent)


        // 画圆
        // 先画个白色填充的圆盖住小球
//        canvas.drawCircle(
//            centerX + (mPathHorizontalPadding / 2 + mArcRadius + mHorizontalDistance + mCircleRadius),
//            centerY - (mVerticalDistance + mArcRadius),
//            mCircleRadius,
//            fillCirclePaint
//        )
//        // 再画个圆环
//        strokeCirclePaint.color = getColorById(context, R.color.red_4d_f56d66_color)
//        canvas.drawCircle(
//            centerX + (mPathHorizontalPadding / 2 + mArcRadius + mHorizontalDistance + mCircleRadius),
//            centerY - (mVerticalDistance + mArcRadius),
//            mCircleRadius,
//            strokeCirclePaint
//        )
//        // 画进度
//        progressPaint.color = getColorById(context, R.color.red_f56d66_color)
//        canvas.drawArc(
//            centerX + (mPathHorizontalPadding / 2 + mArcRadius + mHorizontalDistance + mCircleRadius) - mCircleRadius,
//            centerY - (mVerticalDistance + mArcRadius) - mCircleRadius,
//            centerX + (mPathHorizontalPadding / 2 + mArcRadius + mHorizontalDistance + mCircleRadius) + mCircleRadius,
//            centerY - (mVerticalDistance + mArcRadius) + mCircleRadius,
//            -90f,
//            90f,
//            false,
//            progressPaint
//        )
    }

    /**
     * 电网 - 逆变器
     *
     * @param centerX
     * @param centerY
     * @param canvas
     */
    private fun drawGridToInverter(centerX: Float, centerY: Float, canvas: Canvas) {
        if (!showGridToInverterPath) return
        // 离线
        val offline = isStationOffline || isGridToInverterOffline
        if (offline) {
            endBallAnimations(EsAnimationDirection.INVERTER_TO_GRID, EsAnimationDirection.GRID_TO_INVERTER)
            canvas.drawPath(gridInverterPath, offlinePaint)
            return
        }
        // 画路径
        pathPaint.color = if (isTodayExported) esConfig.blueColor else esConfig.redColor
        canvas.drawPath(gridInverterPath, pathPaint)
        // 动画未开始，不画小球
        if (!isAnimating(EsAnimationDirection.INVERTER_TO_GRID) && !isAnimating(EsAnimationDirection.GRID_TO_INVERTER)) {
            return
        }
        // 初始小球圆心x轴位置
        val pos0 = gridInverterPos[0]
        val pos1 = gridInverterPos[1]
        // 画运动小球内径（卖电 blueColor 买电 redColor）
        ballPaint.color = if (isTodayExported) esConfig.blueColor else esConfig.redColor
        drawInnerBall(canvas, pos0, pos1, gridToInverterBallPercent)
        // 画运动小球外径（卖电 blueColor 买电 redColor）
        ballPaint.color = if (isTodayExported) esConfig.p33BlueColor else esConfig.p33RedColor
        drawOuterBall(canvas, pos0, pos1, gridToInverterBallPercent)


        // 画圆
        // 先画个白色填充的圆盖住小球
        canvas.drawCircle(
            centerX + (mPathHorizontalPadding / 2 + mArcRadius + mHorizontalDistance + mCircleRadius),
            centerY - (mVerticalDistance + mArcRadius),
            mCircleRadius,
            fillCirclePaint
        )
//        // 再画个圆环
//        strokeCirclePaint.color = getColorById(context, R.color.red_4d_f56d66_color)
//        canvas.drawCircle(
//            centerX + (mPathHorizontalPadding / 2 + mArcRadius + mHorizontalDistance + mCircleRadius),
//            centerY - (mVerticalDistance + mArcRadius),
//            mCircleRadius,
//            strokeCirclePaint
//        )
//        // 画进度
//        progressPaint.color = getColorById(context, R.color.red_f56d66_color)
//        canvas.drawArc(
//            centerX + (mPathHorizontalPadding / 2 + mArcRadius + mHorizontalDistance + mCircleRadius) - mCircleRadius,
//            centerY - (mVerticalDistance + mArcRadius) - mCircleRadius,
//            centerX + (mPathHorizontalPadding / 2 + mArcRadius + mHorizontalDistance + mCircleRadius) + mCircleRadius,
//            centerY - (mVerticalDistance + mArcRadius) + mCircleRadius,
//            -90f,
//            90f,
//            false,
//            progressPaint
//        )
    }

    /**
     * Draw battery inverter
     * 电池 - 逆变器
     * @param centerX
     * @param centerY
     * @param canvas
     */
    private fun drawBatteryInverter(centerX: Float, centerY: Float, canvas: Canvas) {
        if (!showBatteryPath) return
        // 离线
        val offline = isStationOffline || isBatteryOffline
        if (offline) {
            endBallAnimations(
                EsAnimationDirection.INVERTER_TO_BATTERY,
                EsAnimationDirection.BATTERY_TO_INVERTER
            )
            canvas.drawPath(batteryInverterPath, offlinePaint)
            return
        }
        // 画路径
        pathPaint.color = if (isTodayCharge) esConfig.greenColor else esConfig.purpleColor
        canvas.drawPath(batteryInverterPath, pathPaint)
        // 动画未开始，不画小球
        if (!isAnimating(EsAnimationDirection.INVERTER_TO_BATTERY) && !isAnimating(EsAnimationDirection.BATTERY_TO_INVERTER)) {
            return
        }
        val pos0 = batteryInverterPos[0]
        val pos1 = batteryInverterPos[1]
        // 画运动小球内径
        ballPaint.color = if (isTodayCharge) esConfig.greenColor else esConfig.purpleColor
        drawInnerBall(canvas, pos0, pos1, batteryBallPercent)
        // 画运动小球外径
        ballPaint.color = if (isTodayCharge) esConfig.p33GreenColor else esConfig.p33PurpleColor
        drawOuterBall(canvas, pos0, pos1, batteryBallPercent)


        // 画圆
        // 先画个白色填充的圆盖住小球
        canvas.drawCircle(
            centerX - (mPathHorizontalPadding / 2 + mArcRadius + mHorizontalDistance + mCircleRadius),
            centerY + (mVerticalDistance + mArcRadius),
            mCircleRadius,
            fillCirclePaint
        )
//        // 再画个圆环
//        strokeCirclePaint.color = getColorById(context, R.color.green_33_aed681_color)
//        canvas.drawCircle(
//            centerX - (mPathHorizontalPadding / 2 + mArcRadius + mHorizontalDistance + mCircleRadius),
//            centerY + (mVerticalDistance + mArcRadius),
//            mCircleRadius,
//            strokeCirclePaint
//        )
//        // 画进度
//        progressPaint.color = getColorById(context, R.color.green_aed681_color)
//        canvas.drawArc(
//            centerX - (mPathHorizontalPadding / 2 + mArcRadius + mHorizontalDistance + mCircleRadius) - mCircleRadius,
//            centerY + (mVerticalDistance + mArcRadius) - mCircleRadius,
//            centerX - (mPathHorizontalPadding / 2 + mArcRadius + mHorizontalDistance + mCircleRadius) + mCircleRadius,
//            centerY + (mVerticalDistance + mArcRadius) + mCircleRadius,
//            -90f,
//            90f,
//            false,
//            progressPaint
//        )
    }

    /**
     * Draw backup load inverter
     * 逆变器 - backup负载
     * @param centerX
     * @param centerY
     * @param canvas
     */
    private fun drawBackupLoadInverter(centerX: Float, centerY: Float, canvas: Canvas) {
        if (!showBackupLoadPath) return
        // 离线
        val offline = isStationOffline || isBackupOffline
        if (offline) {
            endBallAnimations(EsAnimationDirection.INVERTER_TO_BACKUP_LOAD)
            canvas.drawPath(inverterToBackupPath, offlinePaint)
            return
        }
        // 画路径
        pathPaint.color = esConfig.orangeColor
        canvas.drawPath(inverterToBackupPath, pathPaint)
        // 动画未开始，不画小球
        if (!isAnimating(EsAnimationDirection.INVERTER_TO_BACKUP_LOAD)) {
            return
        }
        val pos0 = inverterToBackupPos[0]
        val pos1 = inverterToBackupPos[1]
        // 画运动小球内径
        ballPaint.color = esConfig.orangeColor
        drawInnerBall(canvas, pos0, pos1, backupBallPercent)
        // 画运动小球外径
        ballPaint.color = esConfig.p33OrangeColor
        drawOuterBall(canvas, pos0, pos1, backupBallPercent)


        // 画圆
        // 先画个白色填充的圆盖住小球
        canvas.drawCircle(
            centerX + (mPathHorizontalPadding / 2 + mArcRadius + mHorizontalDistance + mCircleRadius),
            centerY + (mVerticalDistance + mArcRadius),
            mCircleRadius,
            fillCirclePaint
        )
//        // 再画个圆环
//        strokeCirclePaint.color = getColorById(context, R.color.orange_4d_fda23a_color)
//        canvas.drawCircle(
//            centerX + (mPathHorizontalPadding / 2 + mArcRadius + mHorizontalDistance + mCircleRadius),
//            centerY + (mVerticalDistance + mArcRadius),
//            mCircleRadius,
//            strokeCirclePaint
//        )
//        // 画进度
//        progressPaint.color = getColorById(context, R.color.orange_fda23a_color)
//        canvas.drawArc(
//            centerX + (mPathHorizontalPadding / 2 + mArcRadius + mHorizontalDistance + mCircleRadius) - mCircleRadius,
//            centerY + (mVerticalDistance + mArcRadius) - mCircleRadius,
//            centerX + (mPathHorizontalPadding / 2 + mArcRadius + mHorizontalDistance + mCircleRadius) + mCircleRadius,
//            centerY + (mVerticalDistance + mArcRadius) + mCircleRadius,
//            -90f,
//            90f,
//            false,
//            progressPaint
//        )
    }

    private fun getAnimatorPath(direction: EsAnimationDirection): Path? = when (direction) {
        EsAnimationDirection.PV_TO_INVERTER -> pvInverterPath
        EsAnimationDirection.GRID_TO_INVERTER, EsAnimationDirection.INVERTER_TO_GRID -> gridInverterPath
        EsAnimationDirection.BATTERY_TO_INVERTER, EsAnimationDirection.INVERTER_TO_BATTERY -> batteryInverterPath
        EsAnimationDirection.INVERTER_TO_BACKUP_LOAD -> inverterToBackupPath
        EsAnimationDirection.GRID_TO_GRID_LOAD -> gridToGridLoadPath
        EsAnimationDirection.INVERTER_TO_GRID_LOAD -> inverterToGridLoadPath
        EsAnimationDirection.GEN_TO_INVERTER -> genToInverterPath
        else -> null
    }

    private fun getAnimatorPosArray(direction: EsAnimationDirection): FloatArray? =
        when (direction) {
            EsAnimationDirection.PV_TO_INVERTER -> pvInverterPos
            EsAnimationDirection.GRID_TO_INVERTER, EsAnimationDirection.INVERTER_TO_GRID -> gridInverterPos
            EsAnimationDirection.BATTERY_TO_INVERTER, EsAnimationDirection.INVERTER_TO_BATTERY -> batteryInverterPos
            EsAnimationDirection.INVERTER_TO_BACKUP_LOAD -> inverterToBackupPos
            EsAnimationDirection.GRID_TO_GRID_LOAD -> gridToGridLoadPos
            EsAnimationDirection.INVERTER_TO_GRID_LOAD -> inverterToGridLoadPos
            EsAnimationDirection.GEN_TO_INVERTER -> genToInverterPos
            else -> null
        }

    /**
     * Get animator by direction from map
     * 从集合里取出动画，如果没有，返回null
     * @param EsAnimationDirection
     * @return
     */
    private fun getAnimatorByDirectionFromMap(EsAnimationDirection: EsAnimationDirection): ValueAnimator? {
        if (animatorArray.isEmpty()) return null
        return animatorArray.get(EsAnimationDirection.ordinal)
    }

    private fun getAnimatorByDirectionIfNullCreateOne(EsAnimationDirection: EsAnimationDirection): ValueAnimator? {
        val animator = getAnimatorByDirectionFromMap(EsAnimationDirection)
        val startFloat = 0f
        val animatorPath = getAnimatorPath(EsAnimationDirection) ?: return null
        val pathMeasure = PathMeasure(animatorPath, false)
        val length = pathMeasure.length
        val animatorPosArray = getAnimatorPosArray(EsAnimationDirection)
        if (length == 0f) return null
        // 还没有保存过的动画
        if (animator == null) {
            val newAnimator = ValueAnimator.ofFloat(0f, length).animatorConfig()
            newAnimator.addUpdateListener { animation: ValueAnimator ->
                val animatedValue = animation.animatedValue as Float
                pathMeasure.getPosTan(animatedValue, animatorPosArray, null)
                invalidate()
            }
            animatorArray.put(EsAnimationDirection.ordinal, newAnimator)
            return newAnimator
        }
        // 动画已经初始化过了，更新下动画起点和终点
        animator.setFloatValues(startFloat, pathMeasure.length)
        return animator
    }

    fun startBallAnimation(EsAnimationDirection: EsAnimationDirection) {
        startBallAnimations(EsAnimationDirection)
    }

    fun startBallAnimations(vararg EsAnimationDirections: EsAnimationDirection) {
        startBallAnimations(EsAnimationDirections.toList())
    }

    fun startBallAnimations(EsAnimationDirections: List<EsAnimationDirection>) {
        if (EsAnimationDirections.isEmpty()) return
        if (isStationOffline) return
        post {
            EsAnimationDirections.forEach {
                val animatorByDirection =
                    getAnimatorByDirectionIfNullCreateOne(it) ?: return@forEach
                // 如果相反方向的动画存在并且在运行的话，停止他
                handleAntiDirectionAnimationIfExist(it)
                saveRunningAnimator(it, animatorByDirection)
                if (animatorByDirection.isStarted) {
                    return@forEach
                }
                val needReverse = reversedDirectionSet.contains(it)
                if (needReverse) {
                    animatorByDirection.reverse()
                    return@forEach
                }
                animatorByDirection.start()
            }
        }
    }

    /**
     * 停止相反方向的动画
     *
     * @param esAnimationDirection
     */
    private fun handleAntiDirectionAnimationIfExist(esAnimationDirection: EsAnimationDirection) {
        // 这几个模块都是单向的，不会有对立方向
        if (esAnimationDirection == EsAnimationDirection.PV_TO_INVERTER ||
            esAnimationDirection == EsAnimationDirection.INVERTER_TO_BACKUP_LOAD ||
            esAnimationDirection == EsAnimationDirection.INVERTER_TO_GRID_LOAD ||
            esAnimationDirection == EsAnimationDirection.GRID_TO_GRID_LOAD ||
            esAnimationDirection == EsAnimationDirection.GEN_TO_INVERTER
        ) {
            return
        }
        when (esAnimationDirection) {
            EsAnimationDirection.BATTERY_TO_INVERTER -> endAntiDirectionAnimationIfRunning(
                EsAnimationDirection.INVERTER_TO_BATTERY
            )
            EsAnimationDirection.INVERTER_TO_BATTERY -> endAntiDirectionAnimationIfRunning(
                EsAnimationDirection.BATTERY_TO_INVERTER
            )
            EsAnimationDirection.GRID_TO_INVERTER -> endAntiDirectionAnimationIfRunning(
                EsAnimationDirection.INVERTER_TO_GRID
            )
            EsAnimationDirection.INVERTER_TO_GRID -> endAntiDirectionAnimationIfRunning(
                EsAnimationDirection.GRID_TO_INVERTER
            )
            else -> Unit
        }
    }

    /**
     * 停止相反方向的动画（如果相反方向有动画的话）
     */
    private fun endAntiDirectionAnimationIfRunning(antiDirection: EsAnimationDirection) {
        // 还没初始化就不处理了
        val antiDirectionAnimation =
            getAnimatorByDirectionFromMap(antiDirection) ?: return
        if (antiDirectionAnimation.isStarted) {
            endBallAnimation(antiDirection)
        }
    }

    /**
     * End ball animation
     * 取消某个小球动画
     * @param EsAnimationDirection
     */
    fun endBallAnimation(EsAnimationDirection: EsAnimationDirection) {
        if (runningAnimatorArray.isEmpty()) {
            return
        }
        val animator = runningAnimatorArray.get(EsAnimationDirection.ordinal) ?: return
        runningAnimatorArray.remove(EsAnimationDirection.ordinal)
        post {
            animator.end()
        }
    }

    fun endBallAnimations(vararg EsAnimationDirections: EsAnimationDirection) {
        endBallAnimations(EsAnimationDirections.toList())
    }

    /**
     * End ball animations
     * 取消某些小球动画
     * @param EsAnimationDirections
     */
    fun endBallAnimations(EsAnimationDirections: List<EsAnimationDirection>) {
        if (EsAnimationDirections.isEmpty() || runningAnimatorArray.isEmpty()) return
        // 停止动画并从正在进行的动画集合中移除
        EsAnimationDirections.forEach {
            Log.d(
                TAG,
                "startArrowAnimation: EsAnimationDirection name: ${it.name} + ordinal: ${it.ordinal}"
            )
            val animator = runningAnimatorArray.get(it.ordinal) ?: return@forEach
            runningAnimatorArray.removeAt(runningAnimatorArray.indexOfValue(animator))
            post {
                animator.end()
            }
        }
    }


    /**
     * End all ball animations
     * 取消所有小球动画
     */
    fun endAllBallAnimations() {
        if (runningAnimatorArray.isEmpty()) return
        runningAnimatorArray.forEach { _, anim ->
            anim.cancel()
        }
        runningAnimatorArray.clear()
    }

    private fun saveRunningAnimator(
        EsAnimationDirection: EsAnimationDirection,
        objectAnimator: ValueAnimator
    ) {
        if (runningAnimatorArray.isEmpty()) {
            runningAnimatorArray.put(EsAnimationDirection.ordinal, objectAnimator)
            return
        }
        val animator = runningAnimatorArray.get(EsAnimationDirection.ordinal)
        if (animator == null) {
            runningAnimatorArray.put(EsAnimationDirection.ordinal, objectAnimator)
        }
    }

    /**
     * 动画基础配置
     *
     * @return
     */
    private fun ValueAnimator.animatorConfig(): ValueAnimator {
        interpolator = LinearInterpolator()
        repeatCount = ValueAnimator.INFINITE
        startDelay = mAnimationDelay
        duration = mAnimationDuration
        return this
    }

    /**
     * On measure
     * 本控件为项目特用控件，不受使用者宽高限制，控件写死
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // 左右各增加一个圆画笔的宽度以便不挡住线
        val width =
            (mCircleRadius * 2 + mHorizontalDistance + mArcRadius) * 2 + mPathHorizontalPadding
        val height = (mCircleRadius + mVerticalDistance + mArcRadius) * 2
        setMeasuredDimension(
            (width + mExtraWidth).toInt(),
            (height + mExtraWidth).toInt()
        )
    }

}