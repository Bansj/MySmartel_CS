package com.smartelmall.mysmartel_ver_1

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject
import android.provider.Settings
import androidx.appcompat.app.AlertDialog

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val sharedPreferences = getSharedPreferences("MyApp", Context.MODE_PRIVATE)
        val isFirstRun = sharedPreferences.getBoolean("isFirstRun", true)

        if (isFirstRun) {
            AlertDialog.Builder(this)
                .setTitle("알림 권한 설정")
                .setMessage("앱에서 제공하는 서비스를 이용하기 위해서는 알림 권한이 필요합니다. 설정하시겠습니까?")
                .setPositiveButton("예") { _, _ ->
                    // 사용자가 '예'를 선택했을 때의 동작.
                    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                        putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                    }
                    startActivity(intent)

                    goToNextActivity()
                }
                .setNegativeButton("아니오") { _, _ ->
                    // 사용자가 '아니오'를 선택했을 때의 동작.
                    goToNextActivity()
                }
                .show()

            sharedPreferences.edit().putBoolean("isFirstRun", false).apply()
        } else {
            goToNextActivity()
        }
    }

    private fun goToNextActivity() {
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }, 1500) // Delay for 1.5 seconds (1500 milliseconds).
    }
}