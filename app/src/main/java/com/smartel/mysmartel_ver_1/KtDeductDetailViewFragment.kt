package com.smartel.mysmartel_ver_1


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class KtDeductDetailViewFragment : Fragment() {

    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get the phone number from arguments or any other means
        val phoneNumber = arguments?.getString("phoneNumber") ?: ""

        // Create the request body
        val requestBody = KtDeductRequestBody(
            listOf(KtDeductRequestBody.Header("X12")),
            listOf(KtDeductRequestBody.Body("", "", "", phoneNumber, "", "", ""))
        )

        // Convert the request body to JSON
        val jsonMediaType = "application/json; charset=utf-8".toMediaType()
        val requestJson = requestBody.toString()
        val requestBodyJson = requestJson.toRequestBody(jsonMediaType)

        // Create the request
        val request = Request.Builder()
            .url("https://kt-self.smartelmobile.com/common/api/selfcare/selfcareAPIServer.aspx")
            .post(requestBodyJson)
            .build()

        // Execute the request asynchronously
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "API request failed: ${e.message}")
                // Handle the failure case here
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                Log.d(TAG, "API response: $responseBody")
                // Handle the response and update UI here
            }
        })
    }

    companion object {
        private const val TAG = "KtDeductDetailViewFragment"
    }
}

















