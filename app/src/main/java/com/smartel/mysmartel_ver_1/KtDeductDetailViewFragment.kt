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
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class KtDeductDetailViewFragment : Fragment() {

    private val client = OkHttpClient()

    private lateinit var textViewResultValue: TextView
    private lateinit var textViewTraceNoValue: TextView
    private lateinit var textViewResultCdValue: TextView
    private lateinit var textViewResultMsgValue: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_kt_deduct_detail_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize TextViews
        textViewResultValue = view.findViewById(R.id.textViewResultValue)
        textViewTraceNoValue = view.findViewById(R.id.textViewTraceNoValue)
        textViewResultCdValue = view.findViewById(R.id.textViewResultCdValue)
        textViewResultMsgValue = view.findViewById(R.id.textViewResultMsgValue)

        // Get the phone number from arguments or any other means
        val phoneNumber = arguments?.getString("phoneNumber") ?: ""

        // Use the phoneNumber as needed in your code
        Log.d(TAG, "\n-----------Received phoneNumber: $phoneNumber---------")

        // Create the request body
        val requestBody = KtDeductRequestBody(
            listOf(KtDeductRequestBody.Header("X12")),
            listOf(KtDeductRequestBody.Body("", "", "", phoneNumber, "", "", ""))
        )

        // Convert the request body to JSON
        val jsonMediaType = "application/json; charset=utf-8".toMediaType()
        val requestJson = requestBody.toJsonString()
        val requestBodyJson = requestJson.toRequestBody(jsonMediaType)

        // Create the request
        val request = Request.Builder()
            .url("https://kt-self.smartelmobile.com/common/api/selfcare/selfcareAPIServer.aspx")
            .addHeader("Content-Type", "application/json")
            .post(requestBodyJson)
            .build()

        // Execute the request asynchronously
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "API request failed: ${e.message}")
                activity?.runOnUiThread {
                    textViewResultValue.text = "API request failed"
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                Log.d(TAG, "API response: $responseBody")
                activity?.runOnUiThread {
                    handleResponse(responseBody)
                }
            }
        })
    }

    private fun handleResponse(responseBody: String?) {
        if (responseBody.isNullOrEmpty()) {
            textViewResultValue.text = "Empty response"
            return
        }

        try {
            val apiResponse = parseApiResponse(responseBody)
            val body = apiResponse.body.firstOrNull()

            if (body?.result == "N") {
                val errorMessage = "Error - ${body.resultCd}: ${body.resultMsg}"
                Log.d(TAG, "API response: $errorMessage")
                activity?.runOnUiThread {
                    textViewResultValue.text = errorMessage
                    textViewTraceNoValue.text = ""
                    textViewResultCdValue.text = body.resultCd
                    textViewResultMsgValue.text = body.resultMsg ?: ""
                }
            } else {
                // Update UI with the desired information
                activity?.runOnUiThread {
                    textViewResultValue.text = "Success"
                    textViewTraceNoValue.text = body?.traceno ?: ""
                    textViewResultCdValue.text = body?.resultCd
                    textViewResultMsgValue.text = body?.resultMsg ?: ""

                    // Update other TextViews or UI elements as needed
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse API response: ${e.message}")
            activity?.runOnUiThread {
                textViewResultValue.text = "Failed to parse response"
                textViewTraceNoValue.text = ""
                textViewResultCdValue.text = ""
                textViewResultMsgValue.text = ""
            }
        }
    }

    private fun parseApiResponse(responseBody: String): KtDeductApiResponse {
        val jsonObject = JSONObject(responseBody)
        val headers = jsonObject.optJSONArray("header")
        val bodies = jsonObject.optJSONArray("body")

        val headerList = mutableListOf<KtDeductApiResponse.Header>()
        val bodyList = mutableListOf<KtDeductApiResponse.Body>()

        headers?.let {
            for (i in 0 until headers.length()) {
                val headerObject = headers.optJSONObject(i)
                val type = headerObject?.optString("type") ?: ""
                val header = KtDeductApiResponse.Header(type)
                headerList.add(header)
            }
        }

        bodies?.let {
            for (i in 0 until bodies.length()) {
                val bodyObject = bodies.optJSONObject(i)
                val traceno = bodyObject?.optString("traceno") ?: ""
                val result = bodyObject?.optString("result") ?: ""
                val resultCd = bodyObject?.optString("resultCd") ?: ""
                val resultMsg = bodyObject?.optString("resultMsg") ?: ""
                val body = KtDeductApiResponse.Body(traceno, result, resultCd, resultMsg)
                bodyList.add(body)
            }
        }

        return KtDeductApiResponse(headerList, bodyList)
    }

    companion object {
        private const val TAG = "KtDeductDetailViewFragment"
    }
}