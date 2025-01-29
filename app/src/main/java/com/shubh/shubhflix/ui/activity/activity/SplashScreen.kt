package com.shubh.shubhflix.ui.activity.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.shubh.shubhflix.R
import com.shubh.shubhflix.databinding.ActivitySplashScreenBinding
import com.shubh.shubhflix.ui.activity.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SplashScreen : AppCompatActivity() {
    lateinit var binding: ActivitySplashScreenBinding

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        hideSystemBars()

        CoroutineScope(Dispatchers.Main).launch {
            delay(3000)
            animateImageView()
//            startActivity(Intent(this@SplashScreen, MainActivity::class.java))
//            finish() // Close splash screen
        }

        //  animateImageView()
    }


    private fun animateImageView() {

        // Load zoom-out animation
        val zoomOutAnimation = AnimationUtils.loadAnimation(this, R.anim.zoom_out)

        // Start animation
        binding.ivApplogo.startAnimation(zoomOutAnimation)

        // Set listener to start next activity when animation completes
        zoomOutAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                binding.ivApplogo.visibility = View.GONE
                // Move to next activity after animation ends
                startActivity(Intent(this@SplashScreen, MainActivity::class.java))
                finish() // Close splash screen
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun hideSystemBars() {
        // Hide the status bar using WindowInsetsController
        window.insetsController?.apply {
            hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
            systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}