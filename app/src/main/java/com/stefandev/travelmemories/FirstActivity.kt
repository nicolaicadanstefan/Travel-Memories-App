package com.stefandev.travelmemories

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast

@SuppressLint("CustomSplashScreen")
class FirstActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set your custom layout as the content view.
        setContentView(R.layout.first_activity)
        supportActionBar?.hide()

        val imageView = findViewById<ImageView>(R.id.app_logo)
        val startButton = findViewById<Button>(R.id.startButton)
        val planeImage = findViewById<ImageView>(R.id.plane)
        val camelImage = findViewById<ImageView>(R.id.camel)
        val pyramidImage = findViewById<ImageView>(R.id.pyramid)
        val crocodileImage = findViewById<ImageView>(R.id.crocodile)
        val palmImage = findViewById<ImageView>(R.id.palm)

        // Delay before starting the animation
        Handler(Looper.getMainLooper()).postDelayed({
            // Move the image up
            imageView.animate()
                .translationY(-500f)
                .scaleX(0.6f) // Shrink the logo to 60% of its original size
                .scaleY(0.6f) // Shrink the logo to 60% of its original size
                .setDuration(500)
                .withEndAction {
                    Log.d("Button Animation", "Animation completed!") // TESTING BUTTON APPEARANCE
                    // Start button animation
                    startButton.alpha = 0f // button is fully transparent
                    startButton.visibility = View.VISIBLE
                    startButton.animate()
                        .alpha(1.0f) // full opaque
                        .setDuration(600)
                        .start()

                    // Plane image animation
                    planeImage.alpha = 0f
                    planeImage.visibility = View.VISIBLE
                    planeImage.animate()
                        .alpha(1.0f) // full opaque
                        .setDuration(600)
                        .start()

                    // Camel image animation
                    camelImage.alpha = 0f
                    camelImage.visibility = View.VISIBLE
                    camelImage.animate()
                        .alpha(1.0f) // full opaque
                        .setDuration(600)
                        .start()

                    // Pyramid image animation
                    pyramidImage.alpha = 0f
                    pyramidImage.visibility = View.VISIBLE
                    pyramidImage.animate()
                        .alpha(1.0f) // full opaque
                        .setDuration(600)
                        .start()

                    // Crocodile image animation
                    crocodileImage.alpha = 0f
                    crocodileImage.visibility = View.VISIBLE
                    crocodileImage.animate()
                        .alpha(1.0f) // full opaque
                        .setDuration(600)
                        .start()

                    // Palm image animation
                    palmImage.alpha = 0f
                    palmImage.visibility = View.VISIBLE
                    palmImage.animate()
                        .alpha(1.0f) // full opaque
                        .setDuration(600)
                        .start()
                }
                .start()
        }, splashDisplayLength)

        startButton.setOnClickListener {
            val intent = Intent(this@FirstActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }

    companion object {
        // The duration of the splash screen display in milliseconds.
        private const val splashDisplayLength = 2000L
    }
}
