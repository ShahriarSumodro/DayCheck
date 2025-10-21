package com.example.daycheck

package com.example.daycheck

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

// SplashActivity displays the app name for a short duration before launching MainActivity.
// It uses a simple handler to delay the transition for a polished start experience.
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)

        // Delay for 2 seconds to show splash
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 2000)
    }
}