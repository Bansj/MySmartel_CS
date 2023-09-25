package com.smartelmall.mysmartel_ver_1.LGT.Bill

import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.smartelmall.mysmartel_ver_1.R
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
    private lateinit var txt_sumAmount: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_lgt_bill_detail, container, false)

        val moveButton = view.findViewById<ImageButton>(R.id.btn_pgDown)
        moveButton.setOnClickListener {
            animateFragmentOut(view)
        }
        return view
    }
    private fun animateFragmentOut(view: View) {
        val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_down)
        transaction.remove(this@LgtBillDetailFragment)
        transaction.commit()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //svcNmTextView = view.findViewById(R.id.svcNmTextView)
       // blItemNmTextView = view.findViewById(R.id.blItemNmTextView)
        //billAmtTextView = view.findViewById(R.id.billAmtTextView)
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
                    if (apiResponse.billInfo.isNullOrEmpty()) {
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
        if (apiResponse.billInfo.isNullOrEmpty()) {
            Log.e("LgTBillingActivity", "Billing information is null or empty in API response.")
            return
        }

        var totalAmountStr : String? = null

        val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault()) as DecimalFormat
        numberFormat.applyPattern("#,###")

        // Create a list of Bill objects from the API response
        var billsData= apiResponse.billInfo.map { item ->
            if(item.blItemNm.contains("총 납부하실 금액", ignoreCase=true)){
                totalAmountStr= "${numberFormat.format(item.billAmt.toLong())}원\n\n"
                LgtBill(item.blItemNm, totalAmountStr!!)
            }else{
                LgtBill(item.blItemNm, "${numberFormat.format(item.billAmt.toLong())}원\n\n")
            }
        }

        // Create an instance of the adapter and set it to the RecyclerView
        val adapter = LgtBillAdapter(billsData)
        val recyclerView = requireView().findViewById<RecyclerView>(R.id.recyclerView)

        activity?.runOnUiThread {
            recyclerView.layoutManager = LinearLayoutManager(activity)
            recyclerView.adapter = adapter

            // Set totalAmount in txt_sumAmount
            if (totalAmountStr != null) {
                txt_sumAmount.text = "${totalAmountStr}"
            } else {
                // Handle the case when "Total amount to be paid" is not present
                // You can set the txt_sumAmount to a default value or hide it if desired.
            }
        }
    }

}
