package com.smartel.mysmartel_ver_1

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    private lateinit var textViewDateView: TextView
    private lateinit var textViewUseDate: TextView
    private lateinit var textViewDetListDto: TextView
    private lateinit var txt_sumAmount: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_kt_payment_detail, container, false)

        // Initialize TextViews
        textViewDateView = view.findViewById(R.id.textViewDateView)
        textViewUseDate = view.findViewById(R.id.textViewUseDate)
        textViewDetListDto = view.findViewById(R.id.textViewDetListDto)
        txt_sumAmount = view.findViewById(R.id.txt_sumAmount)
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
                    val paymentDataString = formatPaymentData(apiResponse)
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
    private fun formatPaymentData(apiResponse: KtPaymentApiResponse): String {
        val bodyData = apiResponse.body?.firstOrNull()

        if (bodyData != null) {
            val stringBuilder = StringBuilder()

            stringBuilder.append("CTN Number: ${bodyData.ctnNumproductionDate} for the current month")
            stringBuilder.append("\n\n")

            val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault())

            bodyData.payMentDto?.forEach { paymentDto ->
                stringBuilder.append("\n")
                val billMonth = paymentDto.billMonth.substring(0, 6)
                stringBuilder.append(String.format("%6s", billMonth).trim().centerJustify(6))
                stringBuilder.append("\n\n")
                stringBuilder.append("청구요금: ${paymentDto.thisMonth}")
                stringBuilder.append("\n")
                stringBuilder.append("미납요금: ${paymentDto.pastDueAmt}")
                stringBuilder.append("\n\n")
            }
            return stringBuilder.toString()
        }
        return "No payment data available"
    }

    private fun String.centerJustify(width: Int): String {
        val padding = width - this.length
        val leftPadding = padding / 2
        val rightPadding = padding - leftPadding
        return " ".repeat(leftPadding) + this + " ".repeat(rightPadding)
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