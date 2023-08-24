package com.smartelmall.mysmartel_ver_1

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
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

    private lateinit var signUpButton: Button

    private lateinit var findPW: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        phoneNumberEditText = findViewById(R.id.edit_id)
        passwordEditText = findViewById(R.id.edit_password)
        loginButton = findViewById(R.id.btn_login)
        findPW = findViewById(R.id.txt_findPW)

        loadingDialog = createLoadingDialog()

        requestQueue = Volley.newRequestQueue(this, ignoreSslErrorHurlStack())

        loginButton.setOnClickListener {
            Log.d(
                "LoginActivity",
                "============================== Login button clicked =============================="
            )
            loginUser()
        }

        signUpButton = findViewById(R.id.btn_signUp)
        signUpButton.setOnClickListener {
            val message = "회원가입 완료후에 로그인하기 버튼을 클릭하여 주십시오. "
            showAlertDialog(message)
        }

        // 추후 업데이트 후에 웹뷰 방식으로 비밀번호 찾기 기능 구현
        val webView = findViewById<WebView>(R.id.webviewFindPW)
        webView.webViewClient = WebViewClient()

        findPW.setOnClickListener {
            val url = "https://www.mysmartel.com/page/user_pw.php"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }
    }

    private fun showAlertDialog(message: String) {
        val alertDialog = AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton("확인") { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
                navigateToSignupActivity()
            }
            .create()

        // AlertDialog 확인버튼 글자색 변경 코드
        alertDialog.setOnShowListener {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.orange))
        }
        alertDialog.show()
    }
    private fun navigateToSignupActivity() {
        val intent = Intent(this, WebViewActivity::class.java)
        startActivity(intent)
    }
    private fun createLoadingDialog(): AlertDialog {
        val dialogBuilder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.loading_dialog, null)
        val progressBar = dialogView.findViewById<ProgressBar>(R.id.loading_spinner)
        progressBar.indeterminateDrawable.setColorFilter(
            ContextCompat.getColor(this, R.color.orange),
            android.graphics.PorterDuff.Mode.SRC_IN
        )
        dialogBuilder.setView(dialogView)
        dialogBuilder.setCancelable(false)
        return dialogBuilder.create()
    }
    private fun loginUser() {
        val phoneNumber = phoneNumberEditText.text.toString()
        val password = passwordEditText.text.toString()

        Log.d("LoginActivity",
            "--------------------Login User - Phone number: $phoneNumber, Password: $password---------------------")

        val loginParams = JSONObject()
        loginParams.put("log_id", phoneNumber)
        loginParams.put("log_pwd", password)

        showLoadingDialog()

        val loginUrl = "https://www.mysmartel.com/smartel/api_mysmartel_login.php"
        val loginRequest = JsonObjectRequest(
            Request.Method.POST, loginUrl, loginParams,
            Response.Listener { response ->
                handleLoginResponse(response)
            },
            Response.ErrorListener { error ->
                hideLoadingDialog()
                showErrorDialog("An error occurred: ${error.message}")
            }
        )
        requestQueue.add(loginRequest)

        // Save the phoneNumber in SharedPreferences
        val sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.putString("phoneNumber", phoneNumber)
        editor.apply()
    }

    private fun handleLoginResponse(response: JSONObject) {
        try {
            val loginResult = response.getString("resultCd")

            if (loginResult == "true" || loginResult == "1818") {
                val phoneNumber = phoneNumberEditText.text.toString()
                Log.d("LoginActivity",
                    "--------------------Login successful - Phone number: $phoneNumber--------------------")
                fetchUserInfo(phoneNumber)
            } else {
                hideLoadingDialog()
                showErrorDialog("Login failed")
            }
        } catch (e: JSONException) {
            hideLoadingDialog()
            showErrorDialog("Failed to parse login response")
        }
    }

    private fun fetchUserInfo(phoneNumber: String) {
        val infoParams = JSONObject()
        infoParams.put("serviceNum", phoneNumber)

        val infoUrl = "https://www.mysmartel.com/smartel/api_getinfo.php"
        val infoRequest = JsonObjectRequest(
            Request.Method.POST, infoUrl, infoParams,
            Response.Listener { response ->
                handleInfoResponse(response)
            },
            Response.ErrorListener { error ->
                hideLoadingDialog()
                showErrorDialog("An error occurred while fetching user info: ${error.message}")
            }
        )
        requestQueue.add(infoRequest)
    }

    private fun handleInfoResponse(response: JSONObject) {
        hideLoadingDialog()
        try {
            val telecom = response.getString("telecom")
            val custName = response.getString("custNm")
            val serviceAcct = response.getString("serviceAcct")
            val phoneNumber = phoneNumberEditText.text.toString()
            Log.d("\nLoginActivity - getString",
                "-----------------User Info - Telecom: $telecom, CustName: $custName, ServiceAccount: $serviceAcct, PhoneNumber: $phoneNumber-----------------------")

            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("custName", custName)
            intent.putExtra("PhoneNumber", phoneNumber)
            intent.putExtra("Telecom", telecom)
            intent.putExtra("serviceAcct", serviceAcct)
            startActivity(intent)
            Log.d("\nLoginActivity - putExtra",
                "-----------------User Info - Telecom: $telecom, CustName: $custName, ServiceAccount: $serviceAcct, PhoneNumber: $phoneNumber-----------------------")

        } catch (e: JSONException) {
            showErrorDialog("Failed to parse user info response")
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
    // https 통신 허용
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
