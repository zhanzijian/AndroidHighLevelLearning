package com.example.viewapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.viewapplication.databinding.ActivityAcCoupleEsAnimationBinding
import com.example.viewapplication.databinding.ActivityMainBinding

class AcCoupleEsAnimationActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityAcCoupleEsAnimationBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


    }
}