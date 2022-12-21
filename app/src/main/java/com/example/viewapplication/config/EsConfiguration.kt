package com.example.viewapplication.config

import android.content.Context
import android.graphics.Paint
import android.util.SparseArray
import com.example.viewapplication.R
import com.example.viewapplication.dp
import com.example.viewapplication.getColorById

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



    val animationDuration: Long = ANIMATION_DURATION
    val animationDelay: Long = ANIMATION_START_DELAY

    companion object {
        private val sConfigurations = SparseArray<EsConfiguration?>(2)

        private const val ANIMATION_START_DELAY = 300L // 动画运行时长
        private const val ANIMATION_DURATION = 2000L // 动画运行时长
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