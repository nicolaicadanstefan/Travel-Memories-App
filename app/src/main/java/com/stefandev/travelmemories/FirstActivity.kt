package com.stefandev.travelmemories

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper

@SuppressLint("CustomSplashScreen")
class FirstActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set your custom layout as the content view.
        setContentView(R.layout.first_activity)
        supportActionBar?.hide()

        // Use a Handler to add a delay before transitioning to the MainActivity.
        Handler(Looper.getMainLooper()).postDelayed({
            // Create an Intent to start your MainActivity
            val mainIntent = Intent(this@FirstActivity, MainActivity::class.java)
            startActivity(mainIntent)
            // Close the SplashScreenActivity so the user can't navigate back to it.
            finish()
        }, SPLASH_DISPLAY_LENGTH)
    }

    companion object {
        // Set the duration of the splash screen display in milliseconds.
        private const val SPLASH_DISPLAY_LENGTH = 2000L
    }
}
