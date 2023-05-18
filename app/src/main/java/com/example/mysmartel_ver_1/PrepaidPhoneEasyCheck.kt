package com.example.mysmartel_ver_1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast

class PrepaidPhoneEasyCheck : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prepaid_phone_easy_check)

        // 다시 로그인 화면으로 돌아가는 버튼 클릭 이벤트
        val btnBack = findViewById<ImageButton>(R.id.btn_back)
        btnBack.setOnClickListener {
            Toast.makeText(this, "로그인 화면으로 돌아갑니다. ", Toast.LENGTH_SHORT).show()

            var intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // 계좌정보 화면으로 가는 버튼 클릭 이벤트
        val btnEasyCheck = findViewById<Button>(R.id.btn_easyCheck)
        btnEasyCheck.setOnClickListener {
            Toast.makeText(this, "선불폰 계좌조회 페이지로 이동합니다. ", Toast.LENGTH_SHORT).show()

            var intent = Intent(this, PagerActivity::class.java)
            startActivity(intent)
        }

    }
}