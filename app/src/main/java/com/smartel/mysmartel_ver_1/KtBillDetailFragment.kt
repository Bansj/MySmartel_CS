package com.smartel.mysmartel_ver_1

import android.content.res.Resources
import android.os.Bundle
import android.util.DisplayMetrics
import com.example.mysmartel_ver_1.R

import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class KtBillDetailFragment : Fragment() {

    private lateinit var textViewDateView: TextView
    private lateinit var textViewTraceno: TextView
    private lateinit var textViewUseDate: TextView
    private lateinit var textViewDetListDto: TextView

    private lateinit var txt_sumAmount: TextView
    private lateinit var txt_sumAmount2: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_kt_bill_detail, container, false)

        // Initialize TextViews
        textViewDateView = view.findViewById(R.id.textViewDateView)
        textViewUseDate = view.findViewById(R.id.textViewUseDate)
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
        transaction.remove(this@KtBillDetailFragment)
        transaction.commit()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchBillData()
        txt_sumAmount = view.findViewById(R.id.txt_sumAmount)
        txt_sumAmount2 = view.findViewById(R.id.txt_sumAmount2)
    }

    private fun fetchBillData() {
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
                Log.e(TAG, "API request failed: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                Log.d(TAG, "API response: $responseData")

                val apiResponse = Gson().fromJson(responseData, KtBillApiResponse::class.java)

                val bodyData = apiResponse.body.firstOrNull()

                bodyData?.let {
                    requireActivity().runOnUiThread {
                        setDataToTextViews(it)
                        //checkAndSendDataToMyInfoFragment()
                    }
                }
            }
        })
    }
    private fun createRequestBody(phoneNumber: String): RequestBody {  //Requerst body만들어 발신하기
        val requestBody = HashMap<String, List<HashMap<String, String>>>()
        val headerData = HashMap<String, String>()
        val bodyData = HashMap<String, String>()

        headerData["type"] = "X16"
        bodyData["traceno"] = ""
        bodyData["custId"] = ""
        bodyData["ncn"] = ""
        bodyData["ctn"] = phoneNumber
        bodyData["clientIp"] = ""
        bodyData["userId"] = ""
        bodyData["billSeqNo"] = "0"
        bodyData["billDueDateList"] = getCurrentYearMonth()
        bodyData["billMonth"] = getCurrentYearMonth()
        bodyData["billStartDate"] = "19990909"
        bodyData["billEndDate"] = "19990909"

        requestBody["header"] = listOf(headerData)
        requestBody["body"] = listOf(bodyData)

        val gson = Gson()
        val jsonRequestBody = gson.toJson(requestBody)

        return jsonRequestBody.toRequestBody(MEDIA_TYPE_JSON)
    }

    private fun setDataToTextViews(bodyData: Body) {
        textViewDateView.text = "당월 ${bodyData.dateView}"
        textViewDateView.gravity = Gravity.START

        val screenWidth = Resources.getSystem().displayMetrics.widthPixels

        val detListDtoText = StringBuilder()
        val sumAmountList = mutableListOf<String>()

        val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault())

        bodyData.detListDto.forEach { detListDtoData ->
            val description = detListDtoData.splitDescription
            val amount = numberFormat.format(detListDtoData.actvAmt.toLong())

            detListDtoText.append("\n\n")
            detListDtoText.append(description)
            detListDtoText.append("\n\n")
            detListDtoText.append(String.format("\n%66s", "${amount}원\n"))

            if (description.contains("납부하실 금액", ignoreCase = true)) {
                sumAmountList.add("\n총 ${amount}원")
            }
        }

        textViewDetListDto.text = "\n$detListDtoText"

        /// Set the sumAmountList values to txt_sumAmount
        if (sumAmountList.isNotEmpty()) {
            val sumAmount = sumAmountList.joinToString(separator = "\n")
            txt_sumAmount.text = sumAmount

            //txt_sumAmount2.text = sumAmount
            //txt_sumAmount2.gravity = Gravity.END
        }

        Log.d(TAG, "Data set to TextViews: \n bodyData=$bodyData\n, \n detListDtoText=$detListDtoText \n")
    }
    private fun getCurrentYearMonth(): String {
        val currentDate = Calendar.getInstance().time
        val yearMonthFormat = SimpleDateFormat("yyyyMM", Locale.getDefault())
        return yearMonthFormat.format(currentDate)
    }
    companion object {
        private const val TAG = "KtBillDetailFragment"
        private val MEDIA_TYPE_JSON = "application/json; charset=utf-8".toMediaType()
    }
}