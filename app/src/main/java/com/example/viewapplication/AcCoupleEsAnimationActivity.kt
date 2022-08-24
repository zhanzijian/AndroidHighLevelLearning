package com.example.viewapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.viewapplication.databinding.ActivityAcCoupleEsAnimationBinding
import com.example.viewapplication.view.AcCoupleEsPathView

class AcCoupleEsAnimationActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityAcCoupleEsAnimationBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.start.setOnClickListener {
            binding.acCouplePath.startArrowAnimations(
                AcCoupleEsPathView.ArrowDirection.PV_TO_INVERTER,
                AcCoupleEsPathView.ArrowDirection.INVERTER_TO_CENTER,
                AcCoupleEsPathView.ArrowDirection.CENTER_TO_TOP,
                AcCoupleEsPathView.ArrowDirection.CENTER_TO_AC,
                AcCoupleEsPathView.ArrowDirection.CENTER_TO_GRID_LOAD,
                AcCoupleEsPathView.ArrowDirection.BATTERY_TO_AC,
                AcCoupleEsPathView.ArrowDirection.AC_TO_BACK_UP_LOAD,
                AcCoupleEsPathView.ArrowDirection.TOP_CENTER_TO_GRID
            )
//            binding.acCouplePath.startPvToInverterAnimation()
//            binding.acCouplePath.startInverterToCenterAnimation()
//            binding.acCouplePath.startCenterToTopAnimation()
//            binding.acCouplePath.startCenterToAcAnimation()
        }
        binding.cancel.setOnClickListener {
            binding.acCouplePath.endAllArrowAnimations()
//            binding.acCouplePath.endPvToInverterAnimation()
//            binding.acCouplePath.endInverterToCenterAnimation()
//            binding.acCouplePath.endCenterToTopAnimation()
//            binding.acCouplePath.endCenterToAcAnimation()
        }

        binding.cancelCenterToTop.setOnClickListener {
            binding.acCouplePath.endArrowAnimation(AcCoupleEsPathView.ArrowDirection.CENTER_TO_TOP)
        }
    }
}