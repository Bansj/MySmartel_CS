package com.smartel.mysmartel_ver_1

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.mysmartel_ver_1.R

class Splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val sharedPrefs = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val autoLoginEnabled = sharedPrefs.getBoolean("autoLogin", false)

        Handler().postDelayed({
            val intent = if (autoLoginEnabled) {
                Intent(this, MainActivity::class.java) // Replace with your MyInfoActivity
            } else {
                Intent(this, LoginActivity::class.java)
            }
            startActivity(intent)
            finish()
        }, 2000)
    }
}
