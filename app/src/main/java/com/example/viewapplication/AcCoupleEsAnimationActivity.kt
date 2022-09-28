package com.example.viewapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.viewapplication.databinding.ActivityAcCoupleEsAnimationBinding
import com.example.viewapplication.enumeration.ArrowDirection
import com.example.viewapplication.view.AcCoupleEsPathView

class AcCoupleEsAnimationActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityAcCoupleEsAnimationBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.start.setOnClickListener {
//            binding.acCouplePath.isOffline = true
            binding.acCouplePath.startArrowAnimations(
                ArrowDirection.PV_TO_INVERTER,
                ArrowDirection.INVERTER_TO_CENTER,
                ArrowDirection.AC_TO_CENTER,
                ArrowDirection.CENTER_TO_GRID_LOAD,
                ArrowDirection.AC_TO_BATTERY,
                ArrowDirection.AC_TO_BACK_UP_LOAD,
                ArrowDirection.TOP_TO_CENTER,
                ArrowDirection.GRID_TO_TOP_CENTER
            )
//            binding.acCouplePath.startPvToInverterAnimation()
//            binding.acCouplePath.startInverterToCenterAnimation()
//            binding.acCouplePath.startCenterToTopAnimation()
//            binding.acCouplePath.startCenterToAcAnimation()
        }
        binding.start1.setOnClickListener {
            binding.acCouplePath.startArrowAnimations(
                ArrowDirection.PV_TO_INVERTER,
                ArrowDirection.INVERTER_TO_CENTER,
                ArrowDirection.AC_TO_CENTER,
                ArrowDirection.CENTER_TO_GRID_LOAD,
            )
        }
        binding.cancel.setOnClickListener {
            binding.acCouplePath.endAllArrowAnimations()
//            binding.acCouplePath.endPvToInverterAnimation()
//            binding.acCouplePath.endInverterToCenterAnimation()
//            binding.acCouplePath.endCenterToTopAnimation()
//            binding.acCouplePath.endCenterToAcAnimation()
        }

        binding.cancelCenterToTop.setOnClickListener {
            binding.acCouplePath.endArrowAnimation(ArrowDirection.TOP_TO_CENTER)
        }
    }
}