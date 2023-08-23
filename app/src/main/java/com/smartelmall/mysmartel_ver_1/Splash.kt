package com.smartelmall.mysmartel_ver_1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class Splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

//        val sharedPrefs = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
//        val autoLoginEnabled = sharedPrefs.getBoolean("autoLogin", false)
//
//        Handler().postDelayed({
//            val intent = if (autoLoginEnabled) {
//                Intent(this, MainActivity::class.java) // Replace with your MyInfoActivity
//            } else {
//                Intent(this, LoginActivity::class.java)
//            }
//            startActivity(intent)
//            finish()
//        }, 2000)
        // Add a delay to show the splash screen
        Handler().postDelayed({
            // Check if the user is already logged in
            val isLoggedIn = checkIfUserIsLoggedIn()

            // If the user is logged in, navigate to MainActivity
            // Otherwise, navigate to LoginActivity
            val destinationActivity = if (isLoggedIn) {
                MainActivity::class.java
            } else {
                LoginActivity::class.java
            }

            val intent = Intent(this@Splash, destinationActivity)
            startActivity(intent)
            finish()
        }, SPLASH_DELAY)
    }

    private fun checkIfUserIsLoggedIn(): Boolean {
        // Implement your logic to check if the user is already logged in
        // Return true if the user is logged in, false otherwise
        // For example:
        // val loggedInUser = getLoggedInUser()
        // return loggedInUser != null
        return false
    }

    companion object {
        private const val SPLASH_DELAY = 2000L // 2 seconds delay

    }
}
