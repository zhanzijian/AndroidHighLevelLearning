package com.example.viewapplication.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.example.viewapplication.R
import com.example.viewapplication.databinding.LayoutAcCircleElementBinding
import com.google.android.material.card.MaterialCardView

/**
 *
 * @description AcCouple圆形组件
 * @author zijian.zhan
 * @date 2022/08/15 14:52
 */
class AcCircleElement(context: Context, attrs: AttributeSet?) : MaterialCardView(context, attrs) {
    private var binding: LayoutAcCircleElementBinding

    /**
     * 文字
     */
    var acPowerText: String? = "--"
        set(value) {
            field = value
            binding.acPowerText.text = value
        }

    var showPowerText: Boolean = true
        set(value) {
            field = value
            binding.acPowerText.visibility = if (value) View.VISIBLE else View.GONE
        }

    var acIcon: Int = 0
    set(value) {
        field = value
        if (value != 0) {
            binding.acIcon.setImageResource(acIcon)
        }
    }



    init {
        val view =
            LayoutInflater.from(context).inflate(R.layout.layout_ac_circle_element, this, true)
        binding = LayoutAcCircleElementBinding.bind(view)
        initAttributes(attrs)
    }

    private fun initAttributes(attrs: AttributeSet?) {
        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.AcCircleElement)
        acPowerText = typedArray.getString(R.styleable.AcCircleElement_acPowerText)
        acIcon = typedArray.getResourceId(R.styleable.AcCircleElement_acIcon, 0)
        showPowerText = typedArray.getBoolean(R.styleable.AcCircleElement_acShowPowerText, true)
        typedArray.recycle()
    }
}