package com.example.viewapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.viewapplication.databinding.ActivityEsPathBinding
import com.example.viewapplication.enumeration.EsAnimationDirection

class EsPathActivity : AppCompatActivity() {
    private val binding by lazy { ActivityEsPathBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val list1 = arrayListOf(
            EsAnimationDirection.PV_TO_INVERTER,
            EsAnimationDirection.GRID_TO_INVERTER,
            EsAnimationDirection.BATTERY_TO_INVERTER,
            EsAnimationDirection.INVERTER_TO_BACKUP_LOAD,
            EsAnimationDirection.GRID_TO_GRID_LOAD,
            EsAnimationDirection.INVERTER_TO_GRID_LOAD,
        )
        val list2 = arrayListOf(
            EsAnimationDirection.PV_TO_INVERTER,
            EsAnimationDirection.INVERTER_TO_GRID,
            EsAnimationDirection.INVERTER_TO_BATTERY,
            EsAnimationDirection.INVERTER_TO_BACKUP_LOAD
        )
        binding.start1.setOnClickListener {
            binding.path.isStationOffline = false
            binding.path.isPvOffline = false
            binding.path.startBallAnimations(list1)
        }
        binding.end1.setOnClickListener {
            binding.path.isStationOffline = true
            binding.path.endBallAnimations(list1)
        }
        binding.start2.setOnClickListener {
            binding.path.startBallAnimations(list2)
        }
        binding.end2.setOnClickListener {
            binding.path.endBallAnimations(list2)
        }
    }

}