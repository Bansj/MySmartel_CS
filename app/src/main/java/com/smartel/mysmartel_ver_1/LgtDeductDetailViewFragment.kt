package com.smartel.mysmartel_ver_1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.mysmartel_ver_1.R
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class LgtDeductDetailViewFragment : Fragment() {

    private lateinit var phoneNumber: String
    private lateinit var userName: String
    private lateinit var deductDetailsTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            phoneNumber = it.getString("phoneNumber", "")
            userName = it.getString("userName", "")
        }

        fetchDeductibleAmount()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_lgt_deduct_detail_view, container, false)
    }

    private fun fetchDeductibleAmount() {
        val apiUrl = "https://www.mysmartel.com/api/lguDeductibleAmt.php?serviceNum=$phoneNumber&custNm=$userName"

        val client = OkHttpClient()
        val request = Request.Builder()
            .url(apiUrl)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle API request failure
                activity?.runOnUiThread {
                    // Display an error message to the user
                    // ...
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                parseResponseData(responseData)
            }
        })
    }

    private fun parseResponseData(responseData: String?) {
        responseData?.let {
            val jsonObject = JSONObject(it)
            val remainInfoArray = jsonObject.getJSONArray("remainInfo")

            val productNames = mutableListOf<String>()
            val serviceTypes = mutableListOf<String>()
            val units = mutableListOf<String>()
            val alloValues = mutableListOf<String>()
            val useValues = mutableListOf<String>()
            val productTypes = mutableListOf<String>()

            for (i in 0 until remainInfoArray.length()) {
                val item = remainInfoArray.getJSONObject(i)
                val svcNm = item.getString("svcNm")
                val svcTypNm = item.getString("svcTypNm")
                val svcUnitCd = item.getString("svcUnitCd")
                val alloValue = item.getString("alloValue")
                val useValue = item.getString("useValue")
                val prodTypeCd = item.getString("prodTypeCd")

                productNames.add(svcNm)
                serviceTypes.add(svcTypNm)
                units.add(svcUnitCd)
                alloValues.add(alloValue)
                useValues.add(useValue)
                productTypes.add(prodTypeCd)
            }

            activity?.runOnUiThread {
                // Update the UI with the retrieved data
                displayDeductibleDetails(productNames, serviceTypes, units, alloValues, useValues, productTypes)
            }
        }
    }

    private fun displayDeductibleDetails(
        productNames: List<String>,
        serviceTypes: List<String>,
        units: List<String>,
        alloValues: List<String>,
        useValues: List<String>,
        productTypes: List<String>
    ) {
        // Display the retrieved data in the UI
        val sb = StringBuilder()
        for (i in productNames.indices) {
            sb.append("Product Name: ${productNames[i]}\n")
            sb.append("Service Type: ${serviceTypes[i]}\n")
            sb.append("Unit: ${units[i]}\n")
            sb.append("Allocated Value: ${alloValues[i]}\n")
            sb.append("Usage Value: ${useValues[i]}\n")
            sb.append("Product Type: ${productTypes[i]}\n")
            sb.append("\n")
        }
        deductDetailsTextView.text = sb.toString()
    }
}













