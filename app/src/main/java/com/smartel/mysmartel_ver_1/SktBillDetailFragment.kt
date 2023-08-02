package com.smartel.mysmartel_ver_1

import android.media.MediaPlayer.MetricsConstants.ERROR_CODE
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.mysmartel_ver_1.R
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.*
import java.nio.charset.Charset
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.Month
import java.util.*
import java.util.Calendar.MONTH
import java.util.Collections.min
import java.util.regex.Pattern
import kotlin.math.min

class SktBillDetailFragment : Fragment() {

    private lateinit var phoneNumber: String
    private lateinit var currentYearMonth: String
    private lateinit var textView: TextView
    private val yearMonthFormat = SimpleDateFormat("yyyyMM", Locale.getDefault())

    private lateinit var sumAmount: TextView

    private lateinit var tv_code: TextView
    private lateinit var tv_type: TextView
    private lateinit var tv_phone_number: TextView
    private lateinit var tv_account_number: TextView
    private lateinit var tv_month: TextView
    private lateinit var tv_total_amount: TextView
    private lateinit var tv_bill_count: TextView
    private lateinit var ll_billing_details: LinearLayout
    private lateinit var tv_error_code: TextView

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
                var currentResponse = ""
                var currentMonth = Calendar.getInstance()
                currentMonth.add(Calendar.MONTH, -1)

                while (true) {
                    // Log the current year and month
                    Log.d("BillingDetail", "Current Year-Month: ${yearMonthFormat.format(currentMonth.time)}")

                    // Construct the API URL with parameters
                    val url =
                        URL("https://www.mysmartel.com/api/sktGetInfo.php?svcNum=$phoneNumber&ifClCd=R5&addInfo=${yearMonthFormat.format(currentMonth.time)}")
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
                    currentResponse = reader.readText()
                    Log.d("BillingDetail", "Response: $currentResponse")

                    // Check if the "E6" value is included in the response
                    if (!currentResponse.contains("E6")) {
                        // If "E6" is not found, break the loop and display the data
                        break
                    }

                    // Decrement the month for the next iteration
                    currentMonth.add(Calendar.MONTH, -1)
                }

                // Display the data in the textView
                displayData(currentResponse)
            } catch (e: Exception) {
                Log.e("BillingDetail", "Error fetching billing detail: ${e.message}")
            }
        }
    }
    // 1. 한글 포함 여부 확인하는 함수와 데이터 처리 로직 작성
    fun isKoreanIncluded(data: String): Boolean {
        val pattern = Pattern.compile(".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*")
        val matcher = pattern.matcher(data)
        return matcher.find()
    }

    fun processText(text: String): String {
        var processedText = text
        if (isKoreanIncluded(text)) {
            val length = text.length
            val buffer = StringBuffer(length)
            for (i in 0 until length) {
                val c = text[i]
                if (c in '가'..'힣' || c in '\uAC00'..'\uD7AF') {
                    buffer.append(c)
                    buffer.append('\u0000') // 2byte로 처리
                } else {
                    buffer.append(c)
                }
            }
            processedText = buffer.toString()
        }
        return processedText
    }

    // 2. JSON 형식으로 결과 반환하는 함수 작성
    fun makeResultJsonObject(data: MutableList<String>, keyList: MutableList<String>): JSONObject {
        val jsonObject = JSONObject()
        for (i in data.indices) {
            jsonObject.put(keyList[i], data[i].trim())
        }
        return jsonObject
    }

    // 3. 조회된 데이터 처리 및 결과 출력 코드 수정
    private fun displayData(data: String) {
        GlobalScope.launch(Dispatchers.Main) {
            val encodedData = String(data.toByteArray(Charset.forName("UTF-8")), Charset.forName("UTF-8"))

            // 1. Check if the string contains Korean characters and adjust the string accordingly
            val adjustedData = encodedData.chunked(1).joinToString(separator = "") { char ->
                if (char[0].toInt() in 0xAC00..0xD7A3) char + '\u0000' else char
            }

            // 2. Remove the first 60 bytes from the data and create trueValue
            val trueValue = adjustedData.substring(60)

            // Helper function to consume bytes from the string
            var currentIndex = 0
            fun consumeBytes(count: Int): String {
                val substring = trueValue.substring(currentIndex, currentIndex + count)
                currentIndex += count
                return substring
            }

            // 3. Parse the trueValue according to the format
            val opClCd = consumeBytes(1)
            val opTypCd = consumeBytes(2)
            val svcNum = consumeBytes(12)
            val svAcntNum = consumeBytes(11)
            val INV_YM = consumeBytes(6)
            val TOT_INV_AMT = consumeBytes(22)
            val BILL_REC_CNT = consumeBytes(5).trim().toInt()

            // 4. Iterate through the billing items and display additional values
            val stringBuilder1 = StringBuilder()
            stringBuilder1.append("운영구분코드: $opClCd\n")
            stringBuilder1.append("업무구분코드: $opTypCd\n")
            stringBuilder1.append("서비스번호: $svcNum\n")
            stringBuilder1.append("부가서비스 계정번호: $svAcntNum\n")
            stringBuilder1.append("청구년월: $INV_YM\n")
            stringBuilder1.append("총청구금액: ${TOT_INV_AMT.trimStart('0')}원\n")
            stringBuilder1.append("청구서 항목수: $BILL_REC_CNT\n\n")

            // 4. Iterate through the billing items
            val stringBuilder = StringBuilder()
            for (i in 0 until BILL_REC_CNT) {
                val BILL_ITM_LCL_NM = consumeBytes(80).trim()
                val BILL_ITM_SCL_NM = consumeBytes(80).trim()
                val BILL_ITM_NM = consumeBytes(80).trim()
                val INV_AMT = consumeBytes(22).trimStart('0').trim()

                Log.d("BillingDetail", "대분류명: $BILL_ITM_LCL_NM")
                Log.d("BillingDetail", "소분류명: $BILL_ITM_SCL_NM")
                Log.d("BillingDetail", "항목명: $BILL_ITM_NM")
                Log.d("BillingDetail", "청구금액: $INV_AMT")

                stringBuilder.append("청구서 대분류명: $BILL_ITM_LCL_NM\n")
                stringBuilder.append("청구서 소분류명: $BILL_ITM_SCL_NM\n")
                stringBuilder.append("청구서 항목명: $BILL_ITM_NM\n")
                stringBuilder.append("청구금액: ${INV_AMT}원\n\n")
            }

            val ErrorCode = consumeBytes(2)

            textView.text = stringBuilder1.toString() + stringBuilder.toString()

            // Log to show the length of each parsed field
            Log.d("ParsedData", "opClCd: ${opClCd.length}, opTypCd: ${opTypCd.length}, svcNum: ${svcNum.length}, svAcntNum: ${svAcntNum.length}, INV_YM: ${INV_YM.length}, TOT_INV_AMT: ${TOT_INV_AMT.length}, BILL_REC_CNT: ${BILL_REC_CNT}, ErrorCode: ${ErrorCode.length}")
        }
    }



}








