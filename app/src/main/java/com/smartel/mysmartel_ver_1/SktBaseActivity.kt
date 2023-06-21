package com.smartel.mysmartel_ver_1

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class SktBaseActivity: AppCompatActivity() {
    private lateinit var requestQueue: RequestQueue
    private lateinit var serviceAcct: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestQueue = Volley.newRequestQueue(this)

        serviceAcct = intent.getStringExtra("serviceAcct") ?: ""
        Log.d("SktActivity", "Service Account: $serviceAcct")

        // Call the function to fetch the deduction amount
        fetchDeductAmount()
    }

    private fun fetchDeductAmount() {
        val deductUrl = "http://vacs.smartelmobile.com/SKTRealTime/GetDeductAmount.php"

        val deductParams = JSONObject()
        deductParams.put("sv_acnt_num", serviceAcct)

        val deductRequest = JsonObjectRequest(
            Request.Method.POST, deductUrl, deductParams,
            Response.Listener { response ->
                handleDeductResponse(response)
            },
            Response.ErrorListener { error ->
                Log.e("SktActivity", "Error fetching deduction amount: ${error.message}")
            }
        )
        requestQueue.add(deductRequest)
    }

    private fun handleDeductResponse(response: JSONObject) {
        try {
            val resultCode = response.getString("ResultCode")
            if (resultCode == "0000") {
                val remainInfoArray = response.getJSONArray("remainInfo")
                for (i in 0 until remainInfoArray.length()) {
                    val remainInfo = remainInfoArray.getJSONObject(i)
                    val svcNm = remainInfo.getString("svcNm")
                    val svcTypNm = remainInfo.getString("svcTypNm")
                    val alloValue = remainInfo.getString("alloValue")
                    val useValue = remainInfo.getString("useValue")
                    val prodTypeCd = remainInfo.getString("prodTypeCd")

                    // TODO: Display the deduction amount information as needed
                    Log.d("SktActivity", "SvcNm: $svcNm, SvcTypNm: $svcTypNm, AlloValue: $alloValue, UseValue: $useValue, ProdTypeCd: $prodTypeCd")
                }
            } else {
                Log.e("SktActivity", "Error in deduction response: ResultCode = $resultCode")
            }
        } catch (e: JSONException) {
            Log.e("SktActivity", "Error parsing deduction response: ${e.message}")
        }
    }
}