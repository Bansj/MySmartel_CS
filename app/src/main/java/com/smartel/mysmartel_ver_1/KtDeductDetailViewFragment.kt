package com.smartel.mysmartel_ver_1

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mysmartel_ver_1.R
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.IOException

class KtDeductDetailViewFragment : Fragment(), View.OnTouchListener {

    private val TAG = "KtDeductDetailView"
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TotaluseTimeAdapter
    private var initialY: Float = 0f

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_kt_deduct_detail_view, container, false)

        recyclerView = view.findViewById(R.id.rvTotaluseTime)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = TotaluseTimeAdapter(emptyList())
        recyclerView.adapter = adapter

        // Set touch listener for swipe gesture
        view.setOnTouchListener(this)

        // Move fragment down button click event
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
                    // Swipe distance more than half, animate fragment out
                    animateFragmentOut(view)
                } else {
                    // Animate fragment back to initial position
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

        // Fetch API response
        fetchDeductApiData()
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

                // Parse the API response
                val apiResponse = Gson().fromJson(responseData, KtDeductApiResponse::class.java)

                // Update the adapter with new data
                val totaluseTimeList = mutableListOf<KtDeductApiResponse.BodyData.TotaluseTimeDtoData>()
                apiResponse.body.forEach { bodyData ->
                    totaluseTimeList.addAll(bodyData.totaluseTimeDto)
                }
                updateAdapterData(totaluseTimeList)
            }
        })
    }

    private fun updateAdapterData(totaluseTimeList: List<KtDeductApiResponse.BodyData.TotaluseTimeDtoData>) {
        // Run this on the main thread
        requireActivity().runOnUiThread {
            adapter.setData(totaluseTimeList)
        }
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
}
