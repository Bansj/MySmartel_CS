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
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.*
import java.nio.charset.Charset
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.Collections.min

class SktBillDetailFragment : Fragment() {

    private lateinit var phoneNumber: String
    private lateinit var currentYearMonth: String
    private lateinit var textView: TextView
    private val yearMonthFormat = SimpleDateFormat("yyyyMM", Locale.getDefault())

    private lateinit var sumAmount: TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_skt_bill_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialize the textView
        textView = view.findViewById(R.id.textViewData)
        sumAmount = view.findViewById(R.id.txt_sumAmount)
        // Get the phoneNumber value from MyInfoFragment
        phoneNumber = arguments?.getString("phoneNumber") ?: ""
        // Set ifClCd value as "R5"
        val ifClCd = "R5"
        // Get the current year and month from the user's mobile phone
        val calendar = Calendar.getInstance()
        val yearMonthFormat = SimpleDateFormat("yyyyMM", Locale.getDefault())
        currentYearMonth = yearMonthFormat.format(calendar.time)
        // Query the API and fetch the data
        fetchBillingDetail()
    }

    private fun fetchBillingDetail() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                // Construct the API URL with parameters
                val url =
                    URL("https://www.mysmartel.com/api/sktGetInfo.php?svcNum=$phoneNumber&ifClCd=R5&addInfo=$currentYearMonth")
                Log.d("BillingDetail", "API URL: $url")
                val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
                connection.setRequestProperty("charset", "euc-kr")
                connection.requestMethod = "GET"
                // Get the response in EUC-KR charset
                val reader = BufferedReader(
                    InputStreamReader(
                        connection.inputStream,
                        Charset.forName("EUC-KR")
                    )
                )
                val response = reader.readText()
                Log.d("BillingDetail", "Response: $response")
                // Check if the "E6" value is included in the response
                if (response.contains("E6")) {
                    Log.d("BillingDetail", "E6 found in the response")
                    val lastMonth = Calendar.getInstance()
                    lastMonth.add(Calendar.MONTH, -1)
                    currentYearMonth = yearMonthFormat.format(lastMonth.time)
                    // Requery the API with last month's data
                    val lastMonthUrl =
                        URL("https://www.mysmartel.com/api/sktGetInfo.php?svcNum=$phoneNumber&ifClCd=R5&addInfo=$currentYearMonth")
                    Log.d("BillingDetail", "Last Month API URL: $lastMonthUrl")
                    val lastMonthConnection: HttpURLConnection =
                        lastMonthUrl.openConnection() as HttpURLConnection
                    lastMonthConnection.setRequestProperty("charset", "euc-kr")
                    lastMonthConnection.requestMethod = "GET"
                    // Get the new response in EUC-KR charset
                    val lastMonthReader = BufferedReader(
                        InputStreamReader(
                            lastMonthConnection.inputStream,
                            Charset.forName("EUC-KR")
                        )
                    )
                    val lastMonthResponse = lastMonthReader.readText()
                    Log.d("BillingDetail", "Last Month Response: $lastMonthResponse")
                    // Display the data in the textView
                    displayData(lastMonthResponse)
                } else {
                    // Display the data in the textView
                    displayData(response)
                }
            } catch (e: Exception) {
                Log.e("BillingDetail", "Error fetching billing detail: ${e.message}")
            }
        }
    }

    private fun displayData(data: String) {
        // Update the UI on the main thread
        GlobalScope.launch(Dispatchers.Main) {
            // Encode the data in UTF-8 to prevent broken characters
            val encodedData = String(data.toByteArray(Charset.forName("UTF-8")), Charset.forName("UTF-8"))

            // Remove 60 characters at the starting of the raw data
            val trueValue = encodedData.substring(60)

            // Remove 32 characters from trueValue
            val realValue = trueValue.substring(32)

            // Cut off 22 characters from realValue
            val totalAmt = realValue.substring(0, 22).trimStart('0')

            // Cut off first 5 characters from the remaining data
            val leftData1 = realValue.substring(22)
            val leftData2 = leftData1.substring(5)

            // Cut off first 80 characters from leftData2 as 청구서대분류명
            val 청구서대분류명 = leftData2.substring(0, 80)

            // Cut off next 80 characters as 청구서소분류명
            val 청구서소분류명 = leftData2.substring(80, 160)

            // Cut off next 80 characters as 청구서항목
            val 청구서항목 = leftData2.substring(160, 240)

            // Cut off next 22 characters as 청구금액
            val 청구금액 = leftData2.substring(240, 262)


            val formattedTotalAmt = NumberFormat.getNumberInstance().format(totalAmt.toInt())
            // Display the values in the textView
            textView.text = "총 납부하실 금액$formattedTotalAmt\n\n\n청구서대분류명: $청구서대분류명\n\n\n" +
                    "청구서소분류명: $청구서소분류명\n\n\n청구서항목: $청구서항목\n\n\n청구금액: $청구금액\n\n"

            sumAmount.text = "${formattedTotalAmt}원"
        }
    }
}


