package com.example.viewapplication.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import com.example.viewapplication.R
import com.example.viewapplication.dp
import com.example.viewapplication.getColorById

private val HORIZONTAL_DISTANCE = 28f.dp //
private val VERTICAL_DISTANCE = 130f.dp
private val ARC_RADIUS = 20f.dp
private val MAX_BALL_RADIUS = 10f.dp
private val ROUND_RADIUS = 40f.dp
private val PATH_HORIZONTAL_PADDING = 40f.dp
private val EXTRA_WIDTH = 4f.dp
private val BALL_INNER_RADIUS = 4f.dp
private val BALL_OUTER_RADIUS = 10f.dp

private val CIRCLE_STROKE_WIDTH = 3f.dp
private val CIRCLE_RADIUS = 40f.dp

private val PATH_STROKE_WIDTH = 2f.dp

/**
 *
 * @description 储能动图路径和小球动画
 * @author zhanzijian
 * @date 2022/02/24 14:35
 */
class EsPathView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = CIRCLE_STROKE_WIDTH
        style = Paint.Style.STROKE
    }

    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = CIRCLE_STROKE_WIDTH
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    private val ballPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val pathPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = PATH_STROKE_WIDTH
    }

//    private val

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

        // 左上运动小球初始位置
        leftTopPos[0] =
            centerX - (PATH_HORIZONTAL_PADDING / 2 + ARC_RADIUS + HORIZONTAL_DISTANCE + MAX_BALL_RADIUS)
        leftTopPos[1] = centerY - (VERTICAL_DISTANCE + ARC_RADIUS)

        // 移动至小球初始位置
        leftTopPath.moveTo(
            leftTopPos[0],
            leftTopPos[1]
        )
        leftTopPath.lineTo(
            centerX - (PATH_HORIZONTAL_PADDING / 2 + ARC_RADIUS),
            centerY - (VERTICAL_DISTANCE + ARC_RADIUS)
        )
        leftTopPath.arcTo(
            centerX - (PATH_HORIZONTAL_PADDING / 2 + ARC_RADIUS * 2),
            centerY - (VERTICAL_DISTANCE + ARC_RADIUS),
            centerX - PATH_HORIZONTAL_PADDING / 2,
            centerY - VERTICAL_DISTANCE + ARC_RADIUS,
            270f,
            90f,
            false
        )
        leftTopPath.lineTo(
            centerX - PATH_HORIZONTAL_PADDING / 2,
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

        // 右上运动小球初始位置
        rightTopPos[0] =
            centerX + (PATH_HORIZONTAL_PADDING / 2 + ARC_RADIUS + HORIZONTAL_DISTANCE + MAX_BALL_RADIUS)
        rightTopPos[1] = centerY - (VERTICAL_DISTANCE + ARC_RADIUS)

        // 移动至小球初始位置
        rightTopPath.moveTo(
            rightTopPos[0],
            rightTopPos[1]
        )
        rightTopPath.lineTo(
            centerX + (PATH_HORIZONTAL_PADDING / 2 + ARC_RADIUS),
            centerY - (VERTICAL_DISTANCE + ARC_RADIUS)
        )
        rightTopPath.arcTo(
            centerX + PATH_HORIZONTAL_PADDING / 2,
            centerY - (VERTICAL_DISTANCE + ARC_RADIUS),
            centerX + (PATH_HORIZONTAL_PADDING / 2 + ARC_RADIUS * 2),
            centerY - VERTICAL_DISTANCE + ARC_RADIUS,
            270f,
            -90f,
            false
        )
        rightTopPath.lineTo(
            centerX + PATH_HORIZONTAL_PADDING / 2,
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

        // 左下运动小球初始位置
        leftBottomPos[0] =
            centerX - (PATH_HORIZONTAL_PADDING / 2 + ARC_RADIUS + HORIZONTAL_DISTANCE + MAX_BALL_RADIUS)
        leftBottomPos[1] = centerY + (VERTICAL_DISTANCE + ARC_RADIUS)

        // 移动至小球初始位置
        leftBottomPath.moveTo(
            leftBottomPos[0],
            leftBottomPos[1]
        )
        leftBottomPath.lineTo(
            centerX - (PATH_HORIZONTAL_PADDING / 2 + ARC_RADIUS),
            centerY + (VERTICAL_DISTANCE + ARC_RADIUS)
        )
        leftBottomPath.arcTo(
            centerX - (PATH_HORIZONTAL_PADDING / 2 + ARC_RADIUS * 2),
            centerY + VERTICAL_DISTANCE - ARC_RADIUS,
            centerX - PATH_HORIZONTAL_PADDING / 2,
            centerY + (VERTICAL_DISTANCE + ARC_RADIUS),
            90f,
            -90f,
            false
        )
        leftBottomPath.lineTo(
            centerX - PATH_HORIZONTAL_PADDING / 2,
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

        // 右下运动小球初始位置
        rightBottomPos[0] =
            centerX + (PATH_HORIZONTAL_PADDING / 2 + ARC_RADIUS + HORIZONTAL_DISTANCE + MAX_BALL_RADIUS)
        rightBottomPos[1] = centerY + (VERTICAL_DISTANCE + ARC_RADIUS)

        // 移动至小球初始位置
        rightBottomPath.moveTo(
            rightBottomPos[0],
            rightBottomPos[1]
        )
        rightBottomPath.lineTo(
            centerX + (PATH_HORIZONTAL_PADDING / 2 + ARC_RADIUS),
            centerY + (VERTICAL_DISTANCE + ARC_RADIUS)
        )
        rightBottomPath.arcTo(
            centerX + PATH_HORIZONTAL_PADDING / 2,
            centerY + VERTICAL_DISTANCE - ARC_RADIUS,
            centerX + (PATH_HORIZONTAL_PADDING / 2 + ARC_RADIUS * 2),
            centerY + (VERTICAL_DISTANCE + ARC_RADIUS),
            90f,
            90f,
            false
        )
        rightBottomPath.lineTo(
            centerX + PATH_HORIZONTAL_PADDING / 2,
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
        // 画圆
        circlePaint.color = getColorById(context, R.color.yellow_4d_f0cf00_color)
        canvas.drawCircle(
            centerX - (PATH_HORIZONTAL_PADDING / 2 + ARC_RADIUS + HORIZONTAL_DISTANCE + CIRCLE_RADIUS),
            centerY - (VERTICAL_DISTANCE + ARC_RADIUS),
            CIRCLE_RADIUS,
            circlePaint
        )
        // 画进度
        progressPaint.color = getColorById(context, R.color.yellow_f0cf00_color)
        canvas.drawArc(
            centerX - (PATH_HORIZONTAL_PADDING / 2 + ARC_RADIUS + HORIZONTAL_DISTANCE + CIRCLE_RADIUS) - CIRCLE_RADIUS,
            centerY - (VERTICAL_DISTANCE + ARC_RADIUS) - CIRCLE_RADIUS,
            centerX - (PATH_HORIZONTAL_PADDING / 2 + ARC_RADIUS + HORIZONTAL_DISTANCE + CIRCLE_RADIUS) + CIRCLE_RADIUS,
            centerY - (VERTICAL_DISTANCE + ARC_RADIUS) + CIRCLE_RADIUS,
            -90f,
            90f,
            false,
            progressPaint
        )


        // 画运动小球内径
        ballPaint.color = getColorById(context, R.color.yellow_f0cf00_color)
        canvas.drawCircle(
            leftTopPos[0],
            leftTopPos[1],
            BALL_INNER_RADIUS,
            ballPaint
        )
        // 画运动小球外径
        ballPaint.color = getColorById(context, R.color.yellow_33_f0cf00_color)
        canvas.drawCircle(
            leftTopPos[0],
            leftTopPos[1],
            BALL_OUTER_RADIUS,
            ballPaint
        )
        // 画路径
        pathPaint.color = getColorById(context, R.color.yellow_f0cf00_color)
        canvas.drawPath(leftTopPath, pathPaint)
    }

    private fun drawRightTop(centerX: Float, centerY: Float, canvas: Canvas) {
        // 画圆
        circlePaint.color = getColorById(context, R.color.red_4d_f56d66_color)
        canvas.drawCircle(
            centerX + (PATH_HORIZONTAL_PADDING / 2 + ARC_RADIUS + HORIZONTAL_DISTANCE + CIRCLE_RADIUS),
            centerY - (VERTICAL_DISTANCE + ARC_RADIUS),
            CIRCLE_RADIUS,
            circlePaint
        )
        // 画进度
        progressPaint.color = getColorById(context, R.color.red_f56d66_color)
        canvas.drawArc(
            centerX + (PATH_HORIZONTAL_PADDING / 2 + ARC_RADIUS + HORIZONTAL_DISTANCE + CIRCLE_RADIUS) - CIRCLE_RADIUS,
            centerY - (VERTICAL_DISTANCE + ARC_RADIUS) - CIRCLE_RADIUS,
            centerX + (PATH_HORIZONTAL_PADDING / 2 + ARC_RADIUS + HORIZONTAL_DISTANCE + CIRCLE_RADIUS) + CIRCLE_RADIUS,
            centerY - (VERTICAL_DISTANCE + ARC_RADIUS) + CIRCLE_RADIUS,
            -90f,
            90f,
            false,
            progressPaint
        )


        // 画运动小球内径
        ballPaint.color = getColorById(context, R.color.red_f56d66_color)
        canvas.drawCircle(
            rightTopPos[0],
            rightTopPos[1],
            BALL_INNER_RADIUS,
            ballPaint
        )
        // 画运动小球外径
        ballPaint.color = getColorById(context, R.color.red_33_f56d66_color)
        canvas.drawCircle(
            rightTopPos[0],
            rightTopPos[1],
            BALL_OUTER_RADIUS,
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
            centerX - (PATH_HORIZONTAL_PADDING / 2 + ARC_RADIUS + HORIZONTAL_DISTANCE + CIRCLE_RADIUS),
            centerY + (VERTICAL_DISTANCE + ARC_RADIUS),
            CIRCLE_RADIUS,
            circlePaint
        )
        // 画进度
        progressPaint.color = getColorById(context, R.color.green_aed681_color)
        canvas.drawArc(
            centerX - (PATH_HORIZONTAL_PADDING / 2 + ARC_RADIUS + HORIZONTAL_DISTANCE + CIRCLE_RADIUS) - CIRCLE_RADIUS,
            centerY + (VERTICAL_DISTANCE + ARC_RADIUS) - CIRCLE_RADIUS,
            centerX - (PATH_HORIZONTAL_PADDING / 2 + ARC_RADIUS + HORIZONTAL_DISTANCE + CIRCLE_RADIUS) + CIRCLE_RADIUS,
            centerY + (VERTICAL_DISTANCE + ARC_RADIUS) + CIRCLE_RADIUS,
            -90f,
            90f,
            false,
            progressPaint
        )


        // 画运动小球内径
        ballPaint.color = getColorById(context, R.color.green_aed681_color)
        canvas.drawCircle(
            leftBottomPos[0],
            leftBottomPos[1],
            BALL_INNER_RADIUS,
            ballPaint
        )
        // 画运动小球外径
        ballPaint.color = getColorById(context, R.color.green_33_aed681_color)
        canvas.drawCircle(
            leftBottomPos[0],
            leftBottomPos[1],
            BALL_OUTER_RADIUS,
            ballPaint
        )
        // 画路径
        pathPaint.color = getColorById(context, R.color.green_aed681_color)
        canvas.drawPath(leftBottomPath, pathPaint)
    }

    private fun drawRightBottom(centerX: Float, centerY: Float, canvas: Canvas) {
        /*// 画圆
        circlePaint.color = getColorById(context, R.color.green_33_aed681_color)
        canvas.drawCircle(
            centerX - (PATH_HORIZONTAL_PADDING / 2 + ARC_RADIUS + HORIZONTAL_DISTANCE + CIRCLE_RADIUS),
            centerY + (VERTICAL_DISTANCE + ARC_RADIUS),
            CIRCLE_RADIUS,
            circlePaint
        )
        // 画进度
        progressPaint.color = getColorById(context, R.color.green_aed681_color)
        canvas.drawArc(
            centerX - (PATH_HORIZONTAL_PADDING / 2 + ARC_RADIUS + HORIZONTAL_DISTANCE + CIRCLE_RADIUS) - CIRCLE_RADIUS,
            centerY + (VERTICAL_DISTANCE + ARC_RADIUS) - CIRCLE_RADIUS,
            centerX - (PATH_HORIZONTAL_PADDING / 2 + ARC_RADIUS + HORIZONTAL_DISTANCE + CIRCLE_RADIUS) + CIRCLE_RADIUS,
            centerY + (VERTICAL_DISTANCE + ARC_RADIUS) + CIRCLE_RADIUS,
            -90f,
            90f,
            false,
            progressPaint
        )


        // 画运动小球内径
        ballPaint.color = getColorById(context, R.color.green_aed681_color)
        canvas.drawCircle(
            leftBottomPos[0],
            leftBottomPos[1],
            BALL_INNER_RADIUS,
            ballPaint
        )
        // 画运动小球外径
        ballPaint.color = getColorById(context, R.color.green_33_aed681_color)
        canvas.drawCircle(
            leftBottomPos[0],
            leftBottomPos[1],
            BALL_OUTER_RADIUS,
            ballPaint
        )
        // 画路径
        pathPaint.color = getColorById(context, R.color.green_aed681_color)
        canvas.drawPath(leftBottomPath, pathPaint)*/
    }

    fun startLeftTopBallAnimator() {
        val pathMeasure = PathMeasure(leftTopPath, false)
        val length = pathMeasure.length
        val valueAnimator = ValueAnimator.ofFloat(0f, length)
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.duration = 5000
        valueAnimator.repeatCount = ValueAnimator.INFINITE
        valueAnimator.addUpdateListener { animation: ValueAnimator ->
            val animatedValue = animation.animatedValue as Float
            pathMeasure.getPosTan(animatedValue, leftTopPos, leftTopTan)
            invalidate()
        }
        valueAnimator.start()
    }

    fun startRightTopBallAnimator() {
        val pathMeasure = PathMeasure(rightTopPath, false)
        val length = pathMeasure.length
        val valueAnimator = ValueAnimator.ofFloat(0f, length)
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.duration = 5000
        valueAnimator.repeatCount = ValueAnimator.INFINITE
        valueAnimator.addUpdateListener { animation: ValueAnimator ->
            val animatedValue = animation.animatedValue as Float
            pathMeasure.getPosTan(animatedValue, rightTopPos, rightTopTan)
            invalidate()
        }
        valueAnimator.start()
    }

    fun startLeftBottomBallAnimator() {
        val pathMeasure = PathMeasure(leftBottomPath, false)
        val length = pathMeasure.length
        val valueAnimator = ValueAnimator.ofFloat(0f, length)
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.duration = 5000
        valueAnimator.repeatCount = ValueAnimator.INFINITE
        valueAnimator.addUpdateListener { animation: ValueAnimator ->
            val animatedValue = animation.animatedValue as Float
            pathMeasure.getPosTan(animatedValue, leftBottomPos, leftBottomTan)
            invalidate()
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
            (CIRCLE_RADIUS * 2 + HORIZONTAL_DISTANCE + ARC_RADIUS) * 2 + PATH_HORIZONTAL_PADDING
        val height = (CIRCLE_RADIUS + VERTICAL_DISTANCE + ARC_RADIUS) * 2
        setMeasuredDimension(
            (width + EXTRA_WIDTH).toInt(),
            (height + EXTRA_WIDTH).toInt()
        )
    }

}