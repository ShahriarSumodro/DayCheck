package com.daycheck.app.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.daycheck.app.R
import com.daycheck.app.databinding.SplashScreenBinding

/**
 * Splash screen activity that shows the app logo and name.
 * Displays for a short duration before navigating to the main activity.
 */
class SplashActivity : AppCompatActivity() {
    
    private lateinit var binding: SplashScreenBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Show splash screen for 2 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 2000)
    }
}
