package com.smartel.mysmartel_ver_1

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.security.cert.X509Certificate
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

abstract class BaseActivity : AppCompatActivity() {
    private lateinit var requestQueue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestQueue = Volley.newRequestQueue(this, ignoreSslErrorHurlStack())
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

    fun sktDeductAmountRequest(serviceAccount: String) {
        val deductUrl = "http://vacs.smartelmobile.com/SKTRealTime/GetDeductAmount.php"
        val deductParams = JSONObject()
        deductParams.put("sv_acnt_num", serviceAccount)

        val deductRequest = JsonObjectRequest(
            Request.Method.POST, deductUrl, deductParams,
            Response.Listener { response ->
                handleSktDeductAmountResponse(response)
            },
            Response.ErrorListener { error ->
                showErrorDialog("An error occurred while fetching SKT deduct amount: ${error.message}")
            }
        )
        requestQueue.add(deductRequest)
    }

    abstract fun handleSktDeductAmountResponse(response: JSONObject)

    private fun showErrorDialog(message: String) {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage(message)
            .setCancelable(false)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }

        val alert = dialogBuilder.create()
        alert.show()
    }
}
