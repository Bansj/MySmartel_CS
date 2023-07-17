package com.smartel.mysmartel_ver_1

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.mysmartel_ver_1.R
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class KtPaymentDetailFragment : Fragment() {

    private lateinit var textViewDetListDto: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_kt_payment_detail, container, false)

        // Initialize TextViews
        textViewDetListDto = view.findViewById(R.id.textViewDetListDto)

        val moveButton = view.findViewById<ImageButton>(R.id.btn_pgDown)
        moveButton.setOnClickListener {
            animateFragmentOut(view)
        }
        return view
    }
    private fun animateFragmentOut(view: View) {
        val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_down)
        transaction.remove(this@KtPaymentDetailFragment)
        transaction.commit()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchPaymentData()
    }
    private fun fetchPaymentData() {
        val phoneNumber = arguments?.getString("phoneNumber") ?: ""
        val apiUrl = "https://kt-self.smartelmobile.com/common/api/selfcare/selfcareAPIServer.aspx"

        val requestBody = createRequestBody(phoneNumber)
        val request = Request.Builder()
            .url(apiUrl)
            .post(requestBody)
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle API request failure
                Log.e(TAG, "API request failed: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                Log.d(TAG, "API response: $responseData")

                val apiResponse = Gson().fromJson(responseData, KtPaymentApiResponse::class.java)

                requireActivity().runOnUiThread {
                    val paymentDataString = formatPaymentData(apiResponse, requireContext())
                    textViewDetListDto.text = paymentDataString
                }
            }
        })
    }
    private fun createRequestBody(phoneNumber: String): RequestBody {
        val headerData = KtPaymentApiRequest.Header("X15")
        val bodyData = KtPaymentApiRequest.Body(
            traceno = "",
            custId = "",
            ncn = "",
            ctn = phoneNumber,
            clientIp = "",
            userId = "",
            productionDate = getCurrentYearMonth()
        )
        val requestBody = KtPaymentApiRequest(listOf(headerData), listOf(bodyData))

        val gson = Gson()
        val jsonRequestBody = gson.toJson(requestBody)

        return jsonRequestBody.toRequestBody(MEDIA_TYPE_JSON)
    }
    private fun formatPaymentData(apiResponse: KtPaymentApiResponse, context: Context): String {
        val bodyData = apiResponse.body?.firstOrNull()

        if (bodyData != null) {
            val stringBuilder = StringBuilder()

            bodyData.payMentDto?.forEach { paymentDto ->
                stringBuilder.append("\n")
                val year = paymentDto.billMonth.substring(0, 4)
                val month = paymentDto.billMonth.substring(4, 6)
                val formattedBillMonth = "${year}년 ${month}월"

                val screenWidth = getScreenWidth(context)
                val fixedWidth = 70
                val availableWidth = screenWidth.coerceAtMost(fixedWidth)
                val padding = (availableWidth - formattedBillMonth.length) / 2

                val centeredBillMonth = " ".repeat(padding) + formattedBillMonth + " ".repeat(padding)

                stringBuilder.append(centeredBillMonth)
                stringBuilder.append("\n\n")

                val titleWidth = 12 // Fixed width for the title section
                val valueWidth = 12 // Arbitrary width for the value section

                val formattedThisMonth = "청구요금 ${paymentDto.thisMonth}원".splitValueAndAlign(titleWidth, valueWidth)
                stringBuilder.append(formattedThisMonth)
                stringBuilder.append("\n")

                val formattedPastDueAmt = "미납요금 ${paymentDto.pastDueAmt}원".splitValueAndAlign(titleWidth, valueWidth)
                stringBuilder.append(formattedPastDueAmt)
                stringBuilder.append("\n\n\n")
            }
            return stringBuilder.toString()
        }
        return "No payment data available"
    }

    private fun getScreenWidth(context: Context): Int {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels
    }

    fun String.splitValueAndAlign(titleWidth: Int, valueWidth: Int): String {
        val parts = this.split(" ")
        val title = parts[0]
        val value = parts[1]

        val titlePadding = titleWidth - title.length
        val valuePadding = valueWidth - value.length

        val titlePaddingBuilder = StringBuilder()
        val valuePaddingBuilder = StringBuilder()

        for (i in 0 until titlePadding) {
            titlePaddingBuilder.append("\t\t\t\t")
        }
        for (i in 0 until valuePadding) {
            valuePaddingBuilder.append("\t")
        }

        return "$title$titlePaddingBuilder$valuePaddingBuilder$value"
    }

    private fun String.centerJustify(width: Int): String {
        val padding = width - this.length
        val leftPadding = padding / 2
        val rightPadding = padding - leftPadding
        val adjustedLeftPadding = maxOf(leftPadding, 0)
        val adjustedRightPadding = maxOf(rightPadding, 0)
        return " ".repeat(adjustedLeftPadding) + this + " ".repeat(adjustedRightPadding)
    }

    private fun getCurrentYearMonth(): String {
        val currentDate = Calendar.getInstance().time
        val yearMonthFormat = SimpleDateFormat("yyyyMM", Locale.getDefault())
        return yearMonthFormat.format(currentDate)
    }
    companion object {
        private const val TAG = "KtPaymentDetailFragment"
        private val MEDIA_TYPE_JSON = "application/json; charset=utf-8".toMediaType()
    }
}