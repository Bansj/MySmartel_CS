package com.example.mysmartel_ver_1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast

class Setting : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)


        // 메인메뉴로 이동하는 로그인 버튼 클릭 이벤트
        val btnQuit = findViewById<ImageButton>(R.id.btn_quit)
        btnQuit.setOnClickListener {

            var intent = Intent(this, MenuFragment::class.java)
            startActivity(intent)
        }

    }
}