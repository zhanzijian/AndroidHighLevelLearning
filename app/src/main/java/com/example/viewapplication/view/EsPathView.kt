package com.example.viewapplication.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import com.example.viewapplication.R
import com.example.viewapplication.config.EsConfiguration
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

    private lateinit var circlePaint: Paint
    private lateinit var progressPaint: Paint
    private lateinit var ballPaint: Paint
    private lateinit var pathPaint: Paint

    private val mAnimationDuration = esConfig.animationDuration
    private val mAnimationDelay = esConfig.animationDelay


    /* -------------- 左上 ----------------*/
    private val leftTopPath by lazy(::Path)
    private val leftTopPos = floatArrayOf(0f, 0f)
    private val leftTopTan = floatArrayOf(0f, 0f)

    /* -------------- 右上 ----------------*/
    private val rightTopPath by lazy(::Path)
    private val rightTopPos = floatArrayOf(0f, 0f)
    private val rightTopTan = floatArrayOf(0f, 0f)

    /* -------------- 左下 ----------------*/
    private val leftBottomPath by lazy(::Path)
    private val leftBottomPos = floatArrayOf(0f, 0f)
    private val leftBottomTan = floatArrayOf(0f, 0f)

    /* -------------- 右下 ----------------*/
    private val rightBottomPath by lazy(::Path)
    private val rightBottomPos = floatArrayOf(0f, 0f)
    private val rightBottomTan = floatArrayOf(0f, 0f)

    init {
        initPaints()
    }

    private fun initPaints() {
        circlePaint = esConfig.circlePaint
        progressPaint = esConfig.progressPaint
        ballPaint = esConfig.ballPaint
        pathPaint = esConfig.pathPaint
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        initLeftTopPath()
        initRightTopPath()
        initLeftBottomPath()
        initRightBottomPath()
    }

    /**
     * 初始化左上小球运动路径
     */
    private fun initLeftTopPath() {
        if (!leftTopPath.isEmpty) {
            leftTopPath.reset()
        }
        // 中心点坐标
        val centerX = width / 2f
        val centerY = height / 2f

        // path初始位置
        leftTopPos[0] = centerX - (mPathHorizontalPadding / 2 + mArcRadius + mHorizontalDistance)
        leftTopPos[1] = centerY - (mVerticalDistance + mArcRadius)
        Log.d(TAG, "初始化: leftTopPos[0]：${leftTopPos[0]},leftTopPos[1]:${leftTopPos[1]}")
        leftTopPath.moveTo(
            leftTopPos[0],
            leftTopPos[1]
        )
        leftTopPath.lineTo(
            centerX - (mPathHorizontalPadding / 2 + mArcRadius),
            centerY - (mVerticalDistance + mArcRadius)
        )
        leftTopPath.arcTo(
            centerX - (mPathHorizontalPadding / 2 + mArcRadius * 2),
            centerY - (mVerticalDistance + mArcRadius),
            centerX - mPathHorizontalPadding / 2,
            centerY - mVerticalDistance + mArcRadius,
            270f,
            90f,
            false
        )
        leftTopPath.lineTo(
            centerX - mPathHorizontalPadding / 2,
            centerY
        )
    }

    /**
     * 初始化右上小球运动路径
     */
    private fun initRightTopPath() {
        if (!rightTopPath.isEmpty) {
            rightTopPath.reset()
        }
        // 中心点坐标
        val centerX = width / 2f
        val centerY = height / 2f

        // path初始位置
        rightTopPos[0] = centerX + (mPathHorizontalPadding / 2 + mArcRadius + mHorizontalDistance) - mMaxBallRadius
        rightTopPos[1] = centerY - (mVerticalDistance + mArcRadius)

        rightTopPath.moveTo(
            centerX + (mPathHorizontalPadding / 2 + mArcRadius + mHorizontalDistance),
            centerY - (mVerticalDistance + mArcRadius)
        )
        rightTopPath.lineTo(
            centerX + (mPathHorizontalPadding / 2 + mArcRadius),
            centerY - (mVerticalDistance + mArcRadius)
        )
        rightTopPath.arcTo(
            centerX + mPathHorizontalPadding / 2,
            centerY - (mVerticalDistance + mArcRadius),
            centerX + (mPathHorizontalPadding / 2 + mArcRadius * 2),
            centerY - mVerticalDistance + mArcRadius,
            270f,
            -90f,
            false
        )
        rightTopPath.lineTo(
            centerX + mPathHorizontalPadding / 2,
            centerY
        )
    }

    /**
     * 初始化左下小球运动路径
     */
    private fun initLeftBottomPath() {
        if (!leftBottomPath.isEmpty) {
            leftBottomPath.reset()
        }
        // 中心点坐标
        val centerX = width / 2f
        val centerY = height / 2f

        // path初始位置
        leftBottomPos[0] = centerX - (mPathHorizontalPadding / 2 + mArcRadius + mHorizontalDistance) + mMaxBallRadius
        leftBottomPos[1] = centerY + (mVerticalDistance + mArcRadius)

        // 移动至path初始位置
        leftBottomPath.moveTo(
            centerX - (mPathHorizontalPadding / 2 + mArcRadius + mHorizontalDistance),
            centerY + (mVerticalDistance + mArcRadius)
        )
        leftBottomPath.lineTo(
            centerX - (mPathHorizontalPadding / 2 + mArcRadius),
            centerY + (mVerticalDistance + mArcRadius)
        )
        leftBottomPath.arcTo(
            centerX - (mPathHorizontalPadding / 2 + mArcRadius * 2),
            centerY + mVerticalDistance - mArcRadius,
            centerX - mPathHorizontalPadding / 2,
            centerY + (mVerticalDistance + mArcRadius),
            90f,
            -90f,
            false
        )
        leftBottomPath.lineTo(
            centerX - mPathHorizontalPadding / 2,
            centerY
        )
    }

    /**
     * 初始化右下小球运动路径
     */
    private fun initRightBottomPath() {
        if (!rightBottomPath.isEmpty) {
            rightBottomPath.reset()
        }
        // 中心点坐标
        val centerX = width / 2f
        val centerY = height / 2f

        // 小球初始位置
        rightBottomPos[0] = centerX + (mPathHorizontalPadding / 2 + mArcRadius + mHorizontalDistance) - mMaxBallRadius
        rightBottomPos[1] = centerY + (mVerticalDistance + mArcRadius)

        // 移动至path初始位置
        rightBottomPath.moveTo(
            centerX + (mPathHorizontalPadding / 2 + mArcRadius + mHorizontalDistance),
            centerY + (mVerticalDistance + mArcRadius)
        )
        rightBottomPath.lineTo(
            centerX + (mPathHorizontalPadding / 2 + mArcRadius),
            centerY + (mVerticalDistance + mArcRadius)
        )
        rightBottomPath.arcTo(
            centerX + mPathHorizontalPadding / 2,
            centerY + mVerticalDistance - mArcRadius,
            centerX + (mPathHorizontalPadding / 2 + mArcRadius * 2),
            centerY + (mVerticalDistance + mArcRadius),
            90f,
            90f,
            false
        )
        rightBottomPath.lineTo(
            centerX + mPathHorizontalPadding / 2,
            centerY
        )
    }


    override fun onDraw(canvas: Canvas) {
        // 中心点坐标
        val centerX = width / 2f
        val centerY = height / 2f

        // 画左上部分
        drawLeftTop(centerX, centerY, canvas)

        // 画右上部分
        drawRightTop(centerX, centerY, canvas)

        // 画左下部分
        drawLeftBottom(centerX, centerY, canvas)

        // 画右下部分
        drawRightBottom(centerX, centerY, canvas)
    }

    private fun drawLeftTop(centerX: Float, centerY: Float, canvas: Canvas) {
        // 初始小球圆心x轴位置
        val ballStartX = centerX - (mPathHorizontalPadding / 2 + mArcRadius + mHorizontalDistance) + mMaxBallRadius
        val pos0 = leftTopPos[0]
        val pos1 = leftTopPos[1]
        val ballCenterX = maxOf(pos0, ballStartX)
        Log.d(TAG, "使用时leftTopPos[0]：${leftTopPos[0]},leftTopPos[1]:${leftTopPos[1]}")
        ballPaint.color = getColorById(context, R.color.yellow_f0cf00_color)
        canvas.drawCircle(
            ballCenterX,
            pos1,
            mBallInnerRadius,
            ballPaint
        )
        // 画运动小球外径
        ballPaint.color = getColorById(context, R.color.yellow_33_f0cf00_color)
        canvas.drawCircle(
            ballCenterX,
            pos1,
            mBallOuterRadius,
            ballPaint
        )

        // 画路径
        pathPaint.color = getColorById(context, R.color.yellow_f0cf00_color)
        canvas.drawPath(leftTopPath, pathPaint)

        // 画圆
        circlePaint.color = getColorById(context, R.color.yellow_4d_f0cf00_color)
        canvas.drawCircle(
            centerX - (mPathHorizontalPadding / 2 + mArcRadius + mHorizontalDistance + mCircleRadius),
            centerY - (mVerticalDistance + mArcRadius),
            mCircleRadius,
            circlePaint
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

    private fun drawRightTop(centerX: Float, centerY: Float, canvas: Canvas) {
        // 画圆
        circlePaint.color = getColorById(context, R.color.red_4d_f56d66_color)
        canvas.drawCircle(
            centerX + (mPathHorizontalPadding / 2 + mArcRadius + mHorizontalDistance + mCircleRadius),
            centerY - (mVerticalDistance + mArcRadius),
            mCircleRadius,
            circlePaint
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

        // 初始小球圆心x轴位置
        val ballStartX = centerX + (mPathHorizontalPadding / 2 + mArcRadius + mHorizontalDistance) - mMaxBallRadius
        val pos0 = rightTopPos[0]
        val pos1 = rightTopPos[1]
        val ballCenterX = minOf(pos0,ballStartX)
        // 画运动小球内径
        ballPaint.color = getColorById(context, R.color.red_f56d66_color)
        canvas.drawCircle(
            ballCenterX,
            pos1,
            mBallInnerRadius,
            ballPaint
        )
        // 画运动小球外径
        ballPaint.color = getColorById(context, R.color.red_33_f56d66_color)
        canvas.drawCircle(
            ballCenterX,
            pos1,
            mBallOuterRadius,
            ballPaint
        )
        // 画路径
        pathPaint.color = getColorById(context, R.color.red_f56d66_color)
        canvas.drawPath(rightTopPath, pathPaint)
    }

    private fun drawLeftBottom(centerX: Float, centerY: Float, canvas: Canvas) {
        // 画圆
        circlePaint.color = getColorById(context, R.color.green_33_aed681_color)
        canvas.drawCircle(
            centerX - (mPathHorizontalPadding / 2 + mArcRadius + mHorizontalDistance + mCircleRadius),
            centerY + (mVerticalDistance + mArcRadius),
            mCircleRadius,
            circlePaint
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

        // 初始小球圆心x轴位置
        val ballStartX = centerX - (mPathHorizontalPadding / 2 + mArcRadius + mHorizontalDistance) + mMaxBallRadius
        val pos0 = leftBottomPos[0]
        val pos1 = leftBottomPos[1]
        val ballCenterX = maxOf(pos0,ballStartX)
        // 画运动小球内径
        ballPaint.color = getColorById(context, R.color.green_aed681_color)
        canvas.drawCircle(
            ballCenterX,
            pos1,
            mBallInnerRadius,
            ballPaint
        )
        // 画运动小球外径
        ballPaint.color = getColorById(context, R.color.green_33_aed681_color)
        canvas.drawCircle(
            ballCenterX,
            pos1,
            mBallOuterRadius,
            ballPaint
        )
        // 画路径
        pathPaint.color = getColorById(context, R.color.green_aed681_color)
        canvas.drawPath(leftBottomPath, pathPaint)
    }

    private fun drawRightBottom(centerX: Float, centerY: Float, canvas: Canvas) {
        // 画圆
        circlePaint.color = getColorById(context, R.color.orange_4d_fda23a_color)
        canvas.drawCircle(
            centerX + (mPathHorizontalPadding / 2 + mArcRadius + mHorizontalDistance + mCircleRadius),
            centerY + (mVerticalDistance + mArcRadius),
            mCircleRadius,
            circlePaint
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


        // 初始小球圆心x轴位置
        val ballStartX = centerX + (mPathHorizontalPadding / 2 + mArcRadius + mHorizontalDistance) - mMaxBallRadius
        val pos0 = rightBottomPos[0]
        val pos1 = rightBottomPos[1]
        val ballCenterX = minOf(pos0,ballStartX)
        println("ballStartX:$ballStartX，pos0:$pos0, pos1:$pos1, ballCenterX:$ballCenterX")
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
        canvas.drawPath(rightBottomPath, pathPaint)
    }

    /**
     * Start left top ball animator
     * 开始左上小球的动画
     * @param direction BallDirection.LEFT_TOP_TO_CENTER or BallDirection.CENTER_TO_LEFT_TOP
     */
    fun startLeftTopBallAnimator(direction: BallDirection) {
        val pathMeasure = PathMeasure(leftTopPath, false)
        val length = pathMeasure.length
        if (length == 0f) {
            return
        }
        val valueAnimator = ValueAnimator.ofFloat(0f, length)
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.startDelay = mAnimationDelay
        valueAnimator.duration = mAnimationDuration
        valueAnimator.repeatCount = ValueAnimator.INFINITE
        valueAnimator.addUpdateListener { animation: ValueAnimator ->
            val animatedValue = animation.animatedValue as Float
            pathMeasure.getPosTan(animatedValue, leftTopPos, leftTopTan)
            Log.d(TAG, "新鲜出炉的leftTopPos[0]：${leftTopPos[0]},leftTopPos[1]:${leftTopPos[1]}")
            invalidate()
        }
        if (direction == BallDirection.CENTER_TO_LEFT_TOP) {
            valueAnimator.reverse()
            return
        }
        valueAnimator.start()
    }

    /**
     * Start right top ball animator
     * 开始右上小球的动画
     * @param direction BallDirection.RIGHT_TOP_TO_CENTER or BallDirection.CENTER_TO_RIGHT_TOP
     */
    fun startRightTopBallAnimator(direction: BallDirection) {
        val pathMeasure = PathMeasure(rightTopPath, false)
        val length = pathMeasure.length
        if (length == 0f) {
            return
        }
        val valueAnimator = ValueAnimator.ofFloat(0f, length)
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.startDelay = mAnimationDelay
        valueAnimator.duration = mAnimationDuration
        valueAnimator.repeatCount = ValueAnimator.INFINITE
        valueAnimator.addUpdateListener { animation: ValueAnimator ->
            val animatedValue = animation.animatedValue as Float
            pathMeasure.getPosTan(animatedValue, rightTopPos, rightTopTan)
            invalidate()
        }
        if (direction == BallDirection.CENTER_TO_RIGHT_TOP) {
            valueAnimator.reverse()
            return
        }
        valueAnimator.start()
    }

    /**
     * Start left bottom ball animator
     * 开始左下小球的动画
     * @param direction BallDirection.LEFT_BOTTOM_TO_CENTER or BallDirection.CENTER_TO_LEFT_BOTTOM
     */
    fun startLeftBottomBallAnimator(direction: BallDirection) {
        val pathMeasure = PathMeasure(leftBottomPath, false)
        val length = pathMeasure.length
        if (length == 0f) {
            return
        }
        val valueAnimator = ValueAnimator.ofFloat(0f, length)
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.startDelay = mAnimationDelay
        valueAnimator.duration = mAnimationDuration
        valueAnimator.repeatCount = ValueAnimator.INFINITE
        valueAnimator.addUpdateListener { animation: ValueAnimator ->
            val animatedValue = animation.animatedValue as Float
            pathMeasure.getPosTan(animatedValue, leftBottomPos, leftBottomTan)
            invalidate()
        }
        if (direction == BallDirection.CENTER_TO_LEFT_BOTTOM) {
            valueAnimator.reverse()
            return
        }
        valueAnimator.start()
    }

    /**
     * Start right bottom ball animator
     * 开始右下小球的动画
     * {@link BallDirection.RIGHT_BOTTOM_TO_CENTER }
     * @param direction  BallDirection.RIGHT_BOTTOM_TO_CENTER or BallDirection.CENTER_TO_RIGHT_BOTTOM
     */
    fun startRightBottomBallAnimator(direction: BallDirection) {
        val pathMeasure = PathMeasure(rightBottomPath, false)
        val length = pathMeasure.length
        if (length == 0f) {
            return
        }
        val valueAnimator = ValueAnimator.ofFloat(0f, length)
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.startDelay = mAnimationDelay
        valueAnimator.duration = mAnimationDuration
        valueAnimator.repeatCount = ValueAnimator.INFINITE
        valueAnimator.addUpdateListener { animation: ValueAnimator ->
            val animatedValue = animation.animatedValue as Float
            pathMeasure.getPosTan(animatedValue, rightBottomPos, rightBottomTan)
            invalidate()
        }
        if (direction == BallDirection.CENTER_TO_RIGHT_BOTTOM) {
            valueAnimator.reverse()
            return
        }
        valueAnimator.start()

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

    enum class BallDirection(val nativeInt: Int) {
        /**
         * 左上到中间的方向
         */
        LEFT_TOP_TO_CENTER(0),

        /**
         * 中间到左上的方向
         */
        CENTER_TO_LEFT_TOP(1),

        /**
         * 左下到中间的方向
         */
        LEFT_BOTTOM_TO_CENTER(2),

        /**
         * 中间到左下的方向
         */
        CENTER_TO_LEFT_BOTTOM(3),

        /**
         * 右上到中间的方向
         */
        RIGHT_TOP_TO_CENTER(4),

        /**
         * 中间到右上的方向
         */
        CENTER_TO_RIGHT_TOP(5),

        /**
         * 右下到中间的方向
         */
        RIGHT_BOTTOM_TO_CENTER(6),

        /**
         * 中间到右下的方向
         */
        CENTER_TO_RIGHT_BOTTOM(7);
    }

}