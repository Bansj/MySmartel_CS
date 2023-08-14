package com.smartel.mysmartel_ver_1

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
import androidx.lifecycle.lifecycleScope
import com.example.mysmartel_ver_1.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URLDecoder

class SktPaymentDetailFragment : Fragment() {

    private lateinit var textView: TextView
    private lateinit var phoneNumber: String

    private lateinit var downButton: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("SktPaymentDetailFragment", "onCreateView called")
        val view =inflater.inflate(R.layout.fragment_skt_payment_detail, container, false)

        downButton = view.findViewById(R.id.btn_pgDown)
        downButton.setOnClickListener {
            animateFragmentOut(view)
        }
        return view
    }

    private fun animateFragmentOut(view: View) { // 슬라이드 다운 애니메이션 효과
        val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_down)
        transaction.remove(this@SktPaymentDetailFragment)
        transaction.commit()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("SktPaymentDetailFragment", "onViewCreated called")

        textView = view.findViewById(R.id.textView)
        phoneNumber = arguments?.getString("phoneNumber") ?: ""

        if (phoneNumber.isNotEmpty()) {
            Log.d("SktPaymentDetailFragment", "phoneNumber: $phoneNumber")
            fetchData()
        }
    }

    private fun fetchData() {
        Log.d("SktPaymentDetailFragment", "fetchData called")
        lifecycleScope.launch {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url("https://www.mysmartel.com/api/sktGetInfo.php?svcNum=$phoneNumber&ifClCd=R4")
                .build()

            withContext(Dispatchers.IO) {
                Log.d("SktPaymentDetailFragment", "Request sent")
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()
                if (response.isSuccessful && responseBody != null) {
                    Log.d("SktPaymentDetailFragment", "Request successful")
                    val result = URLDecoder.decode(responseBody, "EUC-KR")
                    withContext(Dispatchers.Main) {
                        displayData(result)
                    }
                } else {
                    Log.d("SktPaymentDetailFragment", "Request failed")
                }
            }
        }
    }

    private fun displayData(data: String) {
        val trueValue = data.substring(60)

        var currentIndex = 0
        fun consumeBytes(count: Int): String {
            val substring = trueValue.substring(currentIndex, currentIndex + count)
            currentIndex += count
            return substring
        }

        val opClCd = consumeBytes(1)
        val opTypCd = consumeBytes(2)
        val svcNum = consumeBytes(12)
        val svAcntNum = consumeBytes(11)
        val FEE_REC_CNT = consumeBytes(5).trim().toInt()

        fun formatCurrency(value: String): String {
            if (value.isEmpty()) {
                return "0"
            }
            return value.toBigInteger().toString().reversed().chunked(3).joinToString(",").reversed()
        }

        val stringBuilder = StringBuilder()
        for (i in 0 until FEE_REC_CNT) {
            val INV_YM = consumeBytes(6).trimStart('0')
            val year = INV_YM.substring(0,4)
            val month = INV_YM.substring(4,6)
            val formattedDate = "${year}년${month}월"
            val INV_AMT = consumeBytes(22).trimStart('0')
            val COL_BAMT = consumeBytes(22).trimStart('0')

            val title = "청구금액"
            val title2 = "미납금액"
            val value = "${formatCurrency(INV_AMT)}원"
            val value2 = "${formatCurrency(COL_BAMT)}원"

            val customTitle = title.padEnd(40)
            val customTitle2 = title2.padEnd(40)

         /*   stringBuilder.append("$formattedDate\n\n")
            stringBuilder.append("청구금액: ${INV_AMT}원\n")
            stringBuilder.append("미납잔액: ${COL_BAMT}원\n\n\n")*/

            stringBuilder.append("$formattedDate\n\n")
            stringBuilder.append("$customTitle")
            stringBuilder.append("$value\n\n")
            stringBuilder.append("$customTitle2")
            stringBuilder.append("$value2\n\n\n\n\n")
        }

        val errorCd = consumeBytes(2)

        val formattedResult = "\n$stringBuilder\n"

        textView.text = formattedResult

        textView.gravity = Gravity.CENTER

        // 각 항목의 길이를 로그에 기록합니다.
        Log.d("displayData", "Lengths: opClCd: ${opClCd.length}, opTypCd: ${opTypCd.length}, svcNum: ${svcNum.length}, svAcntNum: ${svAcntNum.length}, FEE_REC_CNT: $FEE_REC_CNT, errorCd: ${errorCd.length}")
    }

}
