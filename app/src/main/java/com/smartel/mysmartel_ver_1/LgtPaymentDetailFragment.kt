package com.smartel.mysmartel_ver_1

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.mysmartel_ver_1.R
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException
import java.security.SecureRandom
import java.security.cert.X509Certificate
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

        val billInfoList = apiResponse.BillInfo

        val sb = StringBuilder()
        for (billInfo in billInfoList) {
            val paymentMethod = billInfo.Method
            val paymentDate = billInfo.PayDate
            val paymentAmount = billInfo.PayAmt
            val paymentName = billInfo.PayName

            sb.append("Payment Method: $paymentMethod\n")
            sb.append("Payment Date: $paymentDate\n")
            sb.append("Payment Amount: $paymentAmount\n")
            sb.append("Payment Name: $paymentName\n\n")

            val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
            val parsedDate = dateFormat.parse(paymentDate)
            val formattedDate = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault()).format(parsedDate ?: Date())

            val paymentDateTextView = TextView(requireContext()).apply {
                text = "\n$formattedDate\n\n"
                gravity = Gravity.CENTER
                setTextColor(Color.BLACK)
                textSize = 20f
            }

            val paymentAmountTextView = TextView(requireContext()).apply {
                val fullText = "청구요금".padEnd(40) + "${paymentAmount}원\n"
                val grayTextColor = ContextCompat.getColor(requireContext(), R.color.grey)
                val blackTextColor = Color.BLACK

                val spannableString = SpannableString(fullText)
                spannableString.setSpan(ForegroundColorSpan(grayTextColor), 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannableString.setSpan(ForegroundColorSpan(blackTextColor), 5, fullText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                text = spannableString
                gravity = Gravity.CENTER
                textSize = 18f
            }

            val paymentMethodTextView = TextView(requireContext()).apply {
                text = "납부방법".padEnd(30) + paymentMethod + "\n\n"
                gravity = Gravity.CENTER
                textSize = 18f
            }
            // Add the TextViews to the layout container
            containerLayout.addView(paymentDateTextView)
            containerLayout.addView(paymentAmountTextView)
            containerLayout.addView(paymentMethodTextView)
        }
        // Log the complete response
        sb.append("Complete Response: $responseString")
    }
}


