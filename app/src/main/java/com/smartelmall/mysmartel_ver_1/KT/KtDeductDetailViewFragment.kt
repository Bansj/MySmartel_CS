package com.smartelmall.mysmartel_ver_1.KT

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.gson.Gson
import com.smartelmall.mysmartel_ver_1.R
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.IOException
import kotlin.collections.HashMap

class KtDeductDetailViewFragment : Fragment(), View.OnTouchListener {

    private val TAG = "KtDeductDetailView"
    private lateinit var totaluseTimeTextView: TextView
    private lateinit var voiceCallDetailTextView: TextView
    private lateinit var voiceCallDetailTotTextView: TextView
    private lateinit var totUseTimeCntTextView: TextView
    private lateinit var totUseTimeCntTotTextView: TextView
    private var initialY: Float = 0f

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_kt_deduct_detail_view, container, false)

        totaluseTimeTextView = view.findViewById(R.id.tvTotaluseTime)
        voiceCallDetailTextView = view.findViewById(R.id.tvVoiceCallDetail)
        voiceCallDetailTotTextView = view.findViewById(R.id.tvVoiceCallDetailTot)
        totUseTimeCntTextView = view.findViewById(R.id.tvTotUseTimeCnt)
        totUseTimeCntTotTextView = view.findViewById(R.id.tvTotUseTimeCntTot)

        view.setOnTouchListener(this)

        val moveButton: View = view.findViewById(R.id.btn_pgDown)
        moveButton.setOnClickListener {
            animateFragmentOut(view)
        }

        fetchDeductApiData()
        return view
    }

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                initialY = event.rawY
            }
            MotionEvent.ACTION_MOVE -> {
                val deltaY = event.rawY - initialY
                if (deltaY > 0 && view.y <= 0) {
                    view.translationY = deltaY
                    return true
                }
            }
            MotionEvent.ACTION_UP -> {
                val deltaY = event.rawY - initialY
                if (deltaY > view.height / 2) {
                    animateFragmentOut(view)
                } else {
                    animateFragmentBack(view)
                }
            }
        }
        return false
    }

    private fun animateFragmentOut(view: View) {
        val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_down)
        transaction.remove(this@KtDeductDetailViewFragment)
        transaction.commit()
    }

    private fun animateFragmentBack(view: View) {
        view.animate().translationY(0f).setDuration(300).start()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun fetchDeductApiData() {
        val phoneNumber = arguments?.getString("phoneNumber") ?: ""
        val apiUrl = "https://kt-self.smartelmobile.com/common/api/selfcare/selfcareAPIServer.aspx"

        val requestBody = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            createRequestBody(phoneNumber)
        )
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

                val apiResponse = Gson().fromJson(responseData, KtDeductApiResponse::class.java)

                val totaluseTimeList = mutableListOf<KtDeductApiResponse.BodyData.TotaluseTimeDtoData>()
                val voiceCallDetailList = mutableListOf<KtDeductApiResponse.BodyData.VoiceCallDetailDtoData>()
                val voiceCallDetailTotList = mutableListOf<KtDeductApiResponse.BodyData.VoiceCallDetailTotDtoData>()
                val totUseTimeCntList = mutableListOf<KtDeductApiResponse.BodyData.TotUseTimeCntDtoData>()
                val totUseTimeCntTotList = mutableListOf<KtDeductApiResponse.BodyData.TotUseTimeCntTotDtoData>()

                apiResponse.body?.forEach { bodyData ->
                    bodyData.totaluseTimeDto?.let { totaluseTimeList.addAll(it) }
                    bodyData.voiceCallDetailDto?.let { voiceCallDetailList.addAll(it) }
                    bodyData.voiceCallDetailTotDto?.let { voiceCallDetailTotList.addAll(it) }
                    bodyData.totUseTimeCntDto?.let { totUseTimeCntList.addAll(it) }
                    bodyData.totUseTimeCntTotDto?.let { totUseTimeCntTotList.addAll(it) }
                }
                requireActivity().runOnUiThread {
                    try {
                        setDataToTextViews(totaluseTimeList)
                        //checkAndSendDataToMyInfoFragment(totaluseTimeList)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error updating UI: ${e.message}")
                    }
                }

            }

        })

    }
    private fun createRequestBody(phoneNumber: String): String {
        val requestBody = HashMap<String, List<HashMap<String, String>>>()
        val headerData = HashMap<String, String>()
        val bodyData = HashMap<String, String>()

        headerData["type"] = "X12"
        bodyData["traceno"] = ""
        bodyData["custId"] = ""
        bodyData["ncn"] = ""
        bodyData["ctn"] = phoneNumber
        bodyData["clientIp"] = ""
        bodyData["userId"] = ""
        bodyData["useMonth"] = ""

        requestBody["header"] = listOf(headerData)
        requestBody["body"] = listOf(bodyData)

        return Gson().toJson(requestBody)
    }

    private fun setDataToTextViews(totaluseTimeList: List<KtDeductApiResponse.BodyData.TotaluseTimeDtoData>) {
        val totaluseTimeStringBuilder = StringBuilder()
        totaluseTimeList.forEach { totaluseTime ->
            val strSvcName = totaluseTime.strSvcName
            var strFreeMinTotal = formatValue(totaluseTime.strFreeMinTotal)
            var strFreeMinReMain = formatValue(totaluseTime.strFreeMinReMain)
            var strFreeMinUse = formatValue(totaluseTime.strFreeMinUse)

            if (strSvcName.contains("데이터")) {
                val multiplier = 0.5 / (1024 * 1024)
                strFreeMinUse = formatDataValue(strFreeMinUse, multiplier)
                strFreeMinReMain = formatDataValue(strFreeMinReMain, multiplier)
                strFreeMinTotal = formatDataValue(strFreeMinTotal, multiplier)
            }
            else if (strSvcName.contains("속도제어")) {
                val multiplier = 1.0
                strFreeMinUse = formatDataValue(strFreeMinUse, multiplier)
            }
            else if (strSvcName.contains("음성")) {
                val minutesUse = strFreeMinUse.toIntOrNull() ?: 0
                strFreeMinReMain = "" // Hide strFreeMinReMain
                strFreeMinUse = "${minutesUse / 60}분"

                // Check if strFreeMinTotal is not "무제한" and is numeric
                if (strFreeMinTotal != "무제한") {
                    val minutesTotal = strFreeMinTotal.toIntOrNull() ?: 0
                    strFreeMinTotal = "${minutesTotal / 60}분"
                }
            }
            else if (strSvcName.contains("영상") || strSvcName.contains("통화")) {
                val minutesTotal = strFreeMinTotal.toIntOrNull() ?: 0
                val minutesRemain = strFreeMinReMain.toIntOrNull() ?: 0
                val minutesUse = strFreeMinUse.toIntOrNull() ?: 0
                strFreeMinTotal = "${minutesTotal / 60}분"
                strFreeMinReMain = "${minutesRemain / 60}분"
                strFreeMinUse = "${minutesUse / 60}분"
            }
            else if (strSvcName.contains("SMS") || strSvcName.contains("문자")) {
                val intValue = strFreeMinReMain.toIntOrNull() ?: 0
                if (intValue > 99999) {
                    strFreeMinReMain = "무제한"
                } else {
                    strFreeMinReMain = "${strFreeMinReMain}건"
                }
                strFreeMinTotal = "${strFreeMinTotal}건"
                strFreeMinUse = "${strFreeMinUse}건"
            }

            totaluseTimeStringBuilder.append("\n")
            val spannableSvcName = SpannableString("${totaluseTime.strSvcName}\n")
            spannableSvcName.setSpan(AbsoluteSizeSpan(32, true), 0, spannableSvcName.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            totaluseTimeStringBuilder.append(spannableSvcName)
            totaluseTimeStringBuilder.append("\n")

            totaluseTimeStringBuilder.append(String.format("%-40s %3s%n\n", "총제공량", strFreeMinTotal))
            if (strSvcName.contains("음성") && strFreeMinReMain == "0") {
                // Do not include "잔여량" if strFreeMinReMain is 0
                totaluseTimeStringBuilder.append(String.format("%-40s %3s%n\n", "", strFreeMinReMain))
            } else {
                if (strFreeMinReMain != "0") {
                    totaluseTimeStringBuilder.append(String.format("%-41.5s %3s%n\n", "잔여량", strFreeMinReMain))
                }
                totaluseTimeStringBuilder.append(String.format("%-40s %3s%n\n", "사용량", strFreeMinUse))
            }
            totaluseTimeStringBuilder.append("\n\n\n")
        }
        totaluseTimeTextView.text = totaluseTimeStringBuilder.toString()
    }

    private fun formatValue(value: String): String {
        val intValue = value.toIntOrNull()
        return if (intValue != null && intValue >= 999999999) {
            "무제한"
        } else {
            value
        }
    }
    private fun formatDataValue(value: String, multiplier: Double): String {
        val floatValue = value.toFloatOrNull()
        return if (floatValue != null) {
            val formattedValue = String.format("%.2f GB", floatValue * multiplier)
            formattedValue
        } else {
            value
        }
    }
}