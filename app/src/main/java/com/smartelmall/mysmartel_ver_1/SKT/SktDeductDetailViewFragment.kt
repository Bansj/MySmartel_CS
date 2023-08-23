package com.smartelmall.mysmartel_ver_1.SKT

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.smartelmall.mysmartel_ver_1.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.TlsVersion
import org.json.JSONArray
import org.json.JSONObject
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class SktDeductDetailViewFragment : Fragment() {

    private lateinit var downButton: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_skt_deduct_detail_view, container, false)

        downButton = view.findViewById(R.id.btn_pgDown)
        downButton.setOnClickListener {
            animateFragmentOut(view)
        }
        return view
    }
    private fun animateFragmentOut(view: View) { // 슬라이드 다운 애니메이션 효과
        val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_down)
        transaction.remove(this@SktDeductDetailViewFragment)
        transaction.commit()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get the serviceAcct and telecom values from MyInfoFragment
        val serviceAcct = arguments?.getString("serviceAcct")
        val telecom = arguments?.getString("Telecom")

        // Call the API with the provided values
        fetchSktDeductData(serviceAcct, telecom)
    }

    private fun fetchSktDeductData(serviceAcct: String?, telecom: String?) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                // Create a custom trust manager that ignores SSL certificate validation
                val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                    override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}

                    override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}

                    override fun getAcceptedIssuers(): Array<X509Certificate> {
                        return emptyArray()
                    }
                })

                // Set up an OkHttpClient that accepts all SSL certificates
                val okHttpClient = OkHttpClient.Builder()
                    .sslSocketFactory(createUnsafeSslSocketFactory(), trustAllCerts[0] as X509TrustManager)
                    .connectionSpecs(listOf(ConnectionSpec.COMPATIBLE_TLS, ConnectionSpec.CLEARTEXT))
                    .build()

                val url = "https://www.mysmartel.com/smartel/api_deductibleAmount.php"
                val request = Request.Builder()
                    .url(url)
                    .post(
                        JSONObject(
                            mapOf(
                                "svcAcntNum" to serviceAcct,
                                "telecom" to telecom
                            )
                        ).toString().toRequestBody()
                    )
                    .build()

                Log.d("API_CALL", "URL: $url, Params: $serviceAcct, $telecom")

                val response = okHttpClient.newCall(request).execute()
                val responseData = response.body?.string()

                Log.d("API_CALL", "Response: $responseData")

                withContext(Dispatchers.Main) {
                    // Parse the response and display it in the fragment
                    parseAndShowResponse(responseData)
                }
            } catch (e: Exception) {
                Log.e("API_CALL", "Error: ${e.message}")
                // Handle error
            }
        }
    }

    // Helper function to create an SSL socket factory that ignores certificate validation
    private fun createUnsafeSslSocketFactory(): javax.net.ssl.SSLSocketFactory {
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}

            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return emptyArray()
            }
        })

        val sslContext = SSLContext.getInstance(TlsVersion.TLS_1_2.javaName)
        sslContext.init(null, trustAllCerts, java.security.SecureRandom())
        return sslContext.socketFactory
    }

    private fun parseAndShowResponse(responseData: String?) {
        responseData?.let {
            val apiResponse = try {
                val jsonObject = JSONObject(responseData)
                SktDeductApiResponse(
                    jsonObject.getString("svcAcntNum"),
                    jsonObject.getString("RcClCd"),
                    jsonObject.optString("dedtRecCnt", ""), // Use optString to handle missing fields
                    parseRemainInfoList(jsonObject.getJSONArray("remainInfo"))
                )
            } catch (e: Exception) {
                Log.e("API_PARSE", "Parsing Error: ${e.message}")
                // Handle parsing error
                null
            }

            Log.d("API_PARSE", "Parsed Response: $apiResponse")

            // Update the UI with the parsed response data
            apiResponse?.let {
                updateUI(it)
            }
        }
    }

    private fun parseRemainInfoList(jsonArray: JSONArray): List<SktRemainInfo> {
        val remainInfoList = mutableListOf<SktRemainInfo>()
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val remainInfo = SktRemainInfo(
                jsonObject.getString("planId"),
                jsonObject.getString("planNm"),
                jsonObject.getString("skipCode"),
                jsonObject.getString("freePlanName"),
                jsonObject.getString("totalQty"),
                jsonObject.getString("useQty"),
                jsonObject.getString("remQty"),
                jsonObject.getString("unitCd")
            )
            remainInfoList.add(remainInfo)
        }
        return remainInfoList
    }
    private fun updateUI(apiResponse: SktDeductApiResponse) {

        // Display remainInfo details in a separate TextView or a RecyclerView (depending on your layout design)
        val remainInfoTextView = view?.findViewById<TextView>(R.id.remainInfoTextView)

        if (apiResponse.remainInfo.isNotEmpty()) {
            val remainInfoStr = StringBuilder()

            for (remainInfo in apiResponse.remainInfo) {
                // If freePlanName is empty, use planNm instead
                val displayName = if (remainInfo.freePlanName.isEmpty()) remainInfo.planNm else remainInfo.freePlanName

                // Default values for when the string is empty or not a valid number
                val totalQtyDefault = if (remainInfo.totalQty.isEmpty()) "0" else remainInfo.totalQty
                val useQtyDefault = if (remainInfo.useQty.isEmpty()) "0" else remainInfo.useQty
                val remQtyDefault = if (remainInfo.remQty.isEmpty()) "0" else remainInfo.remQty

                // Check if freePlanName contains "데이터" or "Data"
                if (displayName.contains("데이터") || displayName.contains("Data")) {
                    // Handle the case where the values are not valid numbers (e.g., "무제한")
                    val totalQtyGB = parseValueToGB(totalQtyDefault)
                    val useQtyGB = parseValueToGB(useQtyDefault)
                    val remQtyGB = parseValueToGB(remQtyDefault)

                    remainInfoStr.append("\n\n$displayName\n\n\n")
                    remainInfoStr.append("총제공량".padEnd(60) + "$totalQtyGB\n\n") // Add padding between label and value
                    remainInfoStr.append("사용량".padEnd(60) + "$useQtyGB\n\n") // Add padding between label and value
                    remainInfoStr.append("잔여량".padEnd(60) + "$remQtyGB\n\n\n\n") // Add padding between label and value
                }
                else if (displayName.contains("음성") || displayName.contains("전화")) {
                    // Handle the case where the values are "음성" or "전화"
                    val totalQtyMin = parseValueToMinutes(remainInfo.totalQty)
                    val useQtyMin = parseValueToMinutes(remainInfo.useQty)
                    val remQtyMin = parseValueToMinutes(remainInfo.remQty)

                    remainInfoStr.append("$displayName\n\n")
                    remainInfoStr.append("총제공량".padEnd(60) + "$totalQtyMin\n\n") // Add padding between label and value
                    remainInfoStr.append("사용량".padEnd(60) + "$useQtyMin\n\n") // Add padding between label and value
                    remainInfoStr.append("잔여량".padEnd(60) + "$remQtyMin\n\n\n\n") // Add padding between label and value
                }
                else if (displayName.contains("원")){
                    // Default case for other freePlanName values
                    remainInfoStr.append("$displayName\n\n")
                    remainInfoStr.append("총제공량".padEnd(60) + "${remainInfo.totalQty}원\n\n")
                    remainInfoStr.append("사용량".padEnd(60) + "${remainInfo.useQty}원\n\n")
                    remainInfoStr.append("잔여량".padEnd(60) + "${remainInfo.remQty}원\n\n\n\n")
                }
                else {
                    // Default case for other freePlanName values
                    remainInfoStr.append("$displayName\n\n")
                    remainInfoStr.append("총제공량".padEnd(60) + "${remainInfo.totalQty}\n\n")
                    remainInfoStr.append("사용량".padEnd(60) + "${remainInfo.useQty}\n\n")
                    remainInfoStr.append("잔여량".padEnd(60) + "${remainInfo.remQty}\n\n\n\n")
                }
            }
            remainInfoTextView?.text = remainInfoStr.toString()
            remainInfoTextView!!.gravity = Gravity.CENTER

        } else {
            remainInfoTextView?.text = "No Remain Info found."
        }
    }


    // Helper function to convert the value to GB or handle non-numeric cases
    private fun parseValueToGB(value: String): String {
        return try {
            if (!value.contains("무제한") && value.replace(",", "").toDoubleOrNull() != null) {
                val number = value.replace(",", "").toDouble() / (1024 * 1024)
                "%.1fGB".format(number)
            } else {
                value
            }
        } catch (e: NumberFormatException) {
            // Handle non-numeric cases, e.g., "unlimited"
            value
        }
    }
    // Helper function to convert the value to minutes or handle non-numeric cases
    private fun parseValueToMinutes(value: String): String {
        return try {
            if (!value.contains("무제한") && value.replace(",", "").toDoubleOrNull() != null) {
                val number = value.replace(",", "").toDouble() / 60
                "%.0f분".format(number)
            } else {
                value
            }
        } catch (e: NumberFormatException) {
            // Handle non-numeric cases, e.g., "unlimited"
            value
        }
    }

}