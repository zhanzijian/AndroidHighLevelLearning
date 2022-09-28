package com.example.viewapplication.config

import android.content.Context
import android.graphics.Paint
import android.util.SparseArray
import com.example.viewapplication.dp

/**
 *
 * @description
 * @author zijian.zhan
 * @date 2022/09/28 15:56
 */
class EsConfiguration private constructor(val mContext: Context) {
    val horizontalDistance = 28f.dp
    val verticalDistance = 130f.dp
    val arcRadius = 20f.dp
    val maxBallRadius = 10f.dp
    val roundRadius = 40f.dp
    val pathHorizontalPadding = 40f.dp
    val extraWidth = 2f.dp
    val ballInnerRadius = 4f.dp
    val ballOuterRadius = 10f.dp

    private val circleStrokeWidth = 2f.dp
    val circleRadius = 40f.dp

    private val pathStrokeWidth = 2f.dp

    // ************* paint ************** //

    val circlePaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = circleStrokeWidth
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

        private const val ANIMATION_START_DELAY = 2000L // 动画运行时长
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