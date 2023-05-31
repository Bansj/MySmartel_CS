package com.smartel.mysmartel_ver_1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.mysmartel_ver_1.R

class PrepaidAccount : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prepaid_account)

        // 선불폰 상세내역으로 가는 버튼 클릭 이벤트
        val btnPrepaidDetail = findViewById<TextView>(R.id.txt_prepaidDetail)
        btnPrepaidDetail.setOnClickListener {

            var intent = Intent(this, PrepaidDetail::class.java)
            startActivity(intent)
        }
    }
}