package com.smartel.mysmartel_ver_1

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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mysmartel_ver_1.R
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.IOException

class KtDeductDetailViewFragment : Fragment(), View.OnTouchListener {

    private val TAG = "KtDeductDetailView"
    private lateinit var totaluseTimeTextView: TextView
    private lateinit var voiceCallDetailTextView: TextView
    private lateinit var voiceCallDetailTotTextView: TextView
    private lateinit var totUseTimeCntTextView: TextView
    private lateinit var totUseTimeCntTotTextView: TextView
    private var initialY: Float = 0f

    private lateinit var sharedViewModel: SharedViewModel

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
        fetchDeductApiData()
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

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

                apiResponse.body.forEach { bodyData ->
                    totaluseTimeList.addAll(bodyData.totaluseTimeDto)
                    voiceCallDetailList.addAll(bodyData.voiceCallDetailDto)
                    voiceCallDetailTotList.addAll(bodyData.voiceCallDetailTotDto)
                    totUseTimeCntList.addAll(bodyData.totUseTimeCntDto)
                    totUseTimeCntTotList.addAll(bodyData.totUseTimeCntTotDto)
                }
                requireActivity().runOnUiThread {
                    setDataToTextViews(
                        totaluseTimeList,

                    )
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
            } else if (strSvcName.contains("속도제어")) {
                val multiplier = 1.0
                strFreeMinUse = formatDataValue(strFreeMinUse, multiplier)
            } else if (strSvcName.contains("음성")) {
                val minutesUse = strFreeMinUse.toIntOrNull() ?: 0
                strFreeMinReMain = "" // Hide strFreeMinReMain
                strFreeMinUse = "${minutesUse / 60}분"
            } else if (strSvcName.contains("영상")) {
                val minutesTotal = strFreeMinTotal.toIntOrNull() ?: 0
                val minutesRemain = strFreeMinReMain.toIntOrNull() ?: 0
                val minutesUse = strFreeMinUse.toIntOrNull() ?: 0
                strFreeMinTotal = "${minutesTotal / 60}분"
                strFreeMinReMain = "${minutesRemain / 60}분"
                strFreeMinUse = "${minutesUse / 60}분"
            } else if (strSvcName.contains("통화")) {
                val minutesTotal = strFreeMinTotal.toIntOrNull() ?: 0
                val minutesUse = strFreeMinUse.toIntOrNull() ?: 0
                val minutesRemain = strFreeMinReMain.toIntOrNull() ?: 0
                strFreeMinTotal = "${minutesTotal / 60}분"
                strFreeMinReMain = "${minutesRemain / 60}분"
                strFreeMinUse = "${minutesUse / 60}분"
            } else if (strSvcName.contains("SMS")) {
                strFreeMinReMain = "" // Hide strFreeMinReMain
            }

            totaluseTimeStringBuilder.append("\n")
            val spannableSvcName = SpannableString("${totaluseTime.strSvcName}\n")
            spannableSvcName.setSpan(AbsoluteSizeSpan(32, true), 0, spannableSvcName.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            totaluseTimeStringBuilder.append(spannableSvcName)
            totaluseTimeStringBuilder.append("\n")
            totaluseTimeStringBuilder.append(String.format("%-30s %20s%n", "총제공량", strFreeMinTotal))
            totaluseTimeStringBuilder.append(String.format("%-30s %20s%n", "잔여량", strFreeMinReMain))
            totaluseTimeStringBuilder.append(String.format("%-30s %20s%n", "사용량", strFreeMinUse))
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
//