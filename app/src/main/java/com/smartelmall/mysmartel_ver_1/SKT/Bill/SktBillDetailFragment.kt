package com.smartelmall.mysmartel_ver_1.SKT.Bill

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smartelmall.mysmartel_ver_1.R
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.*
import java.nio.charset.Charset
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class SktBillDetailFragment : Fragment() { // 당월 청구요금 조회

    private lateinit var phoneNumber: String
    private lateinit var currentYearMonth: String
    private lateinit var textView: TextView
    private val yearMonthFormat = SimpleDateFormat("yyyyMM", Locale.getDefault())

    private lateinit var sumAmount: TextView

    private lateinit var txtTitle: TextView
    private lateinit var txtTitle2: TextView
    private lateinit var txtValue: TextView

    private lateinit var downButton: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_skt_bill_detail, container, false)

        downButton = view.findViewById(R.id.btn_pgDown)
        downButton.setOnClickListener {
            animateFragmentOut(view)
        }
        return view
    }

    private fun animateFragmentOut(view: View) { // 슬라이드 다운 애니메이션 효과
        val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_down)
        transaction.remove(this@SktBillDetailFragment)
        transaction.commit()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the textView
        //textView = view.findViewById(R.id.textViewData)
        sumAmount = view.findViewById(R.id.txt_sumAmount)

  /*      txtTitle = view.findViewById(R.id.txt_title)
        txtTitle2 = view.findViewById(R.id.txt_title2)
        txtValue = view.findViewById(R.id.txt_value)*/
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

                // Add a counter for limiting the number of months to look back
                var counter = 0

                while (counter < 10) { // Limit to -10 months
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

                    // Decrement the month for the next iteration and increment counter by one.
                    currentMonth.add(Calendar.MONTH, -1)

                    counter++
                }
                // Display the data in textView
                displayData(currentResponse)

            } catch (e: Exception) {
                Log.e("BillingDetail", "Error fetching billing detail: ${e.message}")
            }
        }
    }

    // 문자열에 있는 이상한 문자를 제거하는 함수
    private fun removeStrangeChars(input: String): String {
        return input.replace(Regex("[^가-힣0-9\\s]+"), "")
    }

    // 조회된 데이터 처리 및 결과 출력 코드 수정
    private fun displayData(data: String) {
        GlobalScope.launch(Dispatchers.Main) {

            if (data == "조회된 데이터가 없습니다.") { // 조회된 데이터가 없을때 출력한다
                textView.text = data
                return@launch
            }
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
                if (currentIndex >= trueValue.length) {
                    return ""
                }

                val endIndex = if (currentIndex + count > trueValue.length) trueValue.length else currentIndex + count
                val substring = trueValue.substring(currentIndex, endIndex)
                currentIndex += count
                return substring
            }

            // 3. Parse the trueValue according to the format
            val opClCd = consumeBytes(1)
            val opTypCd = consumeBytes(2)
            val svcNum = consumeBytes(12)
            val svAcntNum = consumeBytes(11)

            val INV_YM = consumeBytes(6)
            // Parse the year and month separately
            val year = INV_YM.substring(0, 4)
            val month = INV_YM.substring(4, 6)
            // Format the year and month
            val formattedDate = "${year}년 ${month}월"

            val TOT_INV_AMT = consumeBytes(22).trim()
            val BILL_REC_CNT = consumeBytes(5).trim(' ')
            // 수정된 부분: BILL_REC_CNT가 빈 문자열인 경우에 default 값을 "0"으로 설정
            val billRecCnt = if (BILL_REC_CNT.isEmpty()) 0 else BILL_REC_CNT.toInt()

            // 4. Iterate through the billing items and display additional values
            val stringBuilderDate = StringBuilder()
            stringBuilderDate.append("\n$formattedDate\n\n")

            // 4. Iterate through the billing items
            val stringBuilder2 = StringBuilder()

            fun formatNumber(number: String): String {
                return if (number.isNotEmpty()) {
                    val cleanNumber = number.replace("-", "")  // Remove the negative sign for processing
                    if (cleanNumber.all { it.isDigit() }) {
                        val formattedNumber = NumberFormat.getInstance().format(cleanNumber.toInt())
                        if (number.startsWith("-")) {
                            "-$formattedNumber"  // Add back the negative sign to the formatted number
                        } else {
                            formattedNumber
                        }
                    } else {
                        "0"
                    }
                } else {
                    "0"
                }
            }

            val sktBillingItems = mutableListOf<SktBillingItem>()

            for (i in 0 until billRecCnt) {
                val BILL_ITM_LCL_NM = removeStrangeChars(consumeBytes(80).trim())
                val BILL_ITM_SCL_NM = removeStrangeChars(consumeBytes(80).trim())
                val BILL_ITM_NM = removeStrangeChars(consumeBytes(80).trim())
                val INV_AMT = consumeBytes(22).trimStart('0').trim()

                Log.d("BillingDetail", "대분류명: $BILL_ITM_LCL_NM")
                Log.d("BillingDetail", "소분류명: $BILL_ITM_SCL_NM")
                Log.d("BillingDetail", "항목명: $BILL_ITM_NM")
                Log.d("BillingDetail", "청구금액: $INV_AMT")

                val totalLength = 45 // 이 값을 필요한 전체 문자열 길이로 변경 가능
                val minGap = 20 // 이 값은 두 문자열 사이의 최소 여백 갯수
                val formattedLclNm = BILL_ITM_LCL_NM.padEnd(totalLength - (BILL_ITM_NM.length + INV_AMT.length), ' ')
                val formattedBillItnNM = BILL_ITM_NM.padEnd(BILL_ITM_LCL_NM.length + minGap, ' ')
                //val formattedInvAmt = INV_AMT.padStart()

                stringBuilder2.append("\n$formattedLclNm")
                //stringBuilder2.append("청구서 소분류명: $BILL_ITM_SCL_NM\n\n")
                stringBuilder2.append("\n$formattedBillItnNM")
                val formattedInvAmt = formatNumber(INV_AMT)
                val paddedFormattedInvAmt = "${formattedInvAmt}".padStart(50)
                stringBuilder2.append("\n${paddedFormattedInvAmt}원\n\n")
                Log.d("SktBillDetailFragment","----------check 청구금액: $paddedFormattedInvAmt------------------")

                sktBillingItems.add(SktBillingItem(formattedLclNm, formattedBillItnNM, paddedFormattedInvAmt))
            }
            val recyclerView: RecyclerView = view?.findViewById(R.id.recyclerView) ?: return@launch
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.adapter = SktBillingAdapter(sktBillingItems)

            Log.d("SktBillDetailFragment","----------chceck RecyclerView: $recyclerView")

            val ErrorCode = consumeBytes(2)

            val stringBuilderTotAmt = StringBuilder()
            val title = "총 납부하실 금액 "
            val value = "\n${formatNumber(TOT_INV_AMT.trimStart('0')).padStart(50)}원\n\n"
            val maxSpacing = 15 // Adjust this value as needed for the maximum spacing

            val formattedTitle = title.padEnd(title.length + maxSpacing, ' ')
            val formattedValue = value.padStart(value.length + maxSpacing, ' ')

            stringBuilderTotAmt.append(formattedTitle)
            stringBuilderTotAmt.append(formattedValue)

            //txtTitle.text = stringBuilder2.toString()
            //txtValue.text = stringBuilder2.append("${INV_YM}원")
            //Log.d("BillingDetail", "청구금액: ${txtValue.text}")

            //textView.text = stringBuilderDate.toString() + stringBuilder2.toString()

            val txtMontLoading = requireView().findViewById<TextView>(R.id.txt_monthLoading)
            txtMontLoading.text = stringBuilderDate

            val txtSumAmtTitle = requireView().findViewById<TextView>(R.id.txt_totalAmtTitle)
            txtSumAmtTitle.text = title
            val txtSumAmtValue = requireView().findViewById<TextView>(R.id.txt_totalAmtValue)
            txtSumAmtValue.text = value

            sumAmount.text = "총 ${formatNumber(TOT_INV_AMT.trimStart('0'))}원"

            //textView.gravity = Gravity.START or Gravity.END
            sumAmount.gravity = Gravity.END

            // Log to show the length of each parsed field
            Log.d("ParsedData", "opClCd: ${opClCd.length}\n" +
                    " opTypCd: ${opTypCd.length}\n" +
                    " svcNum: ${svcNum.length}\n" +
                    " svAcntNum: ${svAcntNum.length}\n" +
                    " INV_YM: ${INV_YM.length}\n" +
                    " TOT_INV_AMT: ${TOT_INV_AMT.length}\n" +
                    " BILL_REC_CNT: ${BILL_REC_CNT}\n" +
                    " ErrorCode: ${ErrorCode.length}")
        }
    }
}








