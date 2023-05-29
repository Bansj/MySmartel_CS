package com.example.mysmartel_ver_1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.security.cert.X509Certificate
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class LoginActivity : AppCompatActivity() {
    private lateinit var phoneNumberEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var loadingDialog: AlertDialog

    private lateinit var requestQueue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        phoneNumberEditText = findViewById(R.id.edit_id)
        passwordEditText = findViewById(R.id.edit_password)
        loginButton = findViewById(R.id.btn_login)

        loadingDialog = createLoadingDialog()


        requestQueue = Volley.newRequestQueue(this, ignoreSslErrorHurlStack()) // `ignoreSslErrorHurlStack` is used

        loginButton.setOnClickListener { loginUser() }
    }

    private fun createLoadingDialog(): AlertDialog {
        val dialogBuilder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.loading_dialog, null)
        val progressBar = dialogView.findViewById<ProgressBar>(R.id.loading_spinner)
        progressBar.indeterminateDrawable.setColorFilter(ContextCompat.getColor(this, R.color.orange), android.graphics.PorterDuff.Mode.SRC_IN)
        dialogBuilder.setView(dialogView)
        dialogBuilder.setView(dialogView)
        dialogBuilder.setCancelable(false)
        return dialogBuilder.create()
    }

    private fun loginUser() {
        val phoneNumber = phoneNumberEditText.text.toString()
        val password = passwordEditText.text.toString()

        val params = JSONObject()
        params.put("log_id", phoneNumber)
        params.put("log_pwd", password)

        showLoadingDialog()

        val url = "https://www.mysmartel.com/smartel/api_mysmartel_login.php"
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, params,
            Response.Listener { response ->
                hideLoadingDialog()
                handleLoginResponse(response)
            },
            Response.ErrorListener { error ->
                hideLoadingDialog()
                showErrorDialog("An error occurred: ${error.message}")
            }
        )
        requestQueue.add(jsonObjectRequest)
    }

    private fun handleLoginResponse(response: JSONObject) {
        try {
            // Parse the response and process it
            val loginResult = response.getString("resultCd")

            if (loginResult == "true") {
                // Login successful, navigate to MainActivity
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish() // 종료하여 현재 로그인 액티비티를 백스택에서 제거
            } else {
                // Handle other cases and show appropriate AlertDialogs
                showErrorDialog("Login failed")
            }
        } catch (e: JSONException) {
            showErrorDialog("Failed to parse login response")
        }
    }

    private fun showLoadingDialog() {
        loadingDialog.show()
    }

    private fun hideLoadingDialog() {
        loadingDialog.dismiss()
    }

    private fun showErrorDialog(message: String) {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage(message)
            .setCancelable(false)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }

        val alert = dialogBuilder.create()
        alert.show()
    }

    private fun ignoreSslErrorHurlStack(): HurlStack {
        return object : HurlStack() {
            override fun createConnection(url: URL): HttpURLConnection {
                val connection = super.createConnection(url)
                if (connection is HttpsURLConnection) {
                    connection.setHostnameVerifier { _, _ -> true }
                    val trustAllCerts: Array<TrustManager> = arrayOf(object : X509TrustManager {
                        override fun getAcceptedIssuers(): Array<X509Certificate> {
                            return arrayOf()
                        }

                        override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}

                        override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
                    })

                    try {
                        val sc = SSLContext.getInstance("SSL")
                        sc.init(null, trustAllCerts, java.security.SecureRandom())
                        connection.sslSocketFactory = sc.socketFactory
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                return connection
            }
        }
    }
}








//class LoginActivity : AppCompatActivity() {
//
//    private lateinit var etPhone: EditText
//    private lateinit var etPassword: EditText
//    private lateinit var btnLogin: Button
//    private lateinit var progressDialog: ProgressDialog
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_login)
//
//        etPhone = findViewById(R.id.edit_id)
//        etPassword = findViewById(R.id.edit_password)
//        btnLogin = findViewById(R.id.btn_login)
//
//
//
//        btnLogin.setOnClickListener {
//            progressDialog = ProgressDialog(this)
//            progressDialog.setMessage("로그인 중...")
//            progressDialog.show()
//
//            val request = object : StringRequest(
//                Request.Method.POST,
//                "https://www.mysmartel.com/smartel/api_mysmartel_join.php",
//                { response ->
//                    progressDialog.dismiss()
//
//                    Log.d("LoginActivity", response)
//
//                    val responseJson = JSONObject(response)
//                    val resultCd = responseJson.getString("resultCd")
//
//                    if (resultCd == "true") {
//                        // 로그인 성공
//                        startActivity(Intent(this, MainActivity::class.java))
//                    } else {
//                        // 로그인 실패
//                        AlertDialog.Builder(this)
//                            .setTitle("로그인 실패")
//                            .setMessage(responseJson.getString("message"))
//                            .setPositiveButton("확인", null)
//                            .show()
//                    }
//                },
//                { error ->
//                    progressDialog.dismiss()
//
//                    AlertDialog.Builder(this)
//                        .setTitle("로그인 오류")
//                        .setMessage(error.message)
//                        .setPositiveButton("확인", null)
//                        .show()
//                }
//            ) {
//                override fun getParams(): Map<String, String> {
//                    val params = HashMap<String, String>()
//                    params["log_id"] = etPhone.text.toString()
//                    params["log_pwd"] = etPassword.text.toString()
//
//                    return params
//                }
//            }
//
//            val queue = Volley.newRequestQueue(this)
//            queue.add(request)
//        }
//    }
//}


































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