package com.smartel.mysmartel_ver_1

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.mysmartel_ver_1.R
import com.example.mysmartel_ver_1.databinding.FragmentLgtDeductDetailViewBinding
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


class LgtDeductDetailViewFragment : Fragment() {

    private lateinit var binding: FragmentLgtDeductDetailViewBinding
    private val phoneNumber: String by lazy { arguments?.getString("phoneNumber") ?: "" }
    private val custNm: String by lazy { arguments?.getString("custNm") ?: "" }
    private lateinit var dataTextView: TextView
    private lateinit var downButton: ImageButton

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLgtDeductDetailViewBinding.inflate(inflater, container, false)

        dataTextView = binding.dataTextView // Initialize dataTextView here

        //올라온 화면이 제거되는 버튼 클릭 이벤트
        downButton = binding.btnPgDown
        downButton.setOnClickListener {
            animateFragmentOut(binding.root)
        }

        fetchData()

        return binding.root
    }
    private fun animateFragmentOut(view: View) { // 슬라이드 업 애니메이션 효과
        val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_down)
        transaction.remove(this@LgtDeductDetailViewFragment)
        transaction.commit()
    }

    private fun fetchData() {
        val baseUrl = "https://www.mysmartel.com/api/"
        val apiRequestUrl = "${baseUrl}lguDeductibleAmt.php?serviceNum=$phoneNumber&custNm=$custNm"

        // Create a TrustManager that trusts all certificates
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            @Throws(CertificateException::class)
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
                // No-op
            }

            @Throws(CertificateException::class)
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
                // No-op
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }
        })

        // Create a HostnameVerifier that accepts all hostnames
        val hostnameVerifier = HostnameVerifier { _, _ -> true }

        // Create an SSLContext with the custom TrustManager
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, trustAllCerts, null)

        // Create an OkHttpClient that trusts all certificates and uses the custom SSLContext
        val client = OkHttpClient.Builder()
            .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier(hostnameVerifier)
            .build()

        val request = Request.Builder()
            .url(apiRequestUrl)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle error
                Log.d("LgtDeductDetailViewFragment", "API request failed: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseData = response.body?.string()
                    Log.d("LgtDeductDetailViewFragment", "API response data: $responseData")
                    val gson = Gson()
                    val apiResponse = gson.fromJson(responseData, LgtDedcutApiResponse::class.java)
                    handler.post { updateUI(apiResponse) }
                } else {
                    Log.d("LgtDeductDetailViewFragment", "API request failed: ${response.code}")
                }
            }
        })
    }

    private fun updateUI(apiResponse: LgtDedcutApiResponse) {
        val remainInfoList = apiResponse.remainInfo
        val resultCode = apiResponse.ResultCode

        // Print the ResultCode
        Log.d("LgtDeductDetailView", "ResultCode: $resultCode")

        // Create a StringBuilder to build the data string
        val dataStringBuilder = StringBuilder()

        // Iterate over the RemainInfo list and append the values to the data string
        for (remainInfo in remainInfoList) {
            val svcNm = remainInfo.svcNm
            val svcTypNm = remainInfo.svcTypNm
            val svcUnitCd = remainInfo.svcUnitCd
            val alloValue = remainInfo.alloValue
            val useValue = remainInfo.useValue
            val prodTypeCd = remainInfo.prodTypeCd

            // Modify svcTypNm if it contains "패킷데이터"
            val modifiedSvcTypNm = if (svcTypNm.contains("패킷데이터")) {
                svcTypNm.replace("패킷데이터", "데이터")
            } else {
                svcTypNm
            }

            // Append the values to the data string with proper formatting
            dataStringBuilder.append("$svcNm\t")
            dataStringBuilder.append(" $modifiedSvcTypNm\n\n")

            if (svcUnitCd.contains("초")) {
                if (alloValue.contains("Z")) {
                    dataStringBuilder.append("총제공량: 무제한\n")
                    val useValueInMinutes = useValue.toDouble() / 60
                    dataStringBuilder.append("사용량: ${useValueInMinutes.format(0)}분\n\n\n")
                } else {
                    val alloValueInMinutes = alloValue.toDouble() / 60
                    val useValueInMinutes = useValue.toDouble() / 60
                    val remainValueMin = alloValueInMinutes - useValueInMinutes
                    dataStringBuilder.append("총제공량: ${alloValueInMinutes.format(0)}분\n")
                    dataStringBuilder.append("사용량: ${useValueInMinutes.format(0)}분\n")
                    dataStringBuilder.append("잔여량 ${remainValueMin.format(0)}분\n\n\n")
                }
            } else if (svcUnitCd.contains("건")) {
                if (alloValue.contains("Z")) {
                    dataStringBuilder.append("총제공량: 무제한\n")
                    dataStringBuilder.append("사용량: ${useValue}건\n\n\n")
                } else {
                    dataStringBuilder.append("총제공량: ${alloValue}건\n")
                    dataStringBuilder.append("사용량: ${useValue}건\n")
                    val remainValue = alloValue.toInt() - useValue.toInt()
                    dataStringBuilder.append("잔여량: ${remainValue}건\n\n\n")
                }
            } else if (svcTypNm.contains("패킷")) {
                val alloValueInGB = alloValue.toDouble() / 1024 / 1024
                val useValueInGB = useValue.toDouble() / 1024 / 1024
                val remainValueInGB = alloValueInGB - useValueInGB
                dataStringBuilder.append("총제공량: ${alloValueInGB.format(1)}GB\n")
                dataStringBuilder.append("사용량: ${useValueInGB.format(1)}GB\n")
                dataStringBuilder.append("잔여량: ${remainValueInGB.format(1)}GB\n\n\n")
            } else {
                dataStringBuilder.append("총제공량: $alloValue\n")
                dataStringBuilder.append("사용량: $useValue\n\n\n")
            }
        }
        // Set the data string to the dataTextView and center-align the text
        dataTextView.text = dataStringBuilder.toString()
        dataTextView.gravity = Gravity.CENTER
    }
    // Extension function to format a Double value with the specified number of decimal places
    private fun Double.format(decimalPlaces: Int): String {
        return String.format("%.${decimalPlaces}f", this)
    }
}



