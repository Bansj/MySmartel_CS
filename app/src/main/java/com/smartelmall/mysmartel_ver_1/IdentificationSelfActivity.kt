package com.smartelmall.mysmartel_ver_1

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import okhttp3.*
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class IdentificationSelfActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_identification_self)

        val button = findViewById<Button>(R.id.btn_identification)
        button.setOnClickListener {
            sendTextMessage()
        }
    }

    private fun sendTextMessage() {
        val phoneNumber = getPhoneNumberFromSettingFragment() // Replace with actual function
        val certificationNumber = generateCertificationNumber()
        val msgType = 1

        // Construct the URL
        val url = "https://www.smartelmall.com/site/common/Certification2.asp"

        // Prepare the request parameters
        val requestBody = FormBody.Builder()
            .add("ctn", phoneNumber)
            .add("certi", certificationNumber)
            .add("msgType", msgType.toString())
            .build()

        // Create and execute the HTTP request
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                if (response.isSuccessful) {
                    // Handle success: Show a toast with the success message
                    runOnUiThread {
                        Toast.makeText(this@IdentificationSelfActivity, responseBody, Toast.LENGTH_SHORT).show()
                        // Navigate to SendCheckNumberActivity.kt
                        startActivity(Intent(this@IdentificationSelfActivity, SendCheckNumberActivity::class.java))
                    }
                } else {
                    // Handle failure: Show a toast with the failure message
                    runOnUiThread {
                        Toast.makeText(this@IdentificationSelfActivity, "Please check your mobile phone number.", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                // Handle failure: Show a toast indicating a network error
                runOnUiThread {
                    Toast.makeText(this@IdentificationSelfActivity, "Network error occurred.", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    // Replace with actual implementation
    private fun getPhoneNumberFromSettingFragment(): String {
        // Retrieve and return the phone number from SettingFragment
        return "01075244523"
    }

    private fun generateCertificationNumber(): String {
        // Implement a function to generate a random certification number
        return (100000..999999).random().toString()
    }
}