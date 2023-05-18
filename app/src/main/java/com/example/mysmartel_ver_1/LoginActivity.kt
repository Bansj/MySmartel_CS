package com.example.mysmartel_ver_1

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import org.json.JSONObject
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.annotations.SerializedName

class LoginActivity : AppCompatActivity() {
    private lateinit var phoneNumber: EditText
    private lateinit var password: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        phoneNumber = findViewById(R.id.edit_id)
        password = findViewById(R.id.edit_password)
        val loginButton: Button = findViewById(R.id.btn_login)

        loginButton.setOnClickListener {
            val hpNo = phoneNumber.text.toString()
            val pwd = password.text.toString()
            val loadingDialog = showLoadingDialog()

            val params = mapOf(
                "hp_no" to hpNo,
                "pwd" to pwd,
                "resultCd" to "true"
            )

            Handler().postDelayed({
                login(params, loadingDialog)
            }, 2000)
        }
    }

    private fun login(params: Map<String, String>, dialog: AlertDialog) {
        val requestQueue = Volley.newRequestQueue(this)
        val url = "https://www.mysmartel.com/smartel/api_mysmartel_join.php"

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                Log.d("Login", "Login: $response")
                dialog.dismiss()
                val jsonObject = JSONObject(response)
                val resultCd = jsonObject.getString("resultCd")
                handleLoginResponse(resultCd, jsonObject)
            },
            Response.ErrorListener { error ->
                Log.e("Login", "Error: $error")
                dialog.dismiss()
            }) {
            override fun getParams(): Map<String, String> {
                return params
            }
        }

        requestQueue.add(stringRequest)
    }

    private fun handleLoginResponse(resultCd: String, jsonObject: JSONObject) {
        when (resultCd) {
            "join" -> {
                Log.w("Login", "스마텔 개통 고객인 아닌 경우")
            }
            "accnt" -> {
                Log.w("Login", "로그인 계정이 없는 경우 => 회원가입 필요")
            }
            "pwd" -> {
                Log.w("Login", "비밀번호가 일치하지 않는 경우 => 비밀번호 재입력")
            }
            "true" -> {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("user_info", jsonObject.toString())
                startActivity(intent)
                finish()
            }
        }
    }

    private fun showLoadingDialog(): AlertDialog {
        val builder = AlertDialog.Builder(this)
        builder.setView(R.layout.loading_dialog)
        builder.setCancelable(false)
        return builder.show()
    }
}

data class LoginResponse(
    @SerializedName("typ") val typ: String,
    @SerializedName("resultCd") val resultCd: String,
    @SerializedName("hp_no") val phoneNumber: String,
    @SerializedName("pwd") val password: String
)


















/*// 메인메뉴로 이동하는 로그인 버튼 클릭 이벤트
        val btnLogIn = findViewById<Button>(R.id.btn_login)
        btnLogIn.setOnClickListener {
            Toast.makeText(this, "해당 회선으로 로그인 합니다. ", Toast.LENGTH_SHORT).show()

            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // 선불폰 간편조회 액티비티로 이동하는 버튼 클릭 이벤트
        val btnFindPW = findViewById<Button>(R.id.btn_prepaidPhoneEasyCheck)
        btnFindPW.setOnClickListener {
            Toast.makeText(this, "선불폰 간편조회 페이지로 이동합니다. ", Toast.LENGTH_SHORT).show()

            var intent = Intent(this, PrepaidPhoneEasyCheck::class.java)
            startActivity(intent)
        }*/