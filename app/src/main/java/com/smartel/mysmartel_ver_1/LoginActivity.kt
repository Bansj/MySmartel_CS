package com.smartel.mysmartel_ver_1

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.mysmartel_ver_1.R
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

        val signUpButton: Button = findViewById(R.id.btn_signUp)
        signUpButton.setOnClickListener {
            val intent = Intent(this, WebViewActivity::class.java)
            startActivity(intent)
        }

        phoneNumberEditText = findViewById(R.id.edit_id)
        passwordEditText = findViewById(R.id.edit_password)
        loginButton = findViewById(R.id.btn_login)

        loadingDialog = createLoadingDialog()

        requestQueue = Volley.newRequestQueue(this, ignoreSslErrorHurlStack())

        loginButton.setOnClickListener {
            Log.d("LoginActivity", "Login button clicked")
            loginUser()
        }
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

        Log.d("LoginActivity", "Login User - Phone number: $phoneNumber, Password: $password")

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
    }

    private fun handleLoginResponse(response: JSONObject) {
        try {
            val loginResult = response.getString("resultCd")

            if (loginResult == "true") {
                val phoneNumber = phoneNumberEditText.text.toString()
                Log.d("LoginActivity", "Login successful - Phone number: $phoneNumber")
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
                handleInfoResponse(response, phoneNumber)
            },
            Response.ErrorListener { error ->
                hideLoadingDialog()
                showErrorDialog("An error occurred while fetching user info: ${error.message}")
            }
        )
        requestQueue.add(infoRequest)
    }

    private fun handleInfoResponse(response: JSONObject, phoneNumber: String) {
        hideLoadingDialog()
        try {
            val telecom = response.getString("telecom")
            val custName = response.getString("custNm")
            val serviceAcct = response.getString("serviceAcct")
            Log.d("LoginActivity", "User info - Telecom: $telecom, CustName: $custName, ServiceAccount: $serviceAcct")

            if (telecom == "SKT") {
                fetchDeductAmount(serviceAcct)
            } else {
                navigateToMainActivity(telecom, custName, serviceAcct)
            }
        } catch (e: JSONException) {
            showErrorDialog("Failed to parse user info response")
        }
    }

    private fun fetchDeductAmount(serviceAcct: Char) {
        val deductParams = JSONObject()
        deductParams.put("sv_acnt_num", serviceAcct)

        val deductUrl = "http://vacs.smartelmobile.com/SKTRealTime/GetDeductAmount.php"
        val deductRequest = JsonObjectRequest(
            Request.Method.POST, deductUrl, deductParams,
            Response.Listener { response ->
                handleDeductAmountResponse(response)
            },
            Response.ErrorListener { error ->
                hideLoadingDialog()
                showErrorDialog("An error occurred while fetching deduct amount: ${error.message}")
            }
        )
        requestQueue.add(deductRequest)
    }

    private fun handleInfoResponse(response: JSONObject) {
        hideLoadingDialog()
        try {
            val telecom = response.getString("telecom")
            val custName = response.getString("custNm")
            val serviceAcct = response.getString("serviceAcct")
            Log.d("LoginActivity", "User info - Telecom: $telecom, CustName: $custName, ServiceAccount: $serviceAcct")

            if (telecom == "SKT") {
                fetchDeductAmount(serviceAcct)
            } else {
                navigateToMainActivity(telecom, custName, serviceAcct)
            }
        } catch (e: JSONException) {
            showErrorDialog("Failed to parse user info response")
        }
    }

    private fun fetchDeductAmount(serviceAcct: String) {
        val deductParams = JSONObject()
        deductParams.put("sv_acnt_num", serviceAcct)

        val deductUrl = "http://vacs.smartelmobile.com/SKTRealTime/GetDeductAmount.php"
        val deductRequest = JsonObjectRequest(
            Request.Method.POST, deductUrl, deductParams,
            Response.Listener { response ->
                handleDeductAmountResponse(response)
            },
            Response.ErrorListener { error ->
                hideLoadingDialog()
                showErrorDialog("An error occurred while fetching deduct amount: ${error.message}")
            }
        )
        requestQueue.add(deductRequest)
    }

    private fun handleDeductAmountResponse(response: JSONObject) {
            try {
                val resultCode = response.getString("RC_CL_CD")

                if (resultCode == "00") {
                    val deductionCount = response.getString("DEDT_REC_CNT")

                    if (deductionCount.isNotBlank() && deductionCount != "0") {
                        // Deduction amount records are available
                        val productID = response.getString("PLAN_ID")
                        val productName = response.getString("PLAN_NM")
                        val deductionCode = response.getString("SKIP_CODE")
                        val deductionName = response.getString("FREE_PLAN_NAME")
                        val basicDeductionAmount = response.getString("TOTAL_QTY")
                        val usage = response.getString("USE_QTY")
                        val remainingAmount = response.getString("REM_QTY")
                        val deductionUnitCode = response.getString("UNIT_CD")

                        // Pass the deduction amount details to MyInfoFragment
                        val fragment = MyInfoFragment.newInstance(
                            productID, productName, deductionCode, deductionName,
                            basicDeductionAmount, usage, remainingAmount, deductionUnitCode
                        )

                        supportFragmentManager.beginTransaction()
                            .replace(R.id.myInfoFragment, fragment)
                            .commit()
                    } else {
                        // No deduction amount records available
                        showErrorDialog("No deduction amount records found")
                    }
                } else {
                    // Error occurred in deduction amount request
                    showErrorDialog("Failed to fetch deduction amount: $resultCode")
                }
            } catch (e: JSONException) {
                showErrorDialog("Failed to parse deduction amount response")
            }
        }


    private fun navigateToMainActivity(telecom: String, custName: String, serviceAcct: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("telecom", telecom)
        intent.putExtra("custName", custName)
        intent.putExtra("serviceAcct", serviceAcct)
        startActivity(intent)
        finish()
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