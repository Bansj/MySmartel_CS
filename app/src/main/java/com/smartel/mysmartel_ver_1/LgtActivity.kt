package com.smartel.mysmartel_ver_1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.mysmartel_ver_1.R
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class LgtActivity : AppCompatActivity() {

    private lateinit var requestQueue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestQueue = Volley.newRequestQueue(this)

        // Get the mobile phone number and customer name from LoginActivity
        val phoneNumber = intent.getStringExtra("phoneNumber")
        val custNm = intent.getStringExtra("custNm")

        // Make API request to fetch deduction details
        if (phoneNumber != null) {
            if (custNm != null) {
                fetchDeductionDetails(phoneNumber, custNm)
            }
        }
    }

    private fun fetchDeductionDetails(phoneNumber: String, custNm: String) {
        val url = "https://www.mysmartel.com/api/lguDeductibleAmt.php?serviceNum=$phoneNumber&custNm=$custNm"

        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            Response.Listener { response ->
                try {
                    val resultCode = response.getString("ResultCode")
                    if (resultCode == "0000") {
                        val remainInfo = response.getJSONArray("remainInfo")
                        // Process the deduction details and pass it to LgtDeductDetailViewFragment
                        val deductionDetails = processDeductionDetails(remainInfo)
                        showDeductionDetailsFragment(deductionDetails)
                    } else {
                        // Handle the API response with error
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    // Handle JSON parsing error
                }
            },
            Response.ErrorListener { error ->
                error.printStackTrace()
                // Handle Volley error
            })

        requestQueue.add(jsonObjectRequest)
    }

    private fun processDeductionDetails(remainInfo: JSONArray): List<LgtRemainInfo> {
        val deductionDetails = mutableListOf<LgtRemainInfo>()

        for (i in 0 until remainInfo.length()) {
            val detailObj = remainInfo.getJSONObject(i)
            val svcNm = detailObj.getString("svcNm")
            val svcTypNm = detailObj.getString("svcTypNm")
            val svcUnitCd = detailObj.getString("svcUnitCd")
            val alloValue = detailObj.getString("alloValue")
            val useValue = detailObj.getString("useValue")
            val prodTypeCd = detailObj.getString("prodTypeCd")

            val deductionDetail = LgtRemainInfo(svcNm, svcTypNm, svcUnitCd, alloValue, useValue, prodTypeCd)
            deductionDetails.add(deductionDetail)
        }

        return deductionDetails
    }

    private fun showDeductionDetailsFragment(deductionDetails: List<LgtRemainInfo>) {
        val fragment = LgtDeductDetailViewFragment.newInstance(deductionDetails)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentDeductDetailView, fragment)
            .commit()
    }
}






