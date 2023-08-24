package com.smartelmall.mysmartel_ver_1

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException

class GoodbyeActivity : AppCompatActivity() {

    private lateinit var btnGoodbye: Button

    private val TAG = "GoodbyeActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goodbye)
        val serviceNum = intent.getStringExtra("phoneNumber")
        val custNm = intent.getStringExtra("custNm")
        Log.d("GoodbyeActivity------------------------get phoneNumber", "phoneNumber: $serviceNum")
        Log.d("GoodbyeActivity------------------------get custName", "custNm: $custNm")

        // btn_goodbye 버튼 클릭 시 이벤트 처리
        btnGoodbye = findViewById(R.id.btn_goodbye)
        btnGoodbye.setOnClickListener {
            // API 호출 함수 호출
            requestDeleteAccount(serviceNum, custNm)
        }
    }

    // API 호출 함수
    private fun requestDeleteAccount(serviceNum: String?, custNm: String?) {
        val url = "https://www.mysmartel.com/smartel/delUser.php"
        val requestBody = JSONObject()
        requestBody.put("serviceNum", serviceNum)
        requestBody.put("custNm", custNm)

        val client = OkHttpClient()

        val request: Request = Request.Builder()
            .url(url)
            .delete(RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), requestBody.toString()))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                val jsonObject = JSONObject(responseData)
                val affectedRows = jsonObject.getInt("affectedRows")

                // "스마텔 계정이 탈퇴되었습니다." 알럿 메시지 띄우고 어플 종료
                runOnUiThread {
                    AlertDialog.Builder(this@GoodbyeActivity)
                        .setMessage("스마텔 계정이 탈퇴되었습니다.")
                        .setPositiveButton("확인") { _, _ ->
                            finish()
                        }
                        .setCancelable(false)
                        .show()
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Failed to delete account", e)
            }
        })
    }
}

