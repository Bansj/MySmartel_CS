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


class LgtBaseActivity : AppCompatActivity() {
    private lateinit var requestQueue: RequestQueue
    private lateinit var phoneNumber: String
    private lateinit var custName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestQueue = Volley.newRequestQueue(this)

        phoneNumber = intent.getStringExtra("phoneNumber") ?: ""
        custName = intent.getStringExtra("custName") ?: ""
        Log.d("LgtActivity", "Phone Number: $phoneNumber, CustName: $custName")

        // Call the function to fetch the deduction amount
        fetchDeductAmount()
    }

    private fun fetchDeductAmount() {
        val deductUrl = "http://61.41.9.34/lg_RealTime/getrealtime5.php"

        val deductParams = JSONObject()
        deductParams.put("serviceNum", phoneNumber)
        deductParams.put("custNm", custName)

        val deductRequest = JsonObjectRequest(
            Request.Method.POST, deductUrl, deductParams,
            Response.Listener { response ->
                handleDeductResponse(response)
            },
            Response.ErrorListener { error ->
                Log.e("LgtActivity", "Error fetching deduction amount: ${error.message}")
            }
        )
        requestQueue.add(deductRequest)
    }

    private fun handleDeductResponse(response: JSONObject) {
        try {
            val remainInfoArray = response.getJSONArray("remainInfo")
            for (i in 0 until remainInfoArray.length()) {
                val remainInfo = remainInfoArray.getJSONObject(i)
                val svcNm = remainInfo.getString("svcNm")
                val svcTypNm = remainInfo.getString("svcTypNm")
                val alloValue = remainInfo.getString("alloValue")
                val useValue = remainInfo.getString("useValue")
                val prodTypeCd = remainInfo.getString("prodTypeCd")

                // TODO: Display the deduction amount information as needed
                Log.d("LgtActivity", "SvcNm: $svcNm, SvcTypNm: $svcTypNm, AlloValue: $alloValue, UseValue: $useValue, ProdTypeCd: $prodTypeCd")
            }
        } catch (e: JSONException) {
            Log.e("LgtActivity", "Error parsing deduction response: ${e.message}")
        }
    }
}
