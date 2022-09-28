package com.example.viewapplication.config

import android.content.Context
import android.graphics.Paint
import android.util.SparseArray
import com.example.viewapplication.R
import com.example.viewapplication.dp
import com.example.viewapplication.getColorById
import kotlin.math.tan

/**
 * @author zijian.zhan
 * @description
 * @date 2022/09/14 16:23
 */
class AcConfiguration private constructor(private val mContext: Context) {
    val pathStrokeWidth: Float = 2f.dp // 画笔宽度
    val centerCircleRadius = 5f.dp // 中心圆半径
    val elementCircleRadius = 32f.dp // 组件圆半径

    val arrowWidth = 8f.dp // 箭头宽度
    val arrowHeight = 8f.dp // 箭头高度
    /**
     * 箭头内凹长度
     */
    val arrowConcaveLength by lazy { (arrowWidth / 2f / tan(Math.toRadians((ARROW_CONCAVE_ANGLE / 2).toDouble()))).toFloat() }

    val viewWidth = 260f.dp // view 宽度
    val fullElementHeight = 210f.dp // 完整组件视图高度
    val acModuleHeight = fullElementHeight / 2f // ac模块视图高度

    // ************* paint ************** //

    val blueColor: Int
        get() = getColorById(mContext, R.color.blue_5f91cb_color)
    val greenColor: Int
        get() = getColorById(mContext, R.color.green_aed681_color)
    val grayColor: Int
        get() = getColorById(mContext, R.color.gray_cc_color)

    val offlinePaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = pathStrokeWidth
            color = grayColor
        }
    }

    /**
     * 蓝线 paint
     */
    val blueLinePaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = pathStrokeWidth
            color = blueColor
        }
    }

    /**
     * 绿线 paint
     */
    val greenLinePaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = pathStrokeWidth
            color = greenColor
        }
    }

    /**
     * 组件圆画笔
     */
    val circlePaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = greenColor
        }
    }

    /**
     * 蓝色箭头画笔
     */
    val blueArrowPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL_AND_STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            strokeWidth = 2f.dp
            color = blueColor
        }
    }

    /**
     * 绿色箭头 paint
     */
    val greenArrowPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL_AND_STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            strokeWidth = 2f.dp
            color = greenColor
        }
    }

    val animationDuration: Long = ANIMATION_DURATION

    companion object {
        private val sConfigurations = SparseArray<AcConfiguration?>(2)

        const val ARROW_CONCAVE_ANGLE = 90f // 箭头内凹角

        private const val ANIMATION_START_DELAY = 2000L // 动画启动延时

        private const val ANIMATION_DURATION = 2000L // 动画运行时长


        fun get(context: Context): AcConfiguration {
            val metrics = context.resources.displayMetrics
            val density = (100.0f * metrics.density).toInt()
            var configuration: AcConfiguration? = sConfigurations.get(density)
            if (configuration == null) {
                configuration = AcConfiguration(context)
                sConfigurations.put(density, configuration)
            }
            return configuration
        }
    }


}