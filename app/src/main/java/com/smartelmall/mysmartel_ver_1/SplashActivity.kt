package com.smartelmall.mysmartel_ver_1

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

        val phoneNumber = sharedPrefs.getString("phoneNumber", null)

        if (phoneNumber != null) {
            val intent = Intent(this@SplashActivity, MainActivity::class.java).apply{
                putExtra("PhoneNumber", phoneNumber )
                putExtra("custName" ,sharedPrefs.getString( "custName",""))
                putExtra( "Telecom" ,sharedPrefs.getString( "Telecom",""))
                putExtra( "serviceAcct" ,sharedPrefs.getString( "serviceAcct",""))
            }
            startActivity(intent)
            finish()
        } else {
            startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            finish()
        }
    }
}

