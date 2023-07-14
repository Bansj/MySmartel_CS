package com.smartel.mysmartel_ver_1

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.mysmartel_ver_1.R
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class LgtDeductDetailViewFragment : Fragment() {
    private lateinit var custName: String
    private lateinit var phoneNumber: String

    companion object {
        private const val BASE_URL = "https://www.mysmartel.com/api/lguDeductibleAmt.php"
        private const val TAG = "LgtDeductDetailView"

        fun newInstance(custName: String, phoneNumber: String): LgtDeductDetailViewFragment {
            val fragment = LgtDeductDetailViewFragment()
            val args = Bundle()
            args.putString("custName", custName)
            args.putString("phoneNumber", phoneNumber)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            custName = it.getString("custName", "")
            phoneNumber = it.getString("phoneNumber", "")
        }

        fetchDataFromAPI()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_lgt_deduct_detail_view, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Set up your UI components here
    }

    private fun fetchDataFromAPI() {
        val url = "$BASE_URL?serviceNum=$phoneNumber&custNm=$custName"
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Failed to fetch LGT deduction amount", e)
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                response.close()

                if (responseData != null) {
                    parseResponseData(responseData)
                }
            }
        })
    }

    private fun parseResponseData(responseData: String) {
        try {
            val jsonObject = JSONObject(responseData)
            val remainInfoArray = jsonObject.getJSONArray("remainInfo")

            for (i in 0 until remainInfoArray.length()) {
                val item = remainInfoArray.getJSONObject(i)

                val svcNm = item.getString("svcNm")
                val svcTypNm = item.getString("svcTypNm")
                val svcUnitCd = item.getString("svcUnitCd")
                val alloValue = item.getString("alloValue")
                val useValue = item.getString("useValue")
                val prodTypeCd = item.getString("prodTypeCd")

                Log.d(TAG, "svcNm: $svcNm")
                Log.d(TAG, "svcTypNm: $svcTypNm")
                Log.d(TAG, "svcUnitCd: $svcUnitCd")
                Log.d(TAG, "alloValue: $alloValue")
                Log.d(TAG, "useValue: $useValue")
                Log.d(TAG, "prodTypeCd: $prodTypeCd")

                // Display the data in the UI
                displayDataInUI(svcNm, svcTypNm, svcUnitCd, alloValue, useValue, prodTypeCd)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing response data", e)
        }
    }

    private fun displayDataInUI(
        svcNm: String,
        svcTypNm: String,
        svcUnitCd: String,
        alloValue: String,
        useValue: String,
        prodTypeCd: String
    ) {
        // Example: Displaying data in TextViews
        val rootView = view as LinearLayout

        val svcNmTextView = TextView(requireContext())
        svcNmTextView.text = "Service Name: $svcNm"
        rootView.addView(svcNmTextView)

        val svcTypNmTextView = TextView(requireContext())
        svcTypNmTextView.text = "Service Type: $svcTypNm"
        rootView.addView(svcTypNmTextView)

        val svcUnitCdTextView = TextView(requireContext())
        svcUnitCdTextView.text = "Service Unit: $svcUnitCd"
        rootView.addView(svcUnitCdTextView)

        val alloValueTextView = TextView(requireContext())
        alloValueTextView.text = "Allocation Value: $alloValue"
        rootView.addView(alloValueTextView)

        val useValueTextView = TextView(requireContext())
        useValueTextView.text = "Usage Value: $useValue"
        rootView.addView(useValueTextView)
    }
}





