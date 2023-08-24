package com.smartelmall.mysmartel_ver_1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class GoodbyeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goodbye)
        val serviceNum = intent.getStringExtra("phoneNumber")
        val custNm = intent.getStringExtra("custNm")
        Log.d("GoodbyeActivity------------------------get phoneNumber", "phoneNumber: $serviceNum")
        Log.d("GoodbyeActivity------------------------get custName", "custNm: $custNm")
    }
}