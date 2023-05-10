package com.example.viewapplication.config

import android.content.Context
import android.graphics.Paint
import android.util.SparseArray
import com.example.viewapplication.R
import com.example.viewapplication.dp
import com.example.viewapplication.getColorById
import kotlin.math.pow
import kotlin.math.sqrt

/**
 *
 * @description
 * @author zijian.zhan
 * @date 2022/09/28 15:56
 */
class EsConfiguration private constructor(private val mContext: Context) {

    val horizontalDistance = 36f.dp
    val verticalDistance = 146f.dp
    val arcRadius = 20f.dp
    val maxBallRadius = 10f.dp
    val roundRadius = 40f.dp
    val pathHorizontalPadding = 40f.dp
    val extraWidth by lazy { circleStrokeWidth }
    val ballInnerRadius = 4f.dp
    val ballOuterRadius = 10f.dp

    private val circleStrokeWidth = 3f.dp
    val circleRadius = 40f.dp

    private val pathStrokeWidth = 2f.dp

    val horizontalExtra = 2f.dp // 横向额外移动距离
    val gridLoadDy = sqrt(circleRadius.pow(2) - (pathHorizontalPadding / 2).pow(2)) // 电网负载 y 轴方向上的增量，使用勾股定理

    // ************* color ************** //
    val orangeColor: Int
        get() = getColorById(mContext, R.color.orange_fda23a_color)
    val p33OrangeColor: Int
        get() = getColorById(mContext, R.color.orange_33_f9ad57_color)
    val orangeF9AD57Color: Int
        get() = getColorById(mContext, R.color.orange_f9ad57_color)
    val p33OrangeF9AD57Color: Int
        get() = getColorById(mContext, R.color.orange_33_f9ad57_color)
    val yellowColor: Int
        get() = getColorById(mContext, R.color.yellow_f0cf00_color)
    val p33YellowColor: Int
        get() = getColorById(mContext, R.color.yellow_33_f0cf00_color)
    val redColor: Int
        get() = getColorById(mContext, R.color.red_f56d66_color)
    val p33RedColor: Int
        get() = getColorById(mContext, R.color.red_33_f56d66_color)
    val greenColor: Int
        get() = getColorById(mContext, R.color.green_aada76_color)
    val p33GreenColor: Int
        get() = getColorById(mContext, R.color.green_4d_aada76_color)
    val blue06b389Color: Int
        get() = getColorById(mContext, R.color.blue_06b389_color)
    val p33Blue06b389Color: Int
        get() = getColorById(mContext, R.color.blue_4d_06b389_color)
    val blueColor: Int
        get() = getColorById(mContext, R.color.blue_5f91cb_color)
    val p33BlueColor: Int
        get() = getColorById(mContext, R.color.blue_4d_5f91cb_color)
    val purpleColor: Int
        get() = getColorById(mContext, R.color.purple_b676da_color)
    val p33PurpleColor: Int
        get() = getColorById(mContext, R.color.purple_33_b676da_color)

    val offlineColor: Int
        get() = getColorById(mContext, R.color.gray_cc_color)
    
    // ************* paint ************** //

    val strokeCirclePaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = circleStrokeWidth

        }
    }
    val fillCirclePaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            strokeWidth = circleStrokeWidth
            color = getColorById(mContext, R.color.white)
        }
    }

    val progressPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = circleStrokeWidth
            strokeCap = Paint.Cap.ROUND
        }
    }
    val ballPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
        }
    }
    val pathPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = pathStrokeWidth
        }
    }
    val offlinePaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = pathStrokeWidth
            color = offlineColor
        }
    }


    val animationDuration: Long = ANIMATION_DURATION
    val animationDelay: Long = ANIMATION_START_DELAY

    companion object {
        private val sConfigurations = SparseArray<EsConfiguration?>(2)

        private const val ANIMATION_START_DELAY = 300L // 动画运行时长
        private const val ANIMATION_DURATION = 2000L // 动画运行时长

        /**
         * 球体缩小最小比例
         */
        const val MIN_BALL_PROPORTION = 0.4f

        /**
         * 球体增大最大比例
         */
        const val MAX_BALL_PROPORTION = 1.0f

        /**
         * 电池背景宽度
         */
        const val BATTERY_BACKGROUND_WIDTH = 25f

        fun get(context: Context): EsConfiguration {

            val metrics = context.resources.displayMetrics
            val density = (100.0f * metrics.density).toInt()
            var configuration: EsConfiguration? = sConfigurations.get(density)
            if (configuration == null) {
                configuration = EsConfiguration(context)
                sConfigurations.put(density, configuration)
            }
            return configuration
        }
    }
}