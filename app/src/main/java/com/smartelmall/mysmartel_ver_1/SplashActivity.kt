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

    private val PREFS_NAME = "MyPrefs"
    private val IS_FIRST_RUN = "IsFirstRun"
    private var isDialogShown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val settings = getSharedPreferences(PREFS_NAME, 0)
        if (settings.getBoolean(IS_FIRST_RUN, true)) {
            // Show alert dialog here.
            AlertDialog.Builder(this)
                .setTitle("Notification Permission")
                .setMessage("Do you want to enable notification?")
                .setPositiveButton("Yes") { _, _ ->
                    // Go to Notification Settings
                    startActivity(Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                        putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                    })
                    isDialogShown = true
                }
                .setNegativeButton("No") { _, _ ->
                    moveToLoginActivity()
                }
                .setOnDismissListener {
                    settings.edit().putBoolean(IS_FIRST_RUN, false).apply()

                    // If SplashActivity is still visible after returning from settings screen,
                    // move to LoginActivity.

                    if (!isFinishing && !isDialogShown) moveToLoginActivity()
                }
                .show()
        } else {
            moveToLoginActivity()
        }
    }

    override fun onResume() {
        super.onResume()

        if (isDialogShown) {
            moveToLoginActivity()
            isDialogShown = false
        }
    }

    private fun moveToLoginActivity() {
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }, 1500) // Delay for 1.5 seconds (1500 milliseconds)
    }
}
