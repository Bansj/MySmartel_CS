package com.smartelmall.mysmartel_ver_1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast

class CheckNumberDeleteAccountActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_number_delete_account)


        val editText = findViewById<EditText>(R.id.edit_checkNumberDeleteAccount)
        val btnCheck = findViewById<Button>(R.id.btn_checkSendNumberDeleteAccount)

        val correctNumber = intent.getStringExtra("CERTIFICATION_NUMBER")
        val serviceNum = intent.getStringExtra("phoneNumber")
        val custNm = intent.getStringExtra("custNm")
        Log.d("CheckNumberDeleteAccountActivity------------------------get correctNumber", "RandomNumber: $correctNumber")
        Log.d("CheckNumberDeleteAccountActivity------------------------get phoneNumber", "phoneNumber: $serviceNum")
        Log.d("CheckNumberDeleteAccountActivity------------------------get custName", "custNm: $custNm")

        btnCheck.setOnClickListener {
            val userEnteredNumber = editText.text.toString()

            // Log user-entered and correct verification numbers
            Log.d(
                "-----------------Verification",
                "User-entered: $userEnteredNumber, Correct: $correctNumber"
            )

            if (userEnteredNumber == correctNumber) {
                // Correct verification number entered, navigate to NewPasswordActivity
                Log.d("Verification", "Verification successful")
                // Pass phoneNumber to NewPasswordActivity along with the intent
                Intent(
                    this@CheckNumberDeleteAccountActivity,
                    GoodbyeActivity::class.java
                ).apply {
                    putExtra("phoneNumber", serviceNum)
                    putExtra("custNm",custNm)
                    Log.d("-------------CheckNumberDeleteAccountActivity", "send phoneNumber: $serviceNum-------")
                    Log.d("-------------CheckNumberDeleteAccountActivity", "send custNm: $custNm-------")
                    startActivity(this)
                }
            } else {
                // Incorrect verification number, show a toast
                Log.d("Verification", "Verification failed")
                Toast.makeText(
                    this@CheckNumberDeleteAccountActivity,
                    "Incorrect verification number",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        // 뒤로가기 버튼 클릭 이벤트
        val btnBack = findViewById<ImageButton>(R.id.btn_back)
        btnBack.setOnClickListener {
            onBackPressed()
        }

    }
}