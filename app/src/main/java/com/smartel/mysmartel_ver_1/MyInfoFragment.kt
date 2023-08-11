package com.smartel.mysmartel_ver_1

import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.mysmartel_ver_1.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class MyInfoFragment : Fragment() {

    private lateinit var txtcustName: TextView
    private lateinit var txtPhoneNumber: TextView
    private lateinit var txtTelecom: TextView
    private lateinit var sharedPrefs: MyInfoSharedPreferences

    private var doubleBackToExitPressedOnce = false

    private lateinit var bannerImage: ImageView

    private lateinit var txt_refreshData: TextView
    private lateinit var btnRefresh: ImageButton

    // Obtain an instance of the ViewModel from the shared ViewModelStoreOwner
    private val viewModel: MyInfoViewModel by viewModels({ requireActivity() })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_info, container, false)

        // Retrieve the arguments
        val custName = arguments?.getString("custName")
        val phoneNumber = arguments?.getString("phoneNumber")
        val Telecom = arguments?.getString("Telecom")
        val serviceAcct = arguments?.getString("serviceAcct")

        if (savedInstanceState != null) {
            viewModel.custName = savedInstanceState.getString("custName")
            viewModel.phoneNumber = savedInstanceState.getString("phoneNumber")
            viewModel.Telecom = savedInstanceState.getString("Telecom")
            viewModel.serviceAcct = savedInstanceState.getString("serviceAcct")
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        txtcustName = view.findViewById(R.id.txt_cust_nm)
        txtPhoneNumber = view.findViewById(R.id.txt_phoneNumber)
        txtTelecom = view.findViewById(R.id.txt_telecom)

        sharedPrefs = MyInfoSharedPreferences(requireContext())

        bannerImage = view.findViewById(R.id.img_banner)

        //loadBanners()

        // Retrieve the data from the ViewModel or arguments
        val custName = viewModel.custName ?: arguments?.getString("custName")
        val phoneNumber = viewModel.phoneNumber ?: arguments?.getString("phoneNumber")
        val Telecom = viewModel.Telecom ?: arguments?.getString("Telecom")
        val serviceAcct = viewModel.serviceAcct ?: arguments?.getString("serviceAcct")

        // Get a reference to the layout_additionalServices view
        val layoutAdditionalServices = view.findViewById<View>(R.id.layout_additionalServices)

        // Check if the Telecom is KT or LGT and hide the layout if necessary
        if (Telecom == "KT" || Telecom == "LGT") {
            layoutAdditionalServices.visibility = View.GONE
        }

        // Log the values
        Log.d("MyInfoFragment", "from get viewModel -----> custName: $custName")
        Log.d("MyInfoFragment", "from get viewModel -----> phoneNumber: $phoneNumber")
        Log.d("MyInfoFragment", "from get viewModel -----> Telecom: $Telecom")
        Log.d("MyInfoFragment", "from get viewModel -----> serviceAcct: $serviceAcct")

        // Set the data in the views
        txtcustName.text = "  ${custName}님, 안녕하세요. "
        txtPhoneNumber.text = "  ✆ [$Telecom] ${phoneNumber ?: "Unknown"} "
        txtTelecom.text = Telecom

        // Set the data in the ViewModel
        viewModel.custName = custName
        viewModel.phoneNumber = phoneNumber
        viewModel.Telecom = Telecom

       //버튼 클릭시 밑에서 위로 올라오는 사용량 상세보기 페이지 클릭이벤트
        val btnDeductDetailFragment = view?.findViewById<Button>(R.id.btn_detailDeduct)
        btnDeductDetailFragment?.setOnClickListener {
            val Telecom = viewModel.Telecom ?:arguments?. getString("Telecom")
            val fragment: Fragment? = when (Telecom) {
                "SKT" -> {
                    val sktDeductDetailViewFragment = SktDeductDetailViewFragment()
                    val bundle = Bundle()
                    bundle.putString("serviceAcct", serviceAcct)
                    bundle.putString("Telecom", Telecom)
                    sktDeductDetailViewFragment.arguments = bundle

                    // Log the values for SKT
                    Log.d("MyInfoFragment", "to SktBillDetailFragment--------------------serviceAcct: $serviceAcct--------------------")
                    Log.d("MyInfoFragment", "to SktBillDetailFragment--------------------phoneNumber: $phoneNumber--------------------")

                    sktDeductDetailViewFragment
                }
                "KT" -> {
                    val ktDeductDetailViewFragment = KtDeductDetailViewFragment()
                    val bundle = Bundle()
                    bundle.putString("phoneNumber", phoneNumber)
                    ktDeductDetailViewFragment.arguments = bundle

                    // Log the values for KT
                    Log.d("MyInfoFragment", "to KtBillDetailFragment--------------------phoneNumber: $phoneNumber--------------------")

                    ktDeductDetailViewFragment
                }
                "LGT" -> {
                    val lgtDeductDetailViewFragment = LgtDeductDetailViewFragment()
                    val bundle = Bundle()
                    bundle.putString("custName", custName)
                    bundle.putString("phoneNumber", phoneNumber)
                    lgtDeductDetailViewFragment.arguments = bundle

                    // Log the values for SKT
                    Log.d("MyInfoFragment", "to LgtBillDetailFragment--------------------custName: $custName--------------------")
                    Log.d("MyInfoFragment", "to LgtBillDetailFragment--------------------phoneNumber: $phoneNumber--------------------")

                    lgtDeductDetailViewFragment
                }
                else -> {
                    Log.e("MyInfoFragment", "Invalid Telecom value: $Telecom")
                    null
                }
            }
            fragment?.let {
                requireFragmentManager().beginTransaction()
                    .setCustomAnimations(
                        R.anim.slide_in_up, // Animation for fragment enter
                        R.anim.slide_out_down, // Animation for fragment exit
                        R.anim.slide_in_up, // Animation for fragment pop-enter
                        R.anim.slide_out_down // Animation for fragment pop-exit
                    )
                    .add(id, it) // Use the ID of any existing container view in your layout
                    .addToBackStack(null)
                    .commit()
            }
        }

        // 버튼을 클릭시 아래에서 위로 올라오는 청구요금 상세보기 페이지 클릭이벤트
        val btnBillDetailFragment = view?.findViewById<Button>(R.id.btn_billDetailDeduct)
        btnBillDetailFragment?.setOnClickListener {
            val telecom = viewModel.Telecom ?: arguments?.getString("Telecom")
            val fragment: Fragment? = when (telecom) {
                "SKT" -> {
                    val skBillDetailFragment = SktBillDetailFragment()
                    val bundle = Bundle()
                    bundle.putString("serviceAcct", serviceAcct)
                    bundle.putString("phoneNumber", phoneNumber)
                    skBillDetailFragment.arguments = bundle

                    // Log the values for SKT
                    Log.d("MyInfoFragment", "to SktBillDetailFragment--------------------serviceAcct: $serviceAcct--------------------")
                    Log.d("MyInfoFragment", "to SktBillDetailFragment--------------------phoneNumber: $phoneNumber--------------------")
                    skBillDetailFragment
                }
                "KT" -> {
                    val ktBillDetailFragment = KtBillDetailFragment()
                    val bundle = Bundle()
                    bundle.putString("phoneNumber", phoneNumber)
                    ktBillDetailFragment.arguments = bundle

                    // Log the values for KT
                    Log.d("MyInfoFragment", "to KtBillDetailFragment--------------------phoneNumber: $phoneNumber--------------------")

                    ktBillDetailFragment
                }
                "LGT" -> {
                    val lgtBillDetailFragment = LgtBillDetailFragment()
                    val bundle = Bundle()
                    bundle.putString("custName", custName)
                    bundle.putString("phoneNumber", phoneNumber)
                    lgtBillDetailFragment.arguments = bundle

                    // Log the values for LGT
                    Log.d("MyInfoFragment", "to LgtBillDetailFragment--------------------custName: $custName--------------------")
                    Log.d("MyInfoFragment", "to LgtBillDetailFragment--------------------phoneNumber: $phoneNumber--------------------")

                    lgtBillDetailFragment
                }
                else -> {
                    Log.e("MyInfoFragment", "Invalid Telecom value: $telecom")
                    null
                }
            }
            fragment?.let {
                requireFragmentManager().beginTransaction()
                    .setCustomAnimations(
                        R.anim.slide_in_up, // Animation for fragment enter
                        R.anim.slide_out_down, // Animation for fragment exit
                        R.anim.slide_in_up, // Animation for fragment pop-enter
                        R.anim.slide_out_down // Animation for fragment pop-exit
                    )
                    .add(id, it) // Use the ID of any existing container view in your layout
                    .addToBackStack(null)
                    .commit()
            }
        }


        // 버튼을 클릭시 아래에서 위로 올라오는 부가서비스 상세보기 페이지 클릭이벤트
        val btnAddServiceFragment = view?.findViewById<Button>(R.id.btn_addService)
        btnAddServiceFragment?.setOnClickListener {
            val telecom = viewModel.Telecom ?: arguments?.getString("Telecom")
            val fragment: Fragment? = when (telecom) {
                "SKT" -> {
                    val sktAddServiceFragment = SktAddServiceFragment()
                    val bundle = Bundle()
                    bundle.putString("serviceAcct", serviceAcct)
                    bundle.putString("phoneNumber", phoneNumber)
                    sktAddServiceFragment.arguments = bundle

                    // Log the values for SKT
                    Log.d("MyInfoFragment", "to SktBillDetailFragment--------------------serviceAcct: $serviceAcct--------------------")
                    Log.d("MyInfoFragment", "to SktBillDetailFragment--------------------phoneNumber: $phoneNumber--------------------")
                    sktAddServiceFragment
                }
                "KT" -> {
                    val ktBillDetailFragment = KtBillDetailFragment()
                    val bundle = Bundle()
                    bundle.putString("phoneNumber", phoneNumber)
                    ktBillDetailFragment.arguments = bundle

                    // Log the values for KT
                    Log.d("MyInfoFragment", "to KtBillDetailFragment--------------------phoneNumber: $phoneNumber--------------------")

                    ktBillDetailFragment
                }
                "LGT" -> {
                    val lgtBillDetailFragment = LgtBillDetailFragment()
                    val bundle = Bundle()
                    bundle.putString("custName", custName)
                    bundle.putString("phoneNumber", phoneNumber)
                    lgtBillDetailFragment.arguments = bundle

                    // Log the values for LGT
                    Log.d("MyInfoFragment", "to LgtBillDetailFragment--------------------custName: $custName--------------------")
                    Log.d("MyInfoFragment", "to LgtBillDetailFragment--------------------phoneNumber: $phoneNumber--------------------")

                    lgtBillDetailFragment
                }
                else -> {
                    Log.e("MyInfoFragment", "Invalid Telecom value: $telecom")
                    null
                }
            }

            fragment?.let {
                requireFragmentManager().beginTransaction()
                    .setCustomAnimations(
                        R.anim.slide_in_up, // Animation for fragment enter
                        R.anim.slide_out_down, // Animation for fragment exit
                        R.anim.slide_in_up, // Animation for fragment pop-enter
                        R.anim.slide_out_down // Animation for fragment pop-exit
                    )
                    .add(id, it) // Use the ID of any existing container view in your layout
                    .addToBackStack(null)
                    .commit()
            }
        }

            // Set click listener for btn_menu button 하단 메뉴이동 네비게이션바 컨트롤러
        view.findViewById<ImageButton>(R.id.btn_menu).setOnClickListener {
            it.findNavController().navigate(R.id.action_myInfoFragment_to_menuFragment)
        }

        // Set click listener for btn_benefit button 하단 설정창이동 네비게이션바 컨트롤러
        view.findViewById<ImageButton>(R.id.btn_setting).setOnClickListener {
            it.findNavController().navigate(R.id.action_myInfoFragment_to_settingFragment)
        }


        // Set click listener for the back button
        //requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPressedCallback)

        txt_refreshData = view.findViewById(R.id.txt_refreshData)
        btnRefresh = view.findViewById(R.id.btn_refresh)

        // Add click listener for btn_refresh button
        btnRefresh.setOnClickListener {
            val serviceAcct = arguments?.getString("serviceAcct")
            val telecom = arguments?.getString("Telecom")
            fetchSktDeductData(serviceAcct, telecom)
        }


    }

    private fun fetchSktDeductData(serviceAcct: String?, telecom: String?) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                // Create a custom trust manager that ignores SSL certificate validation
                val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                    override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}

                    override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}

                    override fun getAcceptedIssuers(): Array<X509Certificate> {
                        return emptyArray()
                    }
                })

                // Set up an OkHttpClient that accepts all SSL certificates
                val okHttpClient = OkHttpClient.Builder()
                    .sslSocketFactory(createUnsafeSslSocketFactory(), trustAllCerts[0] as X509TrustManager)
                    .connectionSpecs(listOf(ConnectionSpec.COMPATIBLE_TLS, ConnectionSpec.CLEARTEXT))
                    .build()

                val url = "https://www.mysmartel.com/smartel/api_deductibleAmount.php"
                val request = Request.Builder()
                    .url(url)
                    .post(
                        JSONObject(
                            mapOf(
                                "svcAcntNum" to serviceAcct,
                                "telecom" to telecom
                            )
                        ).toString().toRequestBody()
                    )
                    .build()

                Log.d("API_CALL", "URL: $url, Params: $serviceAcct, $telecom")

                val response = okHttpClient.newCall(request).execute()
                val responseData = response.body?.string()

                Log.d("API_CALL", "Response: $responseData")

                withContext(Dispatchers.Main) {
                    // Parse the response and display it in the fragment
                    parseAndShowResponse(responseData)
                }
            } catch (e: Exception) {
                Log.e("API_CALL", "Error: ${e.message}")
                // Handle error
            }
        }
    }

    // Helper function to create an SSL socket factory that ignores certificate validation
    private fun createUnsafeSslSocketFactory(): javax.net.ssl.SSLSocketFactory {
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}

            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return emptyArray()
            }
        })

        val sslContext = SSLContext.getInstance(TlsVersion.TLS_1_2.javaName)
        sslContext.init(null, trustAllCerts, java.security.SecureRandom())
        return sslContext.socketFactory
    }

    private fun parseAndShowResponse(responseData: String?) {
        responseData?.let {
            val apiResponse = try {
                val jsonObject = JSONObject(responseData)
                SktDeductApiResponse(
                    jsonObject.getString("svcAcntNum"),
                    jsonObject.getString("RcClCd"),
                    jsonObject.optString("dedtRecCnt", ""), // Use optString to handle missing fields
                    parseRemainInfoList(jsonObject.getJSONArray("remainInfo"))
                )
            } catch (e: Exception) {
                Log.e("API_PARSE", "Parsing Error: ${e.message}")
                // Handle parsing error
                null
            }

            Log.d("API_PARSE", "Parsed Response: $apiResponse")

            // Update the UI with the parsed response data
            apiResponse?.let {
                updateUI(it)
            }
        }
    }

    private fun parseRemainInfoList(jsonArray: JSONArray): List<SktRemainInfo> {
        val remainInfoList = mutableListOf<SktRemainInfo>()
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val remainInfo = SktRemainInfo(
                jsonObject.getString("planId"),
                jsonObject.getString("planNm"),
                jsonObject.getString("skipCode"),
                jsonObject.getString("freePlanName"),
                jsonObject.getString("totalQty"),
                jsonObject.getString("useQty"),
                jsonObject.getString("remQty"),
                jsonObject.getString("unitCd")
            )
            remainInfoList.add(remainInfo)
        }
        return remainInfoList
    }
    private fun updateUI(apiResponse: SktDeductApiResponse) {

        // Display remainInfo details in a separate TextView or a RecyclerView (depending on your layout design)
        val remainInfoTextView = view?.findViewById<TextView>(R.id.txt_refreshData)

        if (apiResponse.remainInfo.isNotEmpty()) {
            val remainInfoStr = StringBuilder()

            for (remainInfo in apiResponse.remainInfo) {
                // If freePlanName is empty, use planNm instead
                val displayName = if (remainInfo.freePlanName.isEmpty()) remainInfo.planNm else remainInfo.freePlanName

                // Default values for when the string is empty or not a valid number
                val totalQtyDefault = if (remainInfo.totalQty.isEmpty()) "0" else remainInfo.totalQty
                val useQtyDefault = if (remainInfo.useQty.isEmpty()) "0" else remainInfo.useQty
                val remQtyDefault = if (remainInfo.remQty.isEmpty()) "0" else remainInfo.remQty

                // Check if freePlanName contains "데이터" or "Data"
                if (displayName.contains("데이터") || displayName.contains("Data")) {
                    // Handle the case where the values are not valid numbers (e.g., "무제한")
                    val totalQtyGB = parseValueToGB(totalQtyDefault)
                    val useQtyGB = parseValueToGB(useQtyDefault)
                    val remQtyGB = parseValueToGB(remQtyDefault)

                    remainInfoStr.append("\n\n$displayName\n\n\n")
                    remainInfoStr.append("총제공량".padEnd(60) + "$totalQtyGB\n\n") // Add padding between label and value
                    remainInfoStr.append("사용량".padEnd(60) + "$useQtyGB\n\n") // Add padding between label and value
                    remainInfoStr.append("잔여량".padEnd(60) + "$remQtyGB\n\n\n\n") // Add padding between label and value
                }
                else if (displayName.contains("음성") || displayName.contains("전화")) {
                    // Handle the case where the values are "음성" or "전화"
                    val totalQtyMin = parseValueToMinutes(remainInfo.totalQty)
                    val useQtyMin = parseValueToMinutes(remainInfo.useQty)
                    val remQtyMin = parseValueToMinutes(remainInfo.remQty)

                    remainInfoStr.append("$displayName\n\n")
                    remainInfoStr.append("총제공량".padEnd(60) + "$totalQtyMin\n\n") // Add padding between label and value
                    remainInfoStr.append("사용량".padEnd(60) + "$useQtyMin\n\n") // Add padding between label and value
                    remainInfoStr.append("잔여량".padEnd(60) + "$remQtyMin\n\n\n\n") // Add padding between label and value
                }
                else {
                    // Default case for other freePlanName values
                    remainInfoStr.append("$displayName\n\n")
                    remainInfoStr.append("총제공량".padEnd(60) + "${remainInfo.totalQty}\n\n")
                    remainInfoStr.append("사용량".padEnd(60) + "${remainInfo.useQty}\n\n")
                    remainInfoStr.append("잔여량".padEnd(60) + "${remainInfo.remQty}\n\n\n\n")
                }
            }
            remainInfoTextView?.text = remainInfoStr.toString()
            remainInfoTextView!!.gravity = Gravity.CENTER

        } else {
            remainInfoTextView?.text = "No Remain Info found."
        }
    }


    // Helper function to convert the value to GB or handle non-numeric cases
    private fun parseValueToGB(value: String): String {
        return try {
            if (!value.contains("무제한") && value.replace(",", "").toDoubleOrNull() != null) {
                val number = value.replace(",", "").toDouble() / (1024 * 1024)
                "%.1fGB".format(number)
            } else {
                value
            }
        } catch (e: NumberFormatException) {
            // Handle non-numeric cases, e.g., "unlimited"
            value
        }
    }
    // Helper function to convert the value to minutes or handle non-numeric cases
    private fun parseValueToMinutes(value: String): String {
        return try {
            if (!value.contains("무제한") && value.replace(",", "").toDoubleOrNull() != null) {
                val number = value.replace(",", "").toDouble() / 60
                "%.0f분".format(number)
            } else {
                value
            }
        } catch (e: NumberFormatException) {
            // Handle non-numeric cases, e.g., "unlimited"
            value
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()

        // Save the data to SharedPreferences before the fragment view is destroyed
        sharedPrefs.custName = txtcustName.text.toString()
        sharedPrefs.phoneNumber = txtPhoneNumber.text.toString()
        sharedPrefs.telecom = txtTelecom.text.toString()
    }
}


/*// 버튼을 클릭시 아래에서 위로 올라오는 사용량 상세보기 페이지 클릭이벤트
 val btnShowFragment = view?.findViewById<Button>(R.id.btn_detailDeduct)
 btnShowFragment?.setOnClickListener {
     val telecom = viewModel.Telecom ?: arguments?.getString("Telecom")

     val fragmentTransaction = requireFragmentManager().beginTransaction()
         .setCustomAnimations(
             R.anim.slide_in_up,
             R.anim.slide_out_down,
             R.anim.slide_in_up,
             R.anim.slide_out_down
         )

     when (telecom) {
         "SKT" -> {
             val sktDeductDetailViewFragment = SktDeductDetailViewFragment()
             val bundle = Bundle()
             bundle.putString("serviceAcct", serviceAcct)
             bundle.putString("Telecom", Telecom)
             sktDeductDetailViewFragment.arguments = bundle
             // Log the values for SKT
             Log.d("MyInfoFragment", "to SktDeductDetailViewFragment--------------------serviceAcct: $serviceAcct--------------------")
             Log.d("MyInfoFragment", "to SktDeductDetailViewFragment--------------------Telecom: $Telecom--------------------")
             sktDeductDetailViewFragment
             //fragmentTransaction.replace(android.R.id.content, sktDeductDetailViewFragment, "SktDeductDetailViewFragment")
         }
         "KT" -> {
             val ktDeductDetailViewFragment = KtDeductDetailViewFragment()
             val bundle = Bundle()
             bundle.putString("phoneNumber", phoneNumber)
             ktDeductDetailViewFragment.arguments = bundle
             // Log the values for KT
             Log.d("MyInfoFragment", "to KtDeductDetailViewFragment--------------------phoneNumber: $phoneNumber--------------------")
             fragmentTransaction.replace(android.R.id.content, ktDeductDetailViewFragment, "KtDeductDetailViewFragment")
         }
         "LGT" -> {
             val lgtDeductDetailViewFragment = LgtDeductDetailViewFragment()
             val bundle = Bundle()
             bundle.putString("custNm", custName)
             bundle.putString("phoneNumber", phoneNumber)
             lgtDeductDetailViewFragment.arguments = bundle
             // Log the values for LGT
             Log.d("MyInfoFragment", "to LgtDeductDetailViewFragment--------------------custName: $custName--------------------")
             Log.d("MyInfoFragment", "to LgtDeductDetailViewFragment--------------------phoneNumber: $phoneNumber--------------------")
             fragmentTransaction.replace(android.R.id.content, lgtDeductDetailViewFragment, "LgtDeductDetailViewFragment")
         }
         else -> {
             Log.e(TAG, "Invalid Telecom value: $telecom")
             return@setOnClickListener
         }
     }
     fragmentTransaction.addToBackStack(null)
     fragmentTransaction.commit()
 }*/


/*  //UI 업데이트 내정보화면에 남은 사용량
        val updateButton = view.findViewById<ImageButton>(R.id.btn_reload)

        val handler = Handler(Looper.getMainLooper())
        val runnable = Runnable {updateButton.performClick()
        }
        handler.postDelayed(runnable,0)

        updateButton.setOnClickListener {
            val ktDeductDetailViewFragment = KtDeductDetailViewFragment()
            val args = Bundle()
            args.putString("phoneNumber", phoneNumber)
            ktDeductDetailViewFragment.arguments = args

            val freeMinRemain2 = arguments?.getString("KtFeeMinRemain")
            if (freeMinRemain2 != null) {
                view.findViewById<TextView>(R.id.txt_leftData).text = freeMinRemain2

            val sktDeductDetailViewFragment = SktDeductDetailViewFragment()
            val args = Bundle()
            args.putString("serviceAcct", serviceAcct)
            sktDeductDetailViewFragment.arguments = args

            val lgtDeductDetailViewFragment = LgtDeductDetailViewFragment()
            val argsl = Bundle()
            args.putString("phoneNumber", phoneNumber)
            args.putString("custName", custName)
            lgtDeductDetailViewFragment.arguments = args

                val toastMessage = "새로고침"
                Toast.makeText(requireContext(), toastMessage, Toast.LENGTH_SHORT).show()
            }
        }*/