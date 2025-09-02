package com.example.musicplayer

import android.content.Intent
import android.os.Bundle
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import androidx.appcompat.app.AppCompatActivity
import com.example.musicplayer.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Start spinning animation
        val rotate = RotateAnimation(
            0f, 360f,
            RotateAnimation.RELATIVE_TO_SELF, 0.5f,
            RotateAnimation.RELATIVE_TO_SELF, 0.5f
        )
        rotate.duration = 1000 // 1 rotation per second
        rotate.repeatCount = RotateAnimation.INFINITE
        rotate.interpolator = LinearInterpolator()
        binding.icon.startAnimation(rotate)

        // Load MainActivity in background
        Thread {
            // Simulate heavy work or just let MainActivity load
            // You can replace this with real initialization if needed
            runOnUiThread {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }.start()
    }
}
