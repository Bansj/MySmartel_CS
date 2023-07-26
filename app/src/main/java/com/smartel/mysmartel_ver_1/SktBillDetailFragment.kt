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
import kotlin.math.min

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
            try {
                val encodedData = String(data.toByteArray(Charset.forName("UTF-8")), Charset.forName("UTF-8"))
                val trueValue = encodedData.drop(60).dropLast(2)
                val dataWithoutTrailing00 = trueValue.dropLast(2)

                val 구분코드 = trueValue.take(1)
                val 업무유형코드 = trueValue.safeSubstring(1, 3)
                val 전화번호 = trueValue.safeSubstring(3, 15)
                val 서비스계정번호 = trueValue.safeSubstring(15, 26)
                val 조회월 = trueValue.safeSubstring(26, 32)
                val 총청구금액 = trueValue.safeSubstring(32, 54).trimStart('0')
                val 청구서건수 = trueValue.safeSubstring(54, 59).trim().toInt()

                val dataRemaining = trueValue.drop(59)
                val 청구서리스트 = mutableListOf<String>()

                for (i in 0 until 청구서건수) {
                    val 대분류명 = dataRemaining.safeSubstring(i * 262, i * 262 + 75).replace("\\s","") //262
                    val 소분류명 = dataRemaining.safeSubstring(i * 252 + 75, i * 262 + 154).replace("\\s","")
                    val 항목명 = dataRemaining.safeSubstring(i * 262 + 154, i * 262 + 240)
                        .replace("\\s","")
                        .trimStart('0')
                    val 청구금액 = dataRemaining.safeSubstring(i * 262 + 240, i * 262 + 253)
                        .replace(Regex("[^\\d]"), "") // 문자열 삭제
                        .replace("\\s", "") // 공백삭제
                        .trimStart('0') // 0 삭제     .trimStart { it == '0' || it.isWhitespace() }
                    청구서리스트.add("\n분류명: $대분류명\n\n소분류: $소분류명\n\n항목명: $항목명\n\n금액: ${청구금액}원\n\n")

                    Log.d("\n\n-----------청구서 출력 ------------\n",
                        "대분류 : $대분류명\n" +
                            "소분류 : $소분류명\n" +
                            "항목명 : $항목명\n" +
                            "청구금액 : $청구금액\n")
                }

                val 에러코드 = dataRemaining.safeSubstring(청구서건수 * 262, 청구서건수 * 262 + 2)
                val 종료문자 = dataRemaining.safeSubstring(청구서건수 * 262 + 2, 청구서건수 * 262 + 3)

                textView.text = "조회 월: $조회월\n\n" +
                        "총 납부하실 금액: ${총청구금액}원\n\n" +
                        "청구서 건수: $청구서건수\n\n\n" +
                        "청구서리스트:\n${청구서리스트.joinToString("\n\n")}\n\n"
            } catch (e: Exception) {
                textView.text = "데이터 처리 중 오류가 발생했습니다:\n${e.message}"
            }
        }
    }

    fun String.safeSubstring(startIndex: Int, endIndex: Int): String {
        if (startIndex >= length) return ""
        return substring(startIndex, min(endIndex, length))
    }

}


