package com.smartelmall.mysmartel_ver_1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class SendCheckNumberActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_check_number)

        val editText = findViewById<EditText>(R.id.edit_checkNumber)
        val btnCheck = findViewById<Button>(R.id.btn_checkSendNumber)

        val correctNumber = intent.getStringExtra("CERTIFICATION_NUMBER")
        val phoneNumber = intent.getStringExtra("phoneNumber")
        Log.d("SendCheckNumberActivity------------------------get correctNumber","RandomNumber: $correctNumber")
        Log.d("SendCheckNumberActivity------------------------get phoneNumber","phoneNumber: $phoneNumber")

        btnCheck.setOnClickListener {
            val userEnteredNumber = editText.text.toString()

            // Log user-entered and correct verification numbers
            Log.d("-----------------Verification", "User-entered: $userEnteredNumber, Correct: $correctNumber")

            if (userEnteredNumber == correctNumber) {
                // Correct verification number entered, navigate to NewPasswordActivity
                Log.d("Verification", "Verification successful")
                // Pass phoneNumber to NewPasswordActivity along with the intent
                Intent(this@SendCheckNumberActivity, NewPasswordActivity::class.java).apply {
                    putExtra("phoneNumber", phoneNumber)
                    Log.d("-------------CheckSendNumber","send phoneNumber: $phoneNumber-------")
                    startActivity(this)
                }
            } else {
                // Incorrect verification number, show a toast
                Log.d("Verification", "Verification failed")
                Toast.makeText(this@SendCheckNumberActivity, "Incorrect verification number", Toast.LENGTH_SHORT).show()
            }
        }
    }
}


