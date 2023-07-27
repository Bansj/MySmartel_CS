package com.smartel.mysmartel_ver_1

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.mysmartel_ver_1.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.Request.*
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*

class SktPaymentDetailFragment : Fragment() {
    private lateinit var phoneNumber: String
    private lateinit var textView: TextView

    companion object {
        const val BASE_URL = "https://www.mysmartel.com/api/sktGetInfo.php"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_skt_payment_detail, container, false)
        textView = rootView.findViewById(R.id.textView)
        // Retrieve phoneNumber from arguments
        phoneNumber = arguments?.getString("phoneNumber") ?: ""
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchDataFromApi()
    }

    private fun fetchDataFromApi() {
        val phoneNumber = phoneNumber // Assuming you've obtained phoneNumber from MenuFragment.kt
        val ifClCd = "R4"
        val url = "$BASE_URL?svcNum=$phoneNumber&ifClCd=$ifClCd"
        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("API_CALL", "Failed to fetch data from the API")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    val decodedData = responseBody?.toUtf8String()
                    // Display the decoded data in textView and process it
                    activity?.runOnUiThread {
                        processDataAndDisplay(decodedData)
                    }
                    Log.d("API_CALL", "Data: $decodedData")
                } else {
                    Log.e("API_CALL", "API returned an error. Code: ${response.code}")
                }
            }
        })
    }

    // Function to decode EUC-KR data to UTF-8
    private fun String.toUtf8String(): String {
        return this.toByteArray(Charset.forName("EUC-KR")).toString(Charset.forName("UTF-8"))
    }

    private fun processDataAndDisplay(data: String?) {
        if (data.isNullOrEmpty() || data.length < 60) {
            textView.text = "Invalid data received from the API"
            return
        }

        // Remove the first 60 characters
        val trimmedData = data.substring(60)

        // Split the remaining data into parts
        val parts = trimmedData.chunked(2)

        if (parts.size < 54) {
            textView.text = "Invalid data format"
            return
        }

        // Extract the required values
        val 구분코드 = parts[0]
        val 업무유형코드 = parts[1]
        val 전화번호 = parts[2]
        val 서비스계정번호 = parts[3]
        val 청구건수 = parts[4].toInt()

        // Log the extracted values
        Log.d("API_CALL", "구분코드: $구분코드")
        Log.d("API_CALL", "업무유형코드: $업무유형코드")
        Log.d("API_CALL", "전화번호: $전화번호")
        Log.d("API_CALL", "서비스계정번호: $서비스계정번호")
        Log.d("API_CALL", "청구건수: $청구건수")

        // Process and display 청구월, 청구금액, 미납잔액 for each item
        val resultText = StringBuilder()
        var currentIndex = 5
        for (i in 1..청구건수) {
            val 청구월 = parts[currentIndex]
            val 청구금액 = parts[currentIndex + 1]
            val 미납잔액 = parts[currentIndex + 2]

            // Log 청구월, 청구금액, 미납잔액 for each item
            Log.d("API_CALL", "청구월 [$i]: $청구월")
            Log.d("API_CALL", "청구금액 [$i]: $청구금액")
            Log.d("API_CALL", "미납잔액 [$i]: $미납잔액")

            // Append the data to the resultText
            resultText.append("청구월 [$i]: $청구월, 청구금액 [$i]: $청구금액, 미납잔액 [$i]: $미납잔액\n")

            currentIndex += 3
        }

        // Display the processed data in textView
        textView.text = resultText.toString()
    }
}
