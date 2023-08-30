package com.smartelmall.mysmartel_ver_1.NewPW

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import com.smartelmall.mysmartel_ver_1.R
import com.smartelmall.mysmartel_ver_1.SettingFragment
import okhttp3.*
import java.io.IOException

class IdentificationSelfActivity : AppCompatActivity() {

    private lateinit var phoneNumber: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_identification_self)

        phoneNumber = intent.getStringExtra("phoneNumber") ?: getPhoneNumberFromSettingFragment()
        Log.d("IdentificationSelfActivity","------------------check phoneNumber: $phoneNumber---------------------------\n")

        val button = findViewById<Button>(R.id.btn_identification)
        button.setOnClickListener {
            sendTextMessage()
        }

        val btnBack = findViewById<ImageButton>(R.id.btn_back)
        btnBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun sendTextMessage() {
        val phoneNumber = getPhoneNumberFromSettingFragment() // Replace with actual function
        val certificationNumber = generateCertificationNumber()
        Log.d("-----------------------------IdentificationSelf", "Generated certification number: $certificationNumber----------------------")
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
                        Log.d("IdentificationSelf", "SMS sent successfully. Response: $responseBody")
                        Toast.makeText(this@IdentificationSelfActivity, responseBody, Toast.LENGTH_SHORT).show()

                        // Navigate to SendCheckNumberActivity.kt and pass the generated certification number.
                        Intent(this@IdentificationSelfActivity, CheckNumberChangePasswordActivity::class.java).apply{
                            putExtra("CERTIFICATION_NUMBER", certificationNumber)
                            putExtra("phoneNumber",phoneNumber)
                            Log.d("IdentificationSelfActivity","-----------------send certificationNumber: $certificationNumber---------")
                            Log.d("IdentificationSelfActivity","-----------------send phoneNumber: $phoneNumber---------")
                            startActivity(this)
                        }
                    }
                } else {
                    // Handle failure: Show a toast with the failure message
                    runOnUiThread {
                        Log.d("IdentificationSelf", "SMS sending failed. Response: $responseBody")
                        Toast.makeText(this@IdentificationSelfActivity, "Please check your mobile phone number.", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                // Handle failure: Show a toast indicating a network error
                runOnUiThread {
                    Log.e("IdentificationSelf", "Network error occurred:${e.message}")
                    Toast.makeText(this@IdentificationSelfActivity,"Network error occurred.",Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun getPhoneNumberFromSettingFragment(): String {
        return intent.getStringExtra("phoneNumber") ?: "null"
    }

    private fun generateCertificationNumber(): String {
        return (100000..999999).random().toString()
    }
}

