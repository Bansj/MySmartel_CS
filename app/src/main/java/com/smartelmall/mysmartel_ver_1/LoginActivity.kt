package com.smartelmall.mysmartel_ver_1

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ParseException
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.smartelmall.mysmartel_ver_1.TestUser.UserInfo
import org.json.JSONException
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.security.cert.X509Certificate
import java.text.SimpleDateFormat
import java.util.*
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

    private lateinit var autoLoginSwitch: Switch // 자동로그인 스위치

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        phoneNumberEditText = findViewById(R.id.edit_id)
        passwordEditText = findViewById(R.id.edit_password)
        loginButton = findViewById(R.id.btn_login)
        findPW = findViewById(R.id.txt_findPW)

        loadingDialog = createLoadingDialog()

        requestQueue = Volley.newRequestQueue(this, ignoreSslErrorHurlStack())

        autoLoginSwitch = findViewById(R.id.switch_autoLogin)


        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        if (sharedPrefs.getBoolean("autoLogin", false)) {
            phoneNumberEditText.setText(sharedPrefs.getString("phoneNumber", ""))
            passwordEditText.setText(sharedPrefs.getString("password", ""))
            loginUser()
        }

        loginButton.setOnClickListener { // 로그인 버튼

            Log.d("LoginActivity", "============================== Login button clicked ==============================")

            loginUser()

            if (autoLoginSwitch.isChecked) {
                with(sharedPrefs.edit()) {
                    putString("phoneNumber", phoneNumberEditText.text.toString())
                    putString("password", passwordEditText.text.toString())
                    putBoolean("autoLogin", true)
                    apply()
                }
            } else {
                with(sharedPrefs.edit()) {
                    remove("phoneNumber")
                    remove("password")
                    putBoolean("autoLogin", false)
                    apply()
                }
            }
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

    private fun navigateToSignupActivity() { // 회원가입 버튼 클릭 이벤트
        val intent = Intent(this, WebViewActivity::class.java)
        startActivity(intent)
    }


    private var testPhoneNumber: String? = // 테스트계정 번호 생성
        // 아래에는 테스트 로그인할 번호 기입
        "01021571133"// lg
    private fun loginUser() {
        val phoneNumber = phoneNumberEditText.text.toString()
        val password = passwordEditText.text.toString()

        if (phoneNumber == "123" && password == "123") { // 테스트 계정
            handleTestLogin()
            fetchUserInfo(testPhoneNumber!!)
            return
        }

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
        loginRequest.tag = "LOGIN_REQUEST_TAG"
        requestQueue.add(loginRequest)

        // Save the phoneNumber in SharedPreferences
        val sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.putString("phoneNumber", phoneNumber)
        editor.apply()
    }

    private fun handleTestLogin() {
        val response = JSONObject().apply {
            put("resultCd", "true") // 로그인 로직 강제 통과
            put("typ", "pwd")       // 로그인 로직 강제 통과
            put("telecom", "LGT")
            put("serviceAcct", "500279120526")
            put("custNm", "김지은")
            put("phoneNumber", "01033504523")
            //put("kind", "후불")
        }
        //handleLoginResponse(response)
        handleInfoResponse(response)
    }

    private fun handleLoginResponse(response: JSONObject) {
        try {
            val loginResult = response.getString("resultCd")
            val type = response.getString("typ")

            if (loginResult == "true") {
                when (type) {
                    "pwd" -> {
                        val phoneNumber = phoneNumberEditText.text.toString()
                        Log.d("LoginActivity",
                            "--------------------Login successful - Phone number: $phoneNumber--------------------")
                        fetchUserInfo(phoneNumber)
                    }
                    else -> showErrorDialog("로그인에 실패하였습니다.")
                }
            } else if (loginResult == "1818") {
                // Extract the start and end time from the response
                val startTime = response.getString("startTime")
                val endTime = response.getString("endTime")

                // Parse the start and end time to Date objects
                val parser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

                var parsedStartTime: Date? = null
                var parsedEndTime: Date? = null

                try {
                    parsedStartTime = parser.parse(startTime)
                    parsedEndTime = parser.parse(endTime)

                    if (parsedStartTime != null && parsedEndTime != null) {
                        // If start date and end date are the same, only show the start date.
                        if (parsedStartTime.day == parsedEndTime.day) {
                            val formatterTimeOnly = SimpleDateFormat("HH:mm", Locale.getDefault())
                            showExitDialog(
                                "알림\n",
                                "죄송합니다.\n서버 점검 중입니다.\n\n${SimpleDateFormat("M/d HH:mm", Locale.getDefault()).format(parsedStartTime)} ~ ${formatterTimeOnly.format(parsedEndTime)}",
                                "OK"
                            )
                        } else {
                            showExitDialog(
                                "알림\n",
                                "죄송합니다.\n서버 점검 중입니다.\n\n${SimpleDateFormat("M/d HH:mm", Locale.getDefault()).format(parsedStartTime)} ~ ${SimpleDateFormat("M/d HH:mm", Locale.getDefault()).format(parsedEndTime)}",
                                "OK"
                            )
                        }
                    } else {
                        throw JSONException("")
                    }
                } catch (e: ParseException) {
                    throw JSONException(e.message)
                }
            } else { // loginResult is "false"
                when (type) {
                    "join" -> showErrorDialog("스마텔 개통 고객이 아닙니다.")
                    "accnt" -> showErrorDialog("로그인 계정이 없습니다.\n회원가입이 필요합니다.")
                    "pwd" -> showErrorDialog("비밀번호가 일치하지 않습니다.\n비밀번호를 재입력해주세요.")
                    else -> showErrorDialog("알 수 없는 오류가 발생하였습니다.")
                }
                hideLoadingDialog()
            }
        } catch (e: JSONException) {
            hideLoadingDialog()
            showErrorDialog("Failed to parse login response")
        }
    }

    private fun showExitDialog(title: String = "알림", message: String, okButtonText: String = "OK") {

        requestQueue.cancelAll("LOGIN_REQUEST_TAG")
        hideLoadingDialog()

        val dialog = Dialog(this)

        // Set the custom layout
        dialog.setContentView(R.layout.dialog_layout)

        // Find the views in the custom layout
        val titleTextView = dialog.findViewById<TextView>(R.id.dialogTitle)
        val messageTextView = dialog.findViewById<TextView>(R.id.dialogMessage)
        val okButton = dialog.findViewById<Button>(R.id.okButton)

        // Set the text of the TextViews and Button
        titleTextView.text = title
        messageTextView.text = message
        okButton.text = okButtonText

        // Set an OnClickListener for the OK button
        okButton.setOnClickListener {
            finishAffinity()  // Closes all activities and exits the app
            dialog.dismiss()
        }

        // Set rounded corners to your AlertDialog (Optional)
        val window=dialog.window

        if(window != null){
            window.setBackgroundDrawableResource(R.drawable.box_whitesmoke)  // Use your own resource here.
            window.setLayout(850, 700)  // Specify exact sizes here according to your need.
        }

        // Show the custom alert dialog on screen.
        dialog.show()
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
            var phoneNumber = phoneNumberEditText.text.toString()

            if (phoneNumber == "123") {
                // If entered phone number is "320", replace it with the last fetched phone number
                phoneNumber = testPhoneNumber ?: ""
            }
            val telecom = response.getString("telecom")
            val custName = response.getString("custNm")
            val serviceAcct = response.getString("serviceAcct")
            val kind = response.getString("kind")

            // Save user info in SharedPreferences if auto login switch is on
            val sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            if (sharedPrefs.getBoolean("autoLogin", false)) {
                with(sharedPrefs.edit()) {
                    putString("phoneNumber", phoneNumber)
                    putString("password", passwordEditText.text.toString())
                    putString("telecom", telecom)
                    putString("custName", custName)
                    putString("kind", kind)
                    putString("serviceAcct", serviceAcct)
                    apply()
                }
            }
            Log.d("\nLoginActivity - getString",
                "-----------------User Info - Telecom: $telecom, CustName: $custName, ServiceAccount: $serviceAcct, PhoneNumber: $phoneNumber, Kind: $kind-----------------------")

            if (kind == "선불") { // Check if "kind" is "선불"
                showPrepaidDialog(
                    title = "알림",
                    message = "선불폰 전용앱을 이용해주세요.",
                    okButtonText = "설치하기"
                )
            }
            else {
                // Proceed to MainActivity if "kind" is not "선불"
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("custName", custName)
                intent.putExtra("PhoneNumber", phoneNumber)
                intent.putExtra("Telecom", telecom)
                intent.putExtra("serviceAcct", serviceAcct)
                startActivity(intent)
                Log.d("\nLoginActivity - putExtra",
                    "-----------------User Info - Telecom: $telecom, CustName: $custName, ServiceAccount: $serviceAcct, PhoneNumber: $phoneNumber-----------------------")
            }
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
    private fun showAlertDialog(message: String) {
        val alertDialog = AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton("OK") { dialog: DialogInterface, _: Int ->
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
    private fun showErrorDialog(message: String) {
        requestQueue.cancelAll("LOGIN_REQUEST_TAG")
        hideLoadingDialog()

        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage(message)
            .setCancelable(false)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .create()

        // Apply the custom style to the "OK" button
        val alertDialog = dialogBuilder.show()
        val okButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        okButton.setTextColor(ContextCompat.getColor(this, R.color.orange)) // Use the color resource if available
    }
    // 선불폰 고객 거르기 함수
    private fun showPrepaidDialog(title: String = "알림", message: String, okButtonText: String = "OK") {

        requestQueue.cancelAll("LOGIN_REQUEST_TAG")
        hideLoadingDialog()

        val dialog = Dialog(this)

        // Set the custom layout
        dialog.setContentView(R.layout.dialog_prepaid_exit_layout)

        // Find the views in the custom layout
        val titleTextView = dialog.findViewById<TextView>(R.id.dialogTitle)
        val messageTextView = dialog.findViewById<TextView>(R.id.dialogMessage)
        val okButton = dialog.findViewById<Button>(R.id.okButton)

        // Set the text of the TextViews and Button
        titleTextView.text = title
        messageTextView.text = message
        okButton.text = okButtonText

        // Set an OnClickListener for the OK button
        okButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=kr.co.Smartel.prepaid_smartel0525&hl=ko"))
            startActivity(intent)
            finishAffinity() // Closes all activities and exits the app
            dialog.dismiss()
        }

        // Calculate dialog width and height based on screen size
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels

        val dialogWidth = (screenWidth * 2 ) / 3
        val dialogHeight = (dialogWidth * 2) / 3

        // Set the dialog's size
        val window = dialog.window
        if (window != null) {
            window.setBackgroundDrawableResource(R.drawable.box_whitesmoke)
            window.setLayout(dialogWidth, dialogHeight)
        }

        dialog.show()
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