package com.smartelmall.mysmartel_ver_1.LGT.Payment

import android.graphics.Color
import android.os.Bundle
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
import java.security.cert.X509Certificate
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class LgtPaymentDetailFragment : Fragment() {

    private lateinit var containerLayout: LinearLayout

    private val baseUrl = "https://www.mysmartel.com/api/lguPayment.php"
    private val client = getUnsafeOkHttpClient()
    private val gson = Gson()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_lgt_payment_detail, container, false)

        val moveButton = view.findViewById<ImageButton>(R.id.btn_pgDown)
        moveButton.setOnClickListener {
            animateFragmentOut(view)
        }

        return view
    }
    private fun animateFragmentOut(view: View) {
        val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_down)
        transaction.remove(this@LgtPaymentDetailFragment)
        transaction.commit()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        containerLayout = view.findViewById(R.id.containerLayout)

        val custNm = arguments?.getString("custNm")
        val phoneNumber = arguments?.getString("phoneNumber")

        Log.d("LgtPaymentDetailFragment", "Phone Number: $phoneNumber")
        Log.d("LgtPaymentDetailFragment", "Customer Name: $custNm")

        val url = "$baseUrl?serviceNum=$phoneNumber&custNm=$custNm"
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("LgtPaymentDetailFragment", "API Call Failed: ${e.message}")
                // Handle API call failure
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                val apiResponse = gson.fromJson(responseBody, LgtPaymentApiResponse::class.java)

                activity?.runOnUiThread {
                    updateUI(apiResponse, response.toString())
                }

                Log.d("LgtPaymentDetailFragment", "Response Body: $responseBody")
            }
        })
    }

    private fun getUnsafeOkHttpClient(): OkHttpClient {
        try {
            // Create a trust manager that does not validate certificate chains
            val trustAllCerts: Array<TrustManager> = arrayOf(object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}

                override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}

                override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            })

            // Install the all-trusting trust manager
            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, trustAllCerts, java.security.SecureRandom())

            // Create an OkHttpClient that trusts all certificates
            val builder = OkHttpClient.Builder()
            builder.sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
            builder.hostnameVerifier { _, _ -> true }

            return builder.build()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    private fun updateUI(apiResponse: LgtPaymentApiResponse, responseString: String) {
        if (apiResponse.BillInfo.isNullOrEmpty()) {
            Log.e("LgtPaymentDetailFragment", "Bill Info is null or empty in API response.")
            return
        }

        val recyclerView = requireView().findViewById<RecyclerView>(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(activity)

        val billInfoList = apiResponse.BillInfo

        // LgtPayment 객체로 변환하여 저장할 리스트
        val lgtPaymentList = ArrayList<LgtPayment>()

        for (billInfo in billInfoList) {
            val paymentMethod = billInfo.Method
            val paymentDate = billInfo.PayDate
            val paymentAmount = billInfo.PayAmt
            // PayAmt 값을 안전하게 Double로 변환 후 Int로 변환. 만약 변환이 불가능한 경우 0으로 설정.
            val paymentAmountInt = billInfo.PayAmt.replace(",", "").toDoubleOrNull()?.toInt() ?: 0
            val paymentName = billInfo.PayName

            // NumberFormat 인스턴스 생성 (Locale.KOREA 설정)
            val numberFormat = NumberFormat.getNumberInstance(Locale.KOREA)

            // paymentAmountInt 값을 천 단위로 구분하여 문자열로 변환
            val paymentAmountStr = numberFormat.format(paymentAmountInt)

            // LgtPayment 객체 생성 후 리스트에 추가
            lgtPaymentList.add(LgtPayment(paymentMethod, paymentDate, paymentAmount, paymentName))
        }

        // Adapter 생성 후 RecyclerView에 설정
        val adapter=LgtPaymentAdapter(lgtPaymentList)
        recyclerView.adapter=adapter

    }
}


