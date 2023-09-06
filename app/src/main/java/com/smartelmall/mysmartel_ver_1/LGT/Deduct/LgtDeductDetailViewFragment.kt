package com.smartelmall.mysmartel_ver_1.LGT.Deduct

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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.smartelmall.mysmartel_ver_1.R
import com.smartelmall.mysmartel_ver_1.databinding.FragmentLgtDeductDetailViewBinding
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
    private val custName: String by lazy { arguments?.getString("custName") ?: "" }
    private lateinit var dataTextView: TextView
    private lateinit var downButton: ImageButton

    private val handler = Handler(Looper.getMainLooper())

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter:LgtDeductAdapter

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
        LgtDeductfetchData()

        return binding.root
    }

    private fun animateFragmentOut(view: View) { // 슬라이드 다운 애니메이션 효과
        val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_down)
        transaction.remove(this@LgtDeductDetailViewFragment)
        transaction.commit()
    }

    private fun LgtDeductfetchData() {
        val baseUrl = "https://www.mysmartel.com/api/"
        val apiRequestUrl =
            "${baseUrl}lguDeductibleAmt.php?serviceNum=$phoneNumber&custNm=$custName"

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
                    handler.post { LgtDeductupdateUI(apiResponse) }
                } else {
                    Log.d("LgtDeductDetailViewFragment", "API request failed: ${response.code}")
                }
            }
        })
    }

    private fun LgtDeductupdateUI(apiResponse: LgtDedcutApiResponse) {
        val remainInfoList = apiResponse.remainInfo ?: emptyList()
        val resultCode = apiResponse.ResultCode

        // Print the ResultCode
        Log.d("LgtDeductDetailView", "ResultCode: $resultCode")

        // Create a StringBuilder to build the data string
        val dataStringBuilder = StringBuilder()

        val LgtDeductItemList=ArrayList<LgtDeductItem>()

        // Iterate over the RemainInfo list and append the values to the data string
        for (remainInfo in remainInfoList) {
            val svcNm = remainInfo.svcNm
            val svcTypNm = remainInfo.svcTypNm
            val svcUnitCd = remainInfo.svcUnitCd
            val alloValue = remainInfo.alloValue
            val useValue = remainInfo.useValue
            //val prodTypeCd = remainInfo.prodTypeCd

            val modifiedSvcNm = svcNm.replace("[SMT]", "")

            // Modify svcTypNm if it contains "패킷데이터" or "음성+영상"
            val modifiedSvcTypNm = when {
                svcTypNm.contains("패킷데이터") -> {
                    svcTypNm.replace("패킷데이터", "데이터")
                }
                svcTypNm.contains("음성+영상") -> {
                    svcTypNm.replace("음성+영상", "부가통화")
                }
                else -> {
                    svcTypNm
                }
            }
            // Append the values to the data string with proper formatting
            dataStringBuilder.append("$modifiedSvcNm\t")
            dataStringBuilder.append(" $modifiedSvcTypNm\n\n")
            dataStringBuilder.appendLine().appendLine()

            if (svcUnitCd.contains("초")) {
                if (alloValue.contains("Z")) {
                    dataStringBuilder.append("총제공량 ${"무제한".padStart(40)}\n\n")
                    val useValueInMinutes = useValue.toDouble() / 60
                    dataStringBuilder.append("사용량  ${useValueInMinutes.format(0).padStart(40)}분\n\n\n\n")
                    dataStringBuilder.appendLine().appendLine()

                    LgtDeductItemList.add(
                        LgtDeductItem(
                            modifiedSvcNm+" $modifiedSvcTypNm",
                            "총제공량",
                            "사용량",
                            "잔여량",
                            "무제한",
                            "${useValueInMinutes.format(0)}분",
                            "\n\n"
                        )
                    )
                    Log.d("LgtDuctDetailViewFramgent","----------------check recyclerView: $LgtDeductItemList\n\n\n--------------------")
                }
                else {
                    val alloValueInMinutes = alloValue.toDouble() / 60
                    val useValueInMinutes = useValue.toDouble() / 60
                    val remainValueMin = alloValueInMinutes - useValueInMinutes
                    dataStringBuilder.append("총제공량 ${alloValueInMinutes.format(0).padStart(40)}분\n\n")
                    dataStringBuilder.append("사용량  ${useValueInMinutes.format(0).padStart(40)}분\n\n")
                    dataStringBuilder.append("잔여량    ${remainValueMin.format(0).padStart(40)}분\n\n\n\n")
                    dataStringBuilder.appendLine().appendLine()

                    LgtDeductItemList.add(
                        LgtDeductItem(
                            modifiedSvcNm+" $modifiedSvcTypNm",
                            "총제공량",
                            "사용량",
                            "잔여량",
                            "${alloValueInMinutes.format(0)}분",
                            "${useValueInMinutes.format(0)}분",
                            "${remainValueMin.format(0)}분\n\n"
                        )
                    )
                    Log.d("LgtDuctDetailViewFramgent","----------------check recyclerView: $LgtDeductItemList\n" +
                            "\n" +
                            "\n--------------------")
                }
            }
            else if (svcUnitCd.contains("건")) {
                if (alloValue.contains("Z")) {
                    dataStringBuilder.append("총제공량 ${"무제한".padStart(40)}\n\n")
                    dataStringBuilder.append("사용량  ${useValue.padStart(40)}건\n\n\n\n")
                    dataStringBuilder.appendLine().appendLine()

                    LgtDeductItemList.add(
                        LgtDeductItem(
                            modifiedSvcNm+" $modifiedSvcTypNm",
                            "총제공량",
                            "사용량",
                            "잔여량",
                            "무제한",
                            "${useValue}건",
                            "\n\n"
                        )
                    )
                    Log.d("LgtDuctDetailViewFramgent","----------------check recyclerView: $LgtDeductItemList\n\n\n--------------------")

                }
                else {
                    dataStringBuilder.append("총제공량 ${alloValue.padStart(40)}건\n\n")
                    dataStringBuilder.append("사용량  ${useValue.padStart(40)}건\n\n")
                    val remainValue = alloValue.toInt() - useValue.toInt()
                    dataStringBuilder.append("잔여량:    ${remainValue.toString().padStart(40)}건\n\n\n\n")
                    dataStringBuilder.appendLine().appendLine()

                    LgtDeductItemList.add(
                        LgtDeductItem(
                            modifiedSvcNm+" $modifiedSvcTypNm",
                            "총제공량",
                            "사용량",
                            "잔여량",
                            "${alloValue}건",
                            "${useValue}건",
                            "${remainValue}건\n\n"
                        )
                    )
                    Log.d("LgtDuctDetailViewFramgent","----------------check recyclerView: $LgtDeductItemList\n\n\n--------------------")
                }
            }
            else if (svcTypNm.contains("패킷")) {
                val alloValueInGB = alloValue.toDouble() / 1024 / 1024
                val useValueInGB = useValue.toDouble() / 1024 / 1024
                val remainValueInGB = alloValueInGB - useValueInGB
                dataStringBuilder.append("총제공량 ${alloValueInGB.format(1).padStart(40)}GB\n\n")
                dataStringBuilder.append("사용량  ${useValueInGB.format(1).padStart(40)}GB\n\n")
                dataStringBuilder.append("잔여량    ${remainValueInGB.format(1).padStart(40)}GB\n\n\n\n")
                dataStringBuilder.appendLine().appendLine()

                LgtDeductItemList.add(
                    LgtDeductItem(
                        modifiedSvcNm+" $modifiedSvcTypNm",
                        "총제공량",
                        "사용량",
                        "잔여량",
                        "${alloValueInGB.format(1)}GB",
                        "${useValueInGB.format(1)}GB",
                        "${remainValueInGB.format(1)}GB\n\n"
                    )
                )
                Log.d("LgtDuctDetailViewFramgent","----------------check recyclerView: $LgtDeductItemList\n\n\n--------------------")
            }
            else {
                dataStringBuilder.append("총제공량: $alloValue\n\n")
                dataStringBuilder.append("사용량:  $useValue\n\n\n\n")
                dataStringBuilder.appendLine().appendLine()

                LgtDeductItemList.add(
                    LgtDeductItem(
                        modifiedSvcNm+" $modifiedSvcTypNm",
                        "총제공량",
                        "사용량",
                        "잔여량",
                        "$alloValue",
                        "$useValue",
                        "\n\n"
                    )
                )
                Log.d("LgtDuctDetailViewFramgent","----------------check recyclerView: $LgtDeductItemList\n\n\n--------------------")
            }

        }
        val adapter=LgtDeductAdapter(LgtDeductItemList)

        val recyclerView = requireView().findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter=adapter

        Log.d("LgtDuctDetailViewFramgent","==================================check recyclerView: $LgtDeductItemList\n\n\n=============================")

    }

    // Extension function to format a Double value with the specified number of decimal places
    private fun Double.format(decimalPlaces: Int): String {
        return String.format("%.${decimalPlaces}f", this)
    }
}



