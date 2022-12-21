package com.example.viewapplication.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.util.AttributeSet
import android.util.Log
import android.util.SparseArray
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.util.forEach
import androidx.core.util.isEmpty
import com.example.viewapplication.R
import com.example.viewapplication.config.EsConfiguration
import com.example.viewapplication.enumeration.EsAnimationDirection
import com.example.viewapplication.getColorById

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
    private val mRoundRadius = esConfig.roundRadius
    private val mPathHorizontalPadding = esConfig.pathHorizontalPadding
    private val mExtraWidth = esConfig.extraWidth
    private val mBallInnerRadius = esConfig.ballInnerRadius
    private val mBallOuterRadius = esConfig.ballOuterRadius
    private val mCircleRadius = esConfig.circleRadius

    private lateinit var strokeCirclePaint: Paint
    private lateinit var fillCirclePaint: Paint
    private lateinit var progressPaint: Paint
    private lateinit var ballPaint: Paint
    private lateinit var pathPaint: Paint

    private val mAnimationDuration = esConfig.animationDuration
    private val mAnimationDelay = esConfig.animationDelay


    /* -------------- PV - 逆变器 ----------------*/
    private val pvInverterPath by lazy(::Path)
    private val pvInverterPos = floatArrayOf(0f, 0f)

    /* -------------- 电网 - 逆变器 ----------------*/
    private val gridInverterPath by lazy(::Path)
    private val gridInverterPos = floatArrayOf(0f, 0f)

    /* -------------- 电池 - 逆变器 ----------------*/
    private val batteryInverterPath by lazy(::Path)
    private val batteryInverterPos = floatArrayOf(0f, 0f)

    /* -------------- 逆变器 - backup负载 ----------------*/
    private val inverterToBackupPath by lazy(::Path)
    private val inverterToBackupPos = floatArrayOf(0f, 0f)

    /* -------------- 电网 - 电网负载 ----------------*/
    private val gridToGridLoadPath by lazy(::Path)
    private val gridToGridLoadPos = floatArrayOf(0f, 0f)

    /* -------------- 逆变器 - 电网负载 ----------------*/
    private val inverterToGridLoadPath by lazy(::Path)
    private val inverterToGridLoadPos = floatArrayOf(0f, 0f)

    /**
     * 电站是否离线
     */
    var isStationOffline = false
        set(value) {
            field = value
            invalidate()
        }

    /**
     * 储存动画，key - ArrowDirection的序号  value - 动画对象
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
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        initPvToInverterPath()
        initGridToInverterPath()
        initBatteryToInverterPath()
        initBackupLoadToInverterPath()
        initGridToGridLoadPath()
    }

    /**
     * 初始化 电网 - 电网负载 小球运动路径
     *
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
            centerX + (mPathHorizontalPadding / 2 + mArcRadius + mHorizontalDistance) + mMaxBallRadius
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
            (mVerticalDistance - mArcRadius) / 2
        )
        gridToGridLoadPath.lineTo(
            gridToGridLoadPos[0],
            (mVerticalDistance - mArcRadius) / 2
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
            centerX - (mPathHorizontalPadding / 2 + mArcRadius + mHorizontalDistance) - mMaxBallRadius
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
            centerX + (mPathHorizontalPadding / 2 + mArcRadius + mHorizontalDistance) + mMaxBallRadius
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
            centerX - (mPathHorizontalPadding / 2 + mArcRadius + mHorizontalDistance) - mMaxBallRadius
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
            centerX + (mPathHorizontalPadding / 2 + mArcRadius + mHorizontalDistance) + mMaxBallRadius
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

        // 画 电网 - 电网负载 不分
        drawGridGridLoad(centerX, centerY, canvas)

        // 画 电网 - 逆变器 部分
        drawGridInverter(centerX, centerY, canvas)

        // 画 电池 - 逆变器 部分
        drawBatteryInverter(centerX, centerY, canvas)

        // 画 backup负载 - 逆变器 部分
        drawBackupLoadInverter(centerX, centerY, canvas)

        // 画中心逆变器
        drawCenterInverter(centerX, centerY, canvas)
    }

    private fun drawCenterInverter(centerX: Float, centerY: Float, canvas: Canvas) {
        canvas.drawCircle(
            centerX,
            centerY,
            mCircleRadius,
            fillCirclePaint
        )
        strokeCirclePaint.color = getColorById(context, R.color.orange_fda23a_color)
        canvas.drawCircle(
            centerX,
            centerY,
            mCircleRadius,
            strokeCirclePaint
        )
    }

    private fun drawPvInverter(centerX: Float, centerY: Float, canvas: Canvas) {
        // 初始小球圆心x轴位置
//        val ballStartX = centerX - (mPathHorizontalPadding / 2 + mArcRadius + mHorizontalDistance)
        val pos0 = pvInverterPos[0]
        val pos1 = pvInverterPos[1]
//        val ballCenterX = maxOf(pos0, ballStartX)
        ballPaint.color = getColorById(context, R.color.yellow_f0cf00_color)
        canvas.drawCircle(
            pos0,
            pos1,
            mBallInnerRadius,
            ballPaint
        )
        // 画运动小球外径
        ballPaint.color = getColorById(context, R.color.yellow_33_f0cf00_color)
        canvas.drawCircle(
            pos0,
            pos1,
            mBallOuterRadius,
            ballPaint
        )

        // 画路径
        pathPaint.color = getColorById(context, R.color.yellow_f0cf00_color)
        canvas.drawPath(pvInverterPath, pathPaint)

        // 画圆
        // 先画个白色填充的圆盖住小球
        canvas.drawCircle(
            centerX - (mPathHorizontalPadding / 2 + mArcRadius + mHorizontalDistance + mCircleRadius),
            centerY - (mVerticalDistance + mArcRadius),
            mCircleRadius,
            fillCirclePaint
        )
        // 再画个圆环
        strokeCirclePaint.color = getColorById(context, R.color.yellow_4d_f0cf00_color)
        canvas.drawCircle(
            centerX - (mPathHorizontalPadding / 2 + mArcRadius + mHorizontalDistance + mCircleRadius),
            centerY - (mVerticalDistance + mArcRadius),
            mCircleRadius,
            strokeCirclePaint
        )
        // 画进度
        progressPaint.color = getColorById(context, R.color.yellow_f0cf00_color)
        canvas.drawArc(
            centerX - (mPathHorizontalPadding / 2 + mArcRadius + mHorizontalDistance + mCircleRadius) - mCircleRadius,
            centerY - (mVerticalDistance + mArcRadius) - mCircleRadius,
            centerX - (mPathHorizontalPadding / 2 + mArcRadius + mHorizontalDistance + mCircleRadius) + mCircleRadius,
            centerY - (mVerticalDistance + mArcRadius) + mCircleRadius,
            -90f,
            90f,
            false,
            progressPaint
        )
    }

    private fun drawGridGridLoad(centerX: Float, centerY: Float, canvas: Canvas) {
        val pos0 = gridToGridLoadPos[0]
        val pos1 = gridToGridLoadPos[1]
//        val ballCenterX = minOf(pos0,ballStartX)
        // 画运动小球内径
        ballPaint.color = getColorById(context, R.color.orange_fda23a_color)
        canvas.drawCircle(
            pos0,
            pos1,
            mBallInnerRadius,
            ballPaint
        )
        // 画运动小球外径
        ballPaint.color = getColorById(context, R.color.orange_33_fda23a_color)
        canvas.drawCircle(
            pos0,
            pos1,
            mBallOuterRadius,
            ballPaint
        )
        // 画路径
        pathPaint.color = getColorById(context, R.color.orange_fda23a_color)
        canvas.drawPath(gridToGridLoadPath, pathPaint)

        // 画圆
        // 先画个白色填充的圆盖住小球
        canvas.drawCircle(
            centerX + (mPathHorizontalPadding / 2 + mArcRadius + mHorizontalDistance + mCircleRadius),
            centerY - (mVerticalDistance + mArcRadius),
            mCircleRadius,
            fillCirclePaint
        )
        // 再画个圆环
        strokeCirclePaint.color = getColorById(context, R.color.red_4d_f56d66_color)
        canvas.drawCircle(
            centerX + (mPathHorizontalPadding / 2 + mArcRadius + mHorizontalDistance + mCircleRadius),
            centerY - (mVerticalDistance + mArcRadius),
            mCircleRadius,
            strokeCirclePaint
        )
        // 画进度
        progressPaint.color = getColorById(context, R.color.red_f56d66_color)
        canvas.drawArc(
            centerX + (mPathHorizontalPadding / 2 + mArcRadius + mHorizontalDistance + mCircleRadius) - mCircleRadius,
            centerY - (mVerticalDistance + mArcRadius) - mCircleRadius,
            centerX + (mPathHorizontalPadding / 2 + mArcRadius + mHorizontalDistance + mCircleRadius) + mCircleRadius,
            centerY - (mVerticalDistance + mArcRadius) + mCircleRadius,
            -90f,
            90f,
            false,
            progressPaint
        )
    }

    private fun drawGridInverter(centerX: Float, centerY: Float, canvas: Canvas) {
        // 初始小球圆心x轴位置
//        val ballStartX = centerX + (mPathHorizontalPadding / 2 + mArcRadius + mHorizontalDistance)
        val pos0 = gridInverterPos[0]
        val pos1 = gridInverterPos[1]
//        val ballCenterX = minOf(pos0,ballStartX)
        // 画运动小球内径
        ballPaint.color = getColorById(context, R.color.red_f56d66_color)
        canvas.drawCircle(
            pos0,
            pos1,
            mBallInnerRadius,
            ballPaint
        )
        // 画运动小球外径
        ballPaint.color = getColorById(context, R.color.red_33_f56d66_color)
        canvas.drawCircle(
            pos0,
            pos1,
            mBallOuterRadius,
            ballPaint
        )
        // 画路径
        pathPaint.color = getColorById(context, R.color.red_f56d66_color)
        canvas.drawPath(gridInverterPath, pathPaint)

        // 画圆
        // 先画个白色填充的圆盖住小球
        canvas.drawCircle(
            centerX + (mPathHorizontalPadding / 2 + mArcRadius + mHorizontalDistance + mCircleRadius),
            centerY - (mVerticalDistance + mArcRadius),
            mCircleRadius,
            fillCirclePaint
        )
        // 再画个圆环
        strokeCirclePaint.color = getColorById(context, R.color.red_4d_f56d66_color)
        canvas.drawCircle(
            centerX + (mPathHorizontalPadding / 2 + mArcRadius + mHorizontalDistance + mCircleRadius),
            centerY - (mVerticalDistance + mArcRadius),
            mCircleRadius,
            strokeCirclePaint
        )
        // 画进度
        progressPaint.color = getColorById(context, R.color.red_f56d66_color)
        canvas.drawArc(
            centerX + (mPathHorizontalPadding / 2 + mArcRadius + mHorizontalDistance + mCircleRadius) - mCircleRadius,
            centerY - (mVerticalDistance + mArcRadius) - mCircleRadius,
            centerX + (mPathHorizontalPadding / 2 + mArcRadius + mHorizontalDistance + mCircleRadius) + mCircleRadius,
            centerY - (mVerticalDistance + mArcRadius) + mCircleRadius,
            -90f,
            90f,
            false,
            progressPaint
        )
    }

    private fun drawBatteryInverter(centerX: Float, centerY: Float, canvas: Canvas) {
        // 初始小球圆心x轴位置
//        val ballStartX = centerX - (mPathHorizontalPadding / 2 + mArcRadius + mHorizontalDistance)
        val pos0 = batteryInverterPos[0]
        val pos1 = batteryInverterPos[1]
//        val ballCenterX = maxOf(pos0,ballStartX)
        // 画运动小球内径
        ballPaint.color = getColorById(context, R.color.green_aed681_color)
        canvas.drawCircle(
            pos0,
            pos1,
            mBallInnerRadius,
            ballPaint
        )
        // 画运动小球外径
        ballPaint.color = getColorById(context, R.color.green_33_aed681_color)
        canvas.drawCircle(
            pos0,
            pos1,
            mBallOuterRadius,
            ballPaint
        )
        // 画路径
        pathPaint.color = getColorById(context, R.color.green_aed681_color)
        canvas.drawPath(batteryInverterPath, pathPaint)

        // 画圆
        // 先画个白色填充的圆盖住小球
        canvas.drawCircle(
            centerX - (mPathHorizontalPadding / 2 + mArcRadius + mHorizontalDistance + mCircleRadius),
            centerY + (mVerticalDistance + mArcRadius),
            mCircleRadius,
            fillCirclePaint
        )
        // 再画个圆环
        strokeCirclePaint.color = getColorById(context, R.color.green_33_aed681_color)
        canvas.drawCircle(
            centerX - (mPathHorizontalPadding / 2 + mArcRadius + mHorizontalDistance + mCircleRadius),
            centerY + (mVerticalDistance + mArcRadius),
            mCircleRadius,
            strokeCirclePaint
        )
        // 画进度
        progressPaint.color = getColorById(context, R.color.green_aed681_color)
        canvas.drawArc(
            centerX - (mPathHorizontalPadding / 2 + mArcRadius + mHorizontalDistance + mCircleRadius) - mCircleRadius,
            centerY + (mVerticalDistance + mArcRadius) - mCircleRadius,
            centerX - (mPathHorizontalPadding / 2 + mArcRadius + mHorizontalDistance + mCircleRadius) + mCircleRadius,
            centerY + (mVerticalDistance + mArcRadius) + mCircleRadius,
            -90f,
            90f,
            false,
            progressPaint
        )
    }

    private fun drawBackupLoadInverter(centerX: Float, centerY: Float, canvas: Canvas) {
        // 初始小球圆心x轴位置
//        val ballStartX = centerX + (mPathHorizontalPadding / 2 + mArcRadius + mHorizontalDistance)
        val pos0 = inverterToBackupPos[0]
        val pos1 = inverterToBackupPos[1]
//        val ballCenterX = minOf(pos0,ballStartX)
        // 画运动小球内径
        ballPaint.color = getColorById(context, R.color.orange_fda23a_color)
        canvas.drawCircle(
            pos0,
            pos1,
            mBallInnerRadius,
            ballPaint
        )
        // 画运动小球外径
        ballPaint.color = getColorById(context, R.color.orange_33_fda23a_color)
        canvas.drawCircle(
            pos0,
            pos1,
            mBallOuterRadius,
            ballPaint
        )
        // 画路径
        pathPaint.color = getColorById(context, R.color.orange_fda23a_color)
        canvas.drawPath(inverterToBackupPath, pathPaint)

        // 画圆
        // 先画个白色填充的圆盖住小球
        canvas.drawCircle(
            centerX + (mPathHorizontalPadding / 2 + mArcRadius + mHorizontalDistance + mCircleRadius),
            centerY + (mVerticalDistance + mArcRadius),
            mCircleRadius,
            fillCirclePaint
        )
        // 再画个圆环
        strokeCirclePaint.color = getColorById(context, R.color.orange_4d_fda23a_color)
        canvas.drawCircle(
            centerX + (mPathHorizontalPadding / 2 + mArcRadius + mHorizontalDistance + mCircleRadius),
            centerY + (mVerticalDistance + mArcRadius),
            mCircleRadius,
            strokeCirclePaint
        )
        // 画进度
        progressPaint.color = getColorById(context, R.color.orange_fda23a_color)
        canvas.drawArc(
            centerX + (mPathHorizontalPadding / 2 + mArcRadius + mHorizontalDistance + mCircleRadius) - mCircleRadius,
            centerY + (mVerticalDistance + mArcRadius) - mCircleRadius,
            centerX + (mPathHorizontalPadding / 2 + mArcRadius + mHorizontalDistance + mCircleRadius) + mCircleRadius,
            centerY + (mVerticalDistance + mArcRadius) + mCircleRadius,
            -90f,
            90f,
            false,
            progressPaint
        )
    }

    private fun getAnimatorPath(direction: EsAnimationDirection): Path? = when (direction) {
        EsAnimationDirection.PV_TO_INVERTER -> pvInverterPath
        EsAnimationDirection.GRID_TO_INVERTER, EsAnimationDirection.INVERTER_TO_GRID -> gridInverterPath
        EsAnimationDirection.BATTERY_TO_INVERTER, EsAnimationDirection.INVERTER_TO_BATTERY -> batteryInverterPath
        EsAnimationDirection.INVERTER_TO_BACKUP_LOAD -> inverterToBackupPath
        else -> null
    }

    private fun getAnimatorPosArray(direction: EsAnimationDirection): FloatArray? =
        when (direction) {
            EsAnimationDirection.PV_TO_INVERTER -> pvInverterPos
            EsAnimationDirection.GRID_TO_INVERTER, EsAnimationDirection.INVERTER_TO_GRID -> gridInverterPos
            EsAnimationDirection.BATTERY_TO_INVERTER, EsAnimationDirection.INVERTER_TO_BATTERY -> batteryInverterPos
            EsAnimationDirection.INVERTER_TO_BACKUP_LOAD -> inverterToBackupPos
            else -> null
        }

    /**
     * Get animator by direction from map
     * 从集合里取出动画，如果没有，返回null
     * @param esAnimationDirection
     * @return
     */
    private fun getAnimatorByDirectionFromMap(esAnimationDirection: EsAnimationDirection): ValueAnimator? {
        if (animatorArray.isEmpty()) return null
        return animatorArray.get(esAnimationDirection.ordinal)
    }

    private fun getAnimatorByDirectionIfNullCreateOne(esAnimationDirection: EsAnimationDirection): ValueAnimator? {
        val animator = getAnimatorByDirectionFromMap(esAnimationDirection)
        val startFloat = 0f
        val animatorPath = getAnimatorPath(esAnimationDirection) ?: return null
        val pathMeasure = PathMeasure(animatorPath, false)
        val length = pathMeasure.length
        val animatorPosArray = getAnimatorPosArray(esAnimationDirection)
        if (length == 0f) return null
        // 还没有保存过的动画
        if (animator == null) {
            val newAnimator = ValueAnimator.ofFloat(0f, length).animatorConfig()
            newAnimator.addUpdateListener { animation: ValueAnimator ->
                val animatedValue = animation.animatedValue as Float
                pathMeasure.getPosTan(animatedValue, animatorPosArray, null)
                invalidate()
            }
            animatorArray.put(esAnimationDirection.ordinal, newAnimator)
            return newAnimator
        }
        // 动画已经初始化过了，更新下动画起点和终点
        animator.setFloatValues(startFloat, pathMeasure.length)
        return animator
    }

    fun startBallAnimation(esAnimationDirection: EsAnimationDirection) {
        startBallAnimations(esAnimationDirection)
    }

    fun startBallAnimations(vararg esAnimationDirections: EsAnimationDirection) {
        startBallAnimations(esAnimationDirections.toList())
    }

    fun startBallAnimations(esAnimationDirections: List<EsAnimationDirection>) {
        if (esAnimationDirections.isEmpty()) return
        if (isStationOffline) return
        post {
            esAnimationDirections.forEach {
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
            esAnimationDirection == EsAnimationDirection.GRID_TO_GRID_LOAD
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
     * @param esAnimationDirection
     */
    fun endBallAnimation(esAnimationDirection: EsAnimationDirection) {
        if (runningAnimatorArray.isEmpty()) {
            return
        }
        val animator = runningAnimatorArray.get(esAnimationDirection.ordinal) ?: return
        runningAnimatorArray.remove(esAnimationDirection.ordinal)
        post {
            animator.end()
        }
    }

    fun endBallAnimations(vararg esAnimationDirections: EsAnimationDirection) {
        endBallAnimations(esAnimationDirections.toList())
    }

    /**
     * End ball animations
     * 取消某些小球动画
     * @param esAnimationDirections
     */
    fun endBallAnimations(esAnimationDirections: List<EsAnimationDirection>) {
        if (esAnimationDirections.isEmpty() || runningAnimatorArray.isEmpty()) return
        // 停止动画并从正在进行的动画集合中移除
        esAnimationDirections.forEach {
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
        esAnimationDirection: EsAnimationDirection,
        objectAnimator: ValueAnimator
    ) {
        if (runningAnimatorArray.isEmpty()) {
            runningAnimatorArray.put(esAnimationDirection.ordinal, objectAnimator)
            return
        }
        val animator = runningAnimatorArray.get(esAnimationDirection.ordinal)
        if (animator == null) {
            runningAnimatorArray.put(esAnimationDirection.ordinal, objectAnimator)
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