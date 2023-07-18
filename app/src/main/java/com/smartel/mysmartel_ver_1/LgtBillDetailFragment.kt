package com.smartel.mysmartel_ver_1

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.mysmartel_ver_1.R
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class LgtBillDetailFragment : Fragment() {

    private val baseUrl = "https://www.mysmartel.com/api/lguChargeDtl.php"

    private lateinit var svcNmTextView: TextView
    private lateinit var blItemNmTextView: TextView
    private lateinit var billAmtTextView: TextView
    private lateinit var vatPrntYnTextView: TextView
    private lateinit var txt_sumAmount: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_lgt_bill_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        svcNmTextView = view.findViewById(R.id.svcNmTextView)
        blItemNmTextView = view.findViewById(R.id.blItemNmTextView)
        billAmtTextView = view.findViewById(R.id.billAmtTextView)
        txt_sumAmount = view.findViewById(R.id.txt_sumAmount)

        val phoneNumber = arguments?.getString("phoneNumber")
        val custName = arguments?.getString("custName")

        val currentDate = SimpleDateFormat("yyyyMM", Locale.getDefault()).format(Date())

        val url = "$baseUrl?serviceNum=$phoneNumber&custNm=$custName&billTrgtYymm=$currentDate"
        val request = Request.Builder()
            .url(url)
            .build()

        val client = createOkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("LgtBillDetailFragment", "API Call Failed: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                Log.d("LgtBillDetailFragment", "API Response Data: $responseData")

                if (responseData.isNullOrEmpty()) {
                    Log.e("LgtBillDetailFragment", "Empty or null API response.")
                    return
                }

                try {
                    val apiResponse = Gson().fromJson(responseData, LgtBillApiResponse::class.java)
                    if (apiResponse.BillInfo.isNullOrEmpty()) {
                        Log.e("LgtBillDetailFragment", "Bill Info is null or empty in API response.")
                        return
                    }

                    activity?.runOnUiThread {
                        updateUI(apiResponse)
                    }
                } catch (e: Exception) {
                    Log.e("LgtBillDetailFragment", "Failed to parse API response: ${e.message}")
                }
            }
        })
    }

    private fun createOkHttpClient(): OkHttpClient {
        // Create a trust manager that trusts all certificates
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
                // No implementation needed
            }

            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
                // No implementation needed
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }
        })

        // Create a SSL context with the trust manager
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())

        // Create a hostname verifier that bypasses all hostnames
        val hostnameVerifier = HostnameVerifier { _, _ -> true }

        // Create the OkHttpClient with the custom SSL socket factory and hostname verifier
        return OkHttpClient.Builder()
            .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier(hostnameVerifier)
            .build()
    }

    private fun updateUI(apiResponse: LgtBillApiResponse) {
        if (apiResponse.BillInfo.isNullOrEmpty()) {
            Log.e("LgtBillDetailFragment", "Bill Info is null or empty in API response.")
            return
        }

        val billInfoList = apiResponse.BillInfo
        var totalAmount: String? = null

        val sb = StringBuilder()
        val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault()) as DecimalFormat
        numberFormat.applyPattern("#,###")

        val maxBlItemNmWidth = calculateMaxBlItemNmWidth(billInfoList) // Calculate the maximum width of blItemNm column

        for (billInfo in billInfoList) {
            sb.append(String.format("%-60s\n\n", billInfo.blItemNm)) // Left align blItemNm column with 20 characters

            if (billInfo.blItemNm.contains("총 납부하실 금액", ignoreCase = true)) {
                val formattedAmount = numberFormat.format(billInfo.billAmt.toLong())
                totalAmount = "${formattedAmount}원"
                sb.append(String.format("%64s", totalAmount)) // Right align totalAmount value with padding
            } else {
                val formattedBillAmt = numberFormat.format(billInfo.billAmt.toLong())
                sb.append(String.format("%64s", "${formattedBillAmt}원")) // Right align billAmt value with padding
            }
            sb.append("\n\n\n\n")
        }

        activity?.runOnUiThread {
            svcNmTextView.text = sb.toString()

            // Set LinearLayout parameters for svcNmTextView
            val layoutParams = svcNmTextView.layoutParams as LinearLayout.LayoutParams
            layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT

            // Apply gravity to center
            svcNmTextView.gravity = Gravity.CENTER

            // Set updated layout parameters for svcNmTextView
            svcNmTextView.layoutParams = layoutParams
            // Set totalAmount in txt_sumAmount
            if (totalAmount != null) {
                txt_sumAmount.text = "${totalAmount}"
            } else {
                // Handle the case when "Total amount to be paid" is not present
                // You can set the txt_sumAmount to a default value or hide it if desired.
            }
        }
    }

    private fun calculateMaxBlItemNmWidth(billInfoList: List<BillInfo>): Int {
        var maxBlItemNmWidth = 0
        for (billInfo in billInfoList) {
            val blItemNmWidth = billInfo.blItemNm.length
            if (blItemNmWidth > maxBlItemNmWidth) {
                maxBlItemNmWidth = blItemNmWidth
            }
        }
        return maxBlItemNmWidth
    }



}
