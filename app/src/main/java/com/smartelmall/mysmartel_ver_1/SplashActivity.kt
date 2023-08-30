package com.smartelmall.mysmartel_ver_1

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.SharedPreferences
import android.app.AlertDialog
import android.os.Handler
import android.util.Log
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.security.cert.X509Certificate
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

//import okhttp3.Request
//import okhttp3.Response

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val phoneNumber= sharedPrefs.getString("phoneNumber","")
        val password= sharedPrefs.getString("password","")
        val autoLoginEnabled= sharedPrefs.getBoolean("autoLoginEnabled",false)

        if(autoLoginEnabled && !phoneNumber.isNullOrEmpty() && !password.isNullOrEmpty()){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }else{
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}


