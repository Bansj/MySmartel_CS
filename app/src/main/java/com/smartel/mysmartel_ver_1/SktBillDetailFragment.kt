package com.smartel.mysmartel_ver_1

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.mysmartel_ver_1.R
import okhttp3.*
import java.io.IOException
import java.net.URLEncoder

class SktBillDetailFragment : Fragment() {

    // Late-initialized variables
    private lateinit var serviceAcct: String
    private lateinit var textViewBillingDetails: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_skt_bill_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize textViewBillingDetails using findViewById with its ID
        textViewBillingDetails = view.findViewById(R.id.textViewBillingDetails)

        // Retrieve the serviceAcct value from arguments
        serviceAcct = arguments?.getString("serviceAcct") ?: ""
        if (serviceAcct.isEmpty()) {
            // Handle the case where serviceAcct is not provided properly
            Log.e("BillingDetails", "Missing serviceAcct value from arguments")
            return
        }

        // Fetch billing details from API
        fetchBillingDetails(serviceAcct)
    }

    private fun fetchBillingDetails(serviceAcct: String) {
        val baseUrl = "http://vacs.smartelmobile.com/SKTRealTime/GetUsageRate.php"
        val encodedServiceAcct = URLEncoder.encode(serviceAcct, "UTF-8")
        val url = "$baseUrl?sv_acnt_num=$encodedServiceAcct"

        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle API call failure here
                Log.e("BillingDetails", "API call failed: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string() ?: ""
                if (responseData.isNotEmpty()) {
                    // Process the API response and display in textView
                    activity?.runOnUiThread {
                        processResponseData(responseData)
                    }
                } else {
                    // Handle the case where the response is empty or unexpected
                    Log.e("BillingDetails", "Empty or unexpected response: $responseData")
                }
            }
        })
    }

    private fun processResponseData(responseData: String) {
        val items = responseData.split(" ")

        val stringBuilder = StringBuilder()
        for (i in 0 until items.size step 5) {
            val name = items.getOrNull(i) ?: continue
            val description = items.getOrNull(i + 1) ?: continue
            val amount = items.getOrNull(i + 2) ?: continue

            val formattedString = "$name $description $amount \t"
            stringBuilder.append(formattedString)
        }

        val formattedBillingDetails = stringBuilder.toString()

        // Replace the newline character with a space for logging
        val formattedBillingDetailsLog = formattedBillingDetails.replace("\n", " ")

        // Log the formatted billing details in a single line
        Log.d("BillingDetails", "Formatted Billing Details: $formattedBillingDetailsLog")

        // Set the formatted billing details to the textView
        textViewBillingDetails.text = formattedBillingDetails
    }

}

