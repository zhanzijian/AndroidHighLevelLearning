package com.example.viewapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.viewapplication.databinding.ActivityEsPathBinding
import com.example.viewapplication.view.EsPathView

class EsPathActivity : AppCompatActivity() {
    private val binding by lazy { ActivityEsPathBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        binding.path.apply {
            viewTreeObserver.addOnGlobalLayoutListener {
                startLeftTopBallAnimator(EsPathView.BallDirection.LEFT_TOP_TO_CENTER)
                startRightTopBallAnimator(EsPathView.BallDirection.RIGHT_TOP_TO_CENTER)
                startLeftBottomBallAnimator(EsPathView.BallDirection.LEFT_BOTTOM_TO_CENTER)
                startRightBottomBallAnimator(EsPathView.BallDirection.RIGHT_BOTTOM_TO_CENTER)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}