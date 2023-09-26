package com.smartelmall.mysmartel_ver_1.KT.Payment

import android.content.Context
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.smartelmall.mysmartel_ver_1.R
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

        val paymentsList = mutableListOf<KtPayment>()

        if (bodyData != null) {
            val stringBuilder = StringBuilder()

            bodyData.payMentDto?.forEach { paymentDto ->
                stringBuilder.append("\n")
                val year = paymentDto.billMonth.substring(0, 4)
                val month = paymentDto.billMonth.substring(4, 6)
                val formattedBillMonth = "${year}년 ${month}월"

                val formattedThisMonthAmount= formatToKoreanWon(paymentDto.thisMonth.toLong())
                val formattedPastDueAmtAmount= formatToKoreanWon(paymentDto.pastDueAmt.toLong())

                paymentsList.add(KtPayment(formattedBillMonth, formattedThisMonthAmount, formattedPastDueAmtAmount))

            }
            // 어댑터에 데이터 전달 및 갱신 요청
            val recyclerView = requireView().findViewById<RecyclerView>(R.id.recyclerView)
            recyclerView.layoutManager = LinearLayoutManager(activity)
            val adapter = KtPaymentAdapter(paymentsList)
            recyclerView.adapter = adapter
           //return stringBuilder.toString()
        }
        return "No payment data available"
    }

    fun formatToKoreanWon(amount: Long): String {
        val formatter = NumberFormat.getNumberInstance(Locale.KOREA)
        return formatter.format(amount) + "원"
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