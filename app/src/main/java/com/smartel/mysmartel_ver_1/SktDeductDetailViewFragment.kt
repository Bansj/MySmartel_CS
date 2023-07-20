package com.smartel.mysmartel_ver_1

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.mysmartel_ver_1.R
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

    private lateinit var svcAcntNumTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_skt_deduct_detail_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        svcAcntNumTextView = view.findViewById(R.id.svcAcntNumTextView)

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
                showDataOnUI(it)
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

    private fun showDataOnUI(apiResponse: SktDeductApiResponse) {
        // Update the UI elements with the parsed data
        svcAcntNumTextView.text = "Service Account Number: ${apiResponse.svcAcntNum}"

        // Display remainInfo details in a separate TextView or a RecyclerView (depending on your layout design)
        val remainInfoTextView = view?.findViewById<TextView>(R.id.remainInfoTextView)

        if (apiResponse.remainInfo.isNotEmpty()) {
            val remainInfoStr = StringBuilder()

            for (remainInfo in apiResponse.remainInfo) {
                remainInfoStr.append("Plan ID: ${remainInfo.planId}\n")
                remainInfoStr.append("Plan Name: ${remainInfo.planNm}\n")
                remainInfoStr.append("Skip Code: ${remainInfo.skipCode}\n")
                remainInfoStr.append("Free Plan Name: ${remainInfo.freePlanName}\n")
                remainInfoStr.append("Total Quantity: ${remainInfo.totalQty}\n")
                remainInfoStr.append("Used Quantity: ${remainInfo.useQty}\n")
                remainInfoStr.append("Remaining Quantity: ${remainInfo.remQty}\n")
                remainInfoStr.append("Unit Code: ${remainInfo.unitCd}\n\n")
            }

            remainInfoTextView?.text = remainInfoStr.toString()
        } else {
            remainInfoTextView?.text = "No Remain Info found."
        }
    }

}


