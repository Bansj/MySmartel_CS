package com.smartel.mysmartel_ver_1

import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.mysmartel_ver_1.R
import com.google.gson.Gson
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class MyInfoFragment : Fragment() {

    private val yearMonthFormat = SimpleDateFormat("yyyyMM", Locale.getDefault())

    private lateinit var pgBarTotalData: ProgressBar

    private lateinit var txtcustName: TextView
    private lateinit var txtPhoneNumber: TextView
    private lateinit var txtTelecom: TextView
    private lateinit var sharedPrefs: MyInfoSharedPreferences

    private var doubleBackToExitPressedOnce = false

    private lateinit var bannerImage: ImageView

    private lateinit var txt_refreshData: TextView
    private lateinit var btnRefresh: ImageButton


    private lateinit var custName: String // Lgt 사용량, 청구요금조회, 부가서비스조회
    private lateinit var phoneNumber: String // Skt 사용량, 청구요금조회, 부가서비스 조회

    private lateinit var txtThisMonthBillDate: TextView
    private lateinit var txtSumAmount: TextView

    private lateinit var svcNum: String

    private lateinit var txtFreeService: TextView
    private lateinit var txtPaidService: TextView

    private val handler = Handler(Looper.getMainLooper())

    // Obtain an instance of the ViewModel from the shared ViewModelStoreOwner
    private val viewModel: MyInfoViewModel by viewModels({ requireActivity() })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_info, container, false)

        if (savedInstanceState != null) {
            viewModel.custName = savedInstanceState.getString("custName")
            viewModel.phoneNumber = savedInstanceState.getString("phoneNumber")
            viewModel.Telecom = savedInstanceState.getString("Telecom")
            viewModel.serviceAcct = savedInstanceState.getString("serviceAcct")
        }
        return view
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("custName", viewModel.custName)
        outState.putString("phoneNumber", viewModel.phoneNumber)
        outState.putString("Telecom", viewModel.Telecom)
        outState.putString("serviceAcct", viewModel.serviceAcct)
    }
    fun formatPhoneNumber(rawPhoneNumber: String?): String {
        if (rawPhoneNumber == null) {
            return "Unknown"
        }

        val normalizedPhoneNumber = rawPhoneNumber.replace("[^0-9]".toRegex(), "")

        if (normalizedPhoneNumber.length != 10) {
            return rawPhoneNumber
        }

        val formattedPhoneNumber = "${normalizedPhoneNumber.substring(0, 3)}-${normalizedPhoneNumber.substring(3, 7)}-${normalizedPhoneNumber.substring(7)}"
        return formattedPhoneNumber
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        txtcustName = view.findViewById(R.id.txt_cust_nm)
        txtPhoneNumber = view.findViewById(R.id.txt_phoneNumber)
        txtTelecom = view.findViewById(R.id.txt_telecom)

        txtSumAmount = view.findViewById(R.id.txt_thisMonthBill)
        txtThisMonthBillDate =view.findViewById(R.id.txt_thisMonthBillDate)

        txtFreeService = view.findViewById(R.id.txt_freeService)
        txtPaidService = view.findViewById(R.id.txt_paidService)

        sharedPrefs = MyInfoSharedPreferences(requireContext())

        bannerImage = view.findViewById(R.id.img_banner)

        //loadBanners()

        // Retrieve the data from the ViewModel or arguments
        val custName = viewModel.custName ?: arguments?.getString("custName")?.also { viewModel.custName = it }
        val phoneNumber = viewModel.phoneNumber ?: arguments?.getString("phoneNumber")?.also { viewModel.phoneNumber = it }
        val Telecom = viewModel.Telecom ?: arguments?.getString("Telecom")?.also { viewModel.Telecom = it }
        val serviceAcct = viewModel.serviceAcct ?: arguments?.getString("serviceAcct")?.also { viewModel.serviceAcct = it }

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

        // 전화번호 형시으로 커스텀 함수
        val formattedPhoneNumber = if (phoneNumber != null) {
            String.format("%s-%s-%s", phoneNumber.substring(0, 3), phoneNumber.substring(3, 7), phoneNumber.substring(7))
        } else {
            "Unknown"
        }
        // Set the data in the views
        txtcustName.text = "  ${custName}님, 안녕하세요. "
        txtPhoneNumber.text = " ✆ [$Telecom] $formattedPhoneNumber"
        txtTelecom.text = Telecom

        // Set the data in the ViewModel
        viewModel.custName = custName
        viewModel.phoneNumber = phoneNumber
        viewModel.Telecom = Telecom
        viewModel.serviceAcct = serviceAcct

        // Log the values
        Log.d("MyInfoFragment", "viewModel -----> custName: ${viewModel.custName}")
        Log.d("MyInfoFragment", "viewModel -----> phoneNumber: ${viewModel.phoneNumber}")
        Log.d("MyInfoFragment", "viewModel -----> Telecom: ${viewModel.Telecom}")
        Log.d("MyInfoFragment", "viewModel -----> serviceAcct: ${viewModel.serviceAcct}")

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

        Log.d("getString?","phoneNumber: $phoneNumber")

        txt_refreshData = view.findViewById(R.id.txt_refreshData)
        btnRefresh = view.findViewById(R.id.btn_refresh)

        // Add click listener for btn_refresh button
        btnRefresh.setOnClickListener {

            val serviceAcct = arguments?.getString("serviceAcct")
            val telecom = arguments?.getString("Telecom")
            this.phoneNumber = arguments?.getString("phoneNumber") ?: ""
            svcNum = this.phoneNumber
            //this.custName = arguments?.getString("custName") ?: ""

            when (telecom) {
                "SKT" -> {
                    SktFetchDeductData(serviceAcct, telecom)
                    SktFetchBillingDetail()
                    SktFetchAddServiceDetail()
                }
                "LGT" -> {
                    LgtDeductFetchData()
                    LgtBillFetchData(phoneNumber, custName)
                }
                "KT" -> {
                    KtFetchDeductApiData()
                    KtFetchBillData()
                }
                else -> {
                    // Handle cases where telecom value is not recognized
                    // Replace these comments with the actual handling code
                }
            }
        }
        btnRefresh.performClick() // 화면 전환 완료시 자동으로 버튼 클릭되는 이벤트
    }

    private fun KtFetchBillData() {  // KT 당월 청구요금 API 조회
        val phoneNumber = arguments?.getString("phoneNumber") ?: ""
        val apiUrl = "https://kt-self.smartelmobile.com/common/api/selfcare/selfcareAPIServer.aspx"

        val requestBody = createBillRequestBody(phoneNumber)
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
    private fun createBillRequestBody(phoneNumber: String): RequestBody {  //Requerst body만들어 발신하기
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
        val formattedDate = SimpleDateFormat("yyyy년 MM월", Locale.getDefault()).format(Date())
        txtThisMonthBillDate.text = "${formattedDate}"

        Log.d("---------MyInfoFragment----- 당월", "${bodyData.dateView}")

        val detListDtoText = StringBuilder()
        val sumAmountList = mutableListOf<String>()

        val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault())

        bodyData.detListDto.forEach { detListDtoData ->
            val description = detListDtoData.splitDescription
            val amount = numberFormat.format(detListDtoData.actvAmt.toLong())

            detListDtoText.append("\n\n")
            detListDtoText.append(description)
            detListDtoText.append("\n")
            detListDtoText.append(String.format("\n%66s", "${amount}원\n"))

            if (description.contains("납부하실 금액", ignoreCase = true)) {
                sumAmountList.add("${amount}원")
            }
        }

        /// Set the sumAmountList values to txt_sumAmount
        if (sumAmountList.isNotEmpty()) {
            val sumAmount = sumAmountList.joinToString(separator = "\n")
            txtSumAmount.text = sumAmount

            Log.d("-----------MyInfoFragment----- 청구금액 합계", "$sumAmount")
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










    private fun KtFetchDeductApiData() {  // KT 사용량 API 조회
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

        val txtRefreshCall = view?.findViewById<TextView>(R.id.txt_refreshCall)
        val txtRefreshM = view?.findViewById<TextView>(R.id.txt_refreshM)
        val txtRefreshData = view?.findViewById<TextView>(R.id.txt_refreshData)
        val txtRefreshTotalData = view?.findViewById<TextView>(R.id.txt_totalData)

        pgBarTotalData = view?.findViewById(R.id.pgBar_leftData)!!

        // 프로그레스바 커스텀
        fun updateProgressBar(total: Double, remain: Double, durationInMillis:Long = 1000) {
            val progressPercent = (remain / total) * 100
            val animation = ObjectAnimator.ofInt(pgBarTotalData,"progress",pgBarTotalData.progress,progressPercent.toInt())

            // 애니메이션 지속시간 설정
            animation.duration = durationInMillis

            // 애니메이션 인터폴레이터 설정 (선택 사항)
            val decelerateInterpolator: TimeInterpolator = DecelerateInterpolator()
            animation.interpolator = decelerateInterpolator

            // 애니메이션 시작
            animation.start()
            //pgBarTotalData.progress = progressPercent.toInt()
        }

        val totalUseTimeStringBuilder = StringBuilder()
        var totalStrFreeMinTotal: Double = 0.0
        var totalStrFreeMinRemain: Double = 0.0
        var displayCall = ""
        var displayM = ""

        val strFreeMinRemainCall = StringBuilder()

        totaluseTimeList.forEach { totaluseTime ->
            val strSvcName = totaluseTime.strSvcName
            var strFreeMinTotal = formatValue(totaluseTime.strFreeMinTotal)
            var strFreeMinReMain = formatValue(totaluseTime.strFreeMinReMain)
            var strFreeMinUse = formatValue(totaluseTime.strFreeMinUse)

            if (strSvcName == "데이터-합계") {
                val floatValue = strFreeMinReMain.toFloatOrNull()
                val floatTotalValue = strFreeMinTotal.toFloatOrNull()
                if (floatValue != null || floatTotalValue != null) {
                    totalStrFreeMinTotal += floatTotalValue!! * 0.5 / (1024 * 1024)
                    totalStrFreeMinRemain += floatValue!! * 0.5 / (1024 * 1024)

                    Log.d("-----------MyInfoFragment----------총제공량", "$totalStrFreeMinTotal---------")

                updateProgressBar(totalStrFreeMinTotal, totalStrFreeMinRemain)
                }
            } else if (strSvcName.contains("속도제어")) {
                val floatValue = strFreeMinReMain.toFloatOrNull()
                if (floatValue != null) {
                    totalStrFreeMinRemain += floatValue
                }
            }

            else if (strSvcName =="영상/부가") {
                val minutesTotal = strFreeMinTotal.toIntOrNull() ?: 0
                val minutesRemain = strFreeMinReMain.toIntOrNull() ?: 0
                val minutesUse = strFreeMinUse.toIntOrNull() ?: 0
                strFreeMinTotal = "${minutesTotal / 60}분"
                strFreeMinReMain = "${minutesRemain / 60}분"
                strFreeMinUse = "${minutesUse / 60}분"

            }
            else if (strSvcName == "음성" || strSvcName == "음성/영상") {
                val minutesUse = strFreeMinUse.toIntOrNull() ?: 0
                val minutesRemain = strFreeMinReMain.toIntOrNull() ?: 0
                strFreeMinReMain = "${minutesRemain /60}분" // Hide strFreeMinReMain
                strFreeMinUse = "${minutesUse / 60}분"

                // Check if strFreeMinTotal is not "무제한" and is numeric
                if (strFreeMinTotal != "무제한") {
                    val minutesTotal = strFreeMinTotal.toIntOrNull() ?: 0
                    strFreeMinTotal = "${minutesTotal / 60}분"
                }

                displayCall = if (strFreeMinTotal.equals("무제한", ignoreCase = true)) {
                    "✆︎ 무제한 / 무제한"
                } else {
                    "✆︎ $strFreeMinReMain / $strFreeMinTotal"
                }

            }
            else if (strSvcName.contains("부가")) {
                val minutesTotal = strFreeMinTotal.toIntOrNull() ?: 0
                val minutesRemain = strFreeMinReMain.toIntOrNull() ?: 0
                val minutesUse = strFreeMinUse.toIntOrNull() ?: 0
                strFreeMinTotal = "${minutesTotal / 60}분"
                strFreeMinReMain = "${minutesRemain / 60}분"
                strFreeMinUse = "${minutesUse / 60}분"

                //displayCall = "✆ $strFreeMinReMain / $strFreeMinTotal"
            }
            else if (strSvcName.contains("SMS")) {
                val intValue = strFreeMinReMain.toIntOrNull() ?: 0
                if (intValue > 99999) {
                    strFreeMinReMain = "무제한"
                } else {
                    strFreeMinReMain = "${strFreeMinReMain}건"
                }
                strFreeMinTotal = "${strFreeMinTotal}건"
                strFreeMinUse = "${strFreeMinUse}건"

                displayM = if (strFreeMinReMain.equals("무제한", ignoreCase = true)) {
                    "✉︎ $strFreeMinUse / 무제한"
                } else {
                    "✉︎ $strFreeMinReMain / $strFreeMinTotal"
                }
            }

            strFreeMinRemainCall.append(String, strFreeMinTotal) // 총제공량
            if (strSvcName.contains("음성") && strFreeMinReMain == "0") {
                // Do not include "잔여량" if strFreeMinReMain is 0
                totalUseTimeStringBuilder.append(String, strFreeMinReMain)
            } else {
                if (strFreeMinReMain != "0") {
                    totalUseTimeStringBuilder.append(String, strFreeMinReMain) // 잔여량
                }
                totalUseTimeStringBuilder.append(String, strFreeMinUse) // 사용량
            }
        }

        txtRefreshData?.text = String.format("%.2f GB", totalStrFreeMinRemain)
        Log.d("------------------MyInfoFragment  Check Data", "$totalStrFreeMinRemain")

        txtRefreshTotalData?.text = String.format("%.2f GB", totalStrFreeMinTotal)
        Log.d("------------------MyInfoFragment  Check Data", "$totalStrFreeMinTotal")

        txtRefreshCall?.text = displayCall
        Log.d("---------------------Check Call","$displayCall--------------")

        txtRefreshM?.text = displayM
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











    // LGT 당월 청구요금 API조회
    private fun LgtBillFetchData(phoneNumber: String?, custName: String?) {
        val currentDate = SimpleDateFormat("yyyyMM", Locale.getDefault()).format(Date())
        val baseUrl = "https://www.mysmartel.com/api/lguChargeDtl.php"

        val url = "$baseUrl?serviceNum=$phoneNumber&custNm=$custName&billTrgtYymm=$currentDate"
        val request = Request.Builder()
            .url(url)
            .build()

        val client = createOkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("LgtBillDetailFragment", "API Call Failed: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                Log.d("LgtBillDetailFragment", "API Response Data: $responseData")

                if (responseData.isNullOrEmpty()) {
                    Log.e("LgtBillDetailFragment", "Empty or null API response.")
                    return
                }

                try {
                    val apiResponse = Gson().fromJson(responseData, LgtBillApiResponse::class.java)
                    if (apiResponse.billInfo.isNullOrEmpty()) {
                        Log.e("LgtBillDetailFragment", "Bill Info is null or empty in API response.")
                        return
                    }

                    activity?.runOnUiThread {
                        updateUI(apiResponse)
                    }
                } catch (e: Exception) {
                    Log.e("LgtBillDetailFragment", "Failed to parse API response: ${e.message}")
                }
            }
        })
    }

    private fun handleApiCallFailure(errorMessage: String?) {
        Log.e("LgtBillDetailFragment", "API Call Failed: $errorMessage")
    }

    private fun handleApiResponse(responseData: String?) {
        if (responseData.isNullOrEmpty()) {
            Log.e("LgtBillDetailFragment", "Empty or null API response.")
            return
        }

        try {
            val apiResponse = Gson().fromJson(responseData, LgtBillApiResponse::class.java)
            if (apiResponse.billInfo.isNullOrEmpty()) {
                Log.e("LgtBillDetailFragment", "Bill Info is null or empty in API response.")
                return
            }

            activity?.runOnUiThread {
                updateUI(apiResponse)
            }
        } catch (e: Exception) {
            Log.e("LgtBillDetailFragment", "Failed to parse API response: ${e.message}")
        }
    }

    private fun createOkHttpClient(): OkHttpClient {
        // Create a trust manager that trusts all certificates
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
                // No implementation needed
            }

            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
                // No implementation needed
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }
        })

        // Create a SSL context with the trust manager
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())

        // Create a hostname verifier that bypasses all hostnames
        val hostnameVerifier = HostnameVerifier { _, _ -> true }

        // Create the OkHttpClient with the custom SSL socket factory and hostname verifier
        return OkHttpClient.Builder()
            .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier(hostnameVerifier)
            .build()
    }
    private fun updateUI(apiResponse: LgtBillApiResponse) {
        if (apiResponse.billInfo.isNullOrEmpty()) {
            Log.e("LgtBillDetailFragment", "Bill Info is null or empty in API response.")
            return
        }

        val billInfoList = apiResponse.billInfo
        var totalAmount: String? = null

        val sb = StringBuilder()
        val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault()) as DecimalFormat
        numberFormat.applyPattern("#,###")

        for (billInfo in billInfoList) {
            sb.append(String.format("\n%-60s\n\n", billInfo.blItemNm)) // Left align blItemNm column with 20 characters

            if (billInfo.blItemNm.contains("총 납부하실 금액", ignoreCase = true)) {
                val formattedAmount = numberFormat.format(billInfo.billAmt.toLong())
                totalAmount = "${formattedAmount}원"
                sb.append(String.format("%60s", totalAmount)) // Right align totalAmount value with padding
            } else {
                val formattedBillAmt = numberFormat.format(billInfo.billAmt.toLong())

                // Create a SpannableString with bold style for billAmt
                val spannableString = SpannableString("${formattedBillAmt}원")
                spannableString.setSpan(StyleSpan(Typeface.BOLD), 0, spannableString.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                sb.append(String.format("%64s", spannableString)) // Right align billAmt value with padding
            }
            sb.append("\n\n\n")
        }

        activity?.runOnUiThread {

            val formattedDate = SimpleDateFormat("yyyy년 MM월", Locale.getDefault()).format(Date())

            txtThisMonthBillDate.text = "$formattedDate"
            // Set totalAmount in txt_sumAmount
            if (totalAmount != null) {
                txtSumAmount.text = "${totalAmount}"
            } else {
                // Handle the case when "Total amount to be paid" is not present
                // You can set the txt_sumAmount to a default value or hide it if desired.
            }
        }
    }








    private fun LgtDeductFetchData() { // Lgt 잔여량 조회 API

        val baseUrl = "https://www.mysmartel.com/api/"
        val phoneNumber = viewModel.phoneNumber
        val custName = viewModel.custName
        val apiRequestUrl = "${baseUrl}lguDeductibleAmt.php?serviceNum=$phoneNumber&custNm=$custName"

        // Create a TrustManager that trusts all certificates
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            @Throws(CertificateException::class)
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
                // No-op
            }

            @Throws(CertificateException::class)
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
                // No-op
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }
        })

        // Create a HostnameVerifier that accepts all hostnames
        val hostnameVerifier = HostnameVerifier { _, _ -> true }

        // Create an SSLContext with the custom TrustManager
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, trustAllCerts, null)

        // Create an OkHttpClient that trusts all certificates and uses the custom SSLContext
        val client = OkHttpClient.Builder()
            .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier(hostnameVerifier)
            .build()

        val request = Request.Builder()
            .url(apiRequestUrl)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle error
                Log.d("LgtDeductDetailViewFragment", "API request failed: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseData = response.body?.string()
                    Log.d("LgtDeductDetailViewFragment", "API response data: $responseData")
                    val gson = Gson()
                    val apiResponse = gson.fromJson(responseData, LgtDedcutApiResponse::class.java)
                    handler.post { LgtDeductupdateUI(apiResponse) }
                } else {
                    Log.d("LgtDeductDetailViewFragment", "API request failed: ${response.code}")
                }
            }
        })
    }

    private fun LgtDeductupdateUI(apiResponse: LgtDedcutApiResponse) {
        val remainInfoList = apiResponse.remainInfo ?: emptyList()
        val resultCode = apiResponse.ResultCode

        val txtRefreshCall = view?.findViewById<TextView>(R.id.txt_refreshCall)
        val txtRefreshM = view?.findViewById<TextView>(R.id.txt_refreshM)
        val txtRefreshData = view?.findViewById<TextView>(R.id.txt_refreshData)

        // Print the ResultCode
        Log.d("LgtDeductDetailView", "ResultCode: $resultCode")

        // Create a StringBuilder to build the data string
        val dataStringBuilder = StringBuilder()

        var totalRemainData = 0.0
        var displayCall = ""
        var displayM = ""

        val remainData = StringBuilder()
        val remainCallStr = StringBuilder()

        // Iterate over the RemainInfo list and append the values to the data string
        for (remainInfo in remainInfoList) {
            val svcNm = remainInfo.svcNm
            val svcTypNm = remainInfo.svcTypNm
            val svcUnitCd = remainInfo.svcUnitCd
            val alloValue = remainInfo.alloValue
            val useValue = remainInfo.useValue
            val prodTypeCd = remainInfo.prodTypeCd

            val modifiedSvcNm = svcNm.replace("[SMT]", "")

            // Modify svcTypNm if it contains "패킷데이터" or "음성+영상"
            val modifiedSvcTypNm = when {
                svcTypNm.contains("패킷데이터") -> {
                    svcTypNm.replace("패킷데이터", "데이터")
                }
                svcTypNm.contains("음성+영상") -> {
                    svcTypNm.replace("음성+영상", "부가통화")
                }
                else -> {
                    svcTypNm
                }
            }
            // Append the values to the data string with proper formatting
            dataStringBuilder.append("$modifiedSvcNm\t")
            dataStringBuilder.append(" $modifiedSvcTypNm\n\n")
            dataStringBuilder.appendLine().appendLine()

            if (svcUnitCd.contains("초")) {
                if (alloValue.contains("Z")) {
                    remainCallStr.append("총제공량 ${"무제한".padStart(40)}\n\n")
                    val useValueInMinutes = useValue.toDouble() / 60
                    remainCallStr.append("사용량  ${useValueInMinutes.format(0).padStart(40)}분\n\n\n\n")
                    dataStringBuilder.appendLine().appendLine()

                    displayCall = "✆ ${useValueInMinutes.format(0)}분/무제한"

                } else {
                    val alloValueInMinutes = alloValue.toDouble() / 60
                    val useValueInMinutes = useValue.toDouble() / 60
                    val remainValueMin = alloValueInMinutes - useValueInMinutes
                    remainCallStr.append("총제공량 ${alloValueInMinutes.format(0).padStart(40)}분\n\n")
                    dataStringBuilder.append("사용량  ${useValueInMinutes.format(0).padStart(40)}분\n\n")
                    remainCallStr.append("잔여량    ${remainValueMin.format(0).padStart(40)}분\n\n\n\n")
                    dataStringBuilder.appendLine().appendLine()

                    displayCall = "${remainValueMin}/${alloValueInMinutes}"

                }
            } else if (svcUnitCd.contains("건")) {
                if (alloValue.contains("Z")) {
                    dataStringBuilder.append("총제공량 ${"무제한"}\n\n")
                    dataStringBuilder.append("사용량  ${useValue}건\n\n\n\n")
                    dataStringBuilder.appendLine().appendLine()

                    displayM = "✉︎ ${useValue}건/무제한"

                } else {
                    dataStringBuilder.append("총제공량 ${alloValue}건\n\n")
                    dataStringBuilder.append("사용량  ${useValue}건\n\n")
                    val remainValue = alloValue.toInt() - useValue.toInt()
                    dataStringBuilder.append("잔여량:    ${remainValue.toString()}건\n\n\n\n")
                    dataStringBuilder.appendLine().appendLine()

                    displayM = "✉︎ ${remainValue}건/${alloValue}건"
                }
            } else if (svcTypNm.contains("패킷")) {
                val alloValueInGB = alloValue.toDouble() / 1024 / 1024
                val useValueInGB = useValue.toDouble() / 1024 / 1024
                val remainValueInGB = alloValueInGB - useValueInGB

                dataStringBuilder.append("총제공량 ${alloValueInGB.format(1).padStart(40)}GB\n")
                dataStringBuilder.append("사용량  ${useValueInGB.format(1).padStart(40)}GB\n")
                dataStringBuilder.append("$remainValueInGB")

                dataStringBuilder.appendLine().appendLine()

                totalRemainData += remainValueInGB.toDouble()
            } else {
                dataStringBuilder.append("총제공량: $alloValue\n\n")
                dataStringBuilder.append("사용량:  $useValue\n\n\n\n")
                dataStringBuilder.appendLine().appendLine()
            }
        }

        remainData.append("${totalRemainData.format(1)}GB")
        txtRefreshData?.text = remainData.toString()
        Log.d("-----------------잔여량 ", "총합: $remainData -----------")

        txtRefreshCall?.text = displayCall
        Log.d("-----------------잔여량 ", "총합: $displayCall -----------")

        txtRefreshM?.text = displayM
        Log.d("-----------------잔여량 ", "총합: $displayM -----------")

    }

    // Extension function to format a Double value with the specified number of decimal places
    private fun Double.format(decimalPlaces: Int): String {
        return String.format("%.${decimalPlaces}f", this)
    }









    // SKT 부가서비스 API 조회
    private fun SktFetchAddServiceDetail() {
        // Use viewLifecycleOwner.lifecycleScope instead of GlobalScope
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val apiUrl = "https://www.mysmartel.com/api/sktGetInfo.php?svcNum=$svcNum&ifClCd=R2"
            try {
                val url = URL(apiUrl)
                val connection = url.openConnection()
                val contentType = connection.contentType
                val charset = contentType.split("charset=")
                    .lastOrNull { it.isNotBlank() }
                    ?.let { Charset.forName(it) }
                    ?: Charset.defaultCharset()

                val response = connection.getInputStream().bufferedReader(charset).use { it.readText() }

                // No need to convert the encoding, as we use detected charset from response
                val responseData = response

                // Update the UI in the main thread and call displayData function with the response data
                withContext(Dispatchers.Main) {
                    displayServiceData(responseData)
                    Log.d("SktAddServiceFragment", "$responseData")
                }
            } catch (e: Exception) {
                Log.e("SktAddServiceFragment", "Error fetching service details: ${e.message}", e)
            }
        }
    }

    private fun displayServiceData(data: String) {
        CoroutineScope(Dispatchers.Main).launch {
            // Remove the first 60 bytes from the data and create trueValue
            val trueValue = data.substring(60)

            // Helper function to consume bytes from the string
            var currentIndex = 0
            fun consumeBytes(count: Int): String {
                val subList = mutableListOf<Char>()
                var byteCounter = 0

                while (byteCounter < count && currentIndex < trueValue.length) {
                    val char = trueValue[currentIndex]
                    val byteSize = if (char.toInt() in 0xAC00..0xD7A3) 2 else 1

                    if (byteCounter + byteSize <= count) {
                        subList.add(char)
                        byteCounter += byteSize
                    } else {
                        break
                    }
                    currentIndex++
                }

                return subList.joinToString(separator = "")
            }

            // Parse the trueValue according to the format
            val opClCd = consumeBytes(1)
            val opTypCd = consumeBytes(2)
            val svcNum = consumeBytes(12)
            val svAcntNum = consumeBytes(11)
            val prodRecCnt = consumeBytes(5).trim().toInt()

            Log.d("SktAddServiceFragment", "OpClCd: $opClCd")
            Log.d("SktAddServiceFragment", "OpTypCd: $opTypCd")
            Log.d("SktAddServiceFragment", "SvcNum: $svcNum")
            Log.d("SktAddServiceFragment", "SvAcntNum: $svAcntNum")
            Log.d("SktAddServiceFragment", "ProdRecCnt: $prodRecCnt")

            var freeServiceCnt = 0
            var paidServiceCnt = 0

            // Iterate through the billing items
            val stringBuilder = StringBuilder()
            for (i in 0 until prodRecCnt) {
                val prodId = consumeBytes(10)
                val prodScrbDt = consumeBytes(10)
                val prodNm = consumeBytes(50)
                val prodFeeAmt = consumeBytes(10)

                // prodFeeAmt 값을 기준으로 무료/유료 서비스를 카운팅합니다.
                if (prodFeeAmt.trim() == "0" || prodFeeAmt.trim().isEmpty()) {
                    freeServiceCnt++
                } else {
                    paidServiceCnt++
                }

                val displayProdFee = if (prodFeeAmt.trim() == "0" || prodFeeAmt.trim().isEmpty()) "무료" else prodFeeAmt

                Log.d("SktAddServiceFragment", "Product Id: $prodId")
                Log.d("SktAddServiceFragment", "Product Subscribe Date : $prodScrbDt")
                Log.d("SktAddServiceFragment", "Product Name: $prodNm")
                Log.d("SktAddServiceFragment", "Product Fee Amount: $prodFeeAmt")

                Log.d("ProductLengths",
                    "        Product ID Length: ${prodId.length}\n" +
                            "                      Product Subscribe Date Length: ${prodScrbDt.length}\n" +
                            "                      Product Name Length: ${prodNm.length}\n" +
                            "                      Product Fee Amount Length: ${prodFeeAmt.length}\n")


                //stringBuilder.append("가입일: $prodScrbDt\n")
                stringBuilder.append("$prodNm\n\n")
                stringBuilder.append("${displayProdFee.padStart(55)}\n\n\n\n")
            }

            txtFreeService.text = "${freeServiceCnt}건"
            txtPaidService.text = "${paidServiceCnt}건"

            //textView.text = stringBuilder.toString()

            // Log to show the length of each parsed field
            Log.d("ParsedData",
                "            opClCd: ${opClCd.length}\n" +
                        "                      opTypCd: ${opTypCd.length}\n" +
                        "                      svcNum: ${svcNum.length}\n" +
                        "                      svAcntNum: ${svAcntNum.length}\n" +
                        "                      prodRecCnt: ${prodRecCnt}\n")
        }
    }








    // SKT 당월 청구요금 조회 API
    private fun SktFetchBillingDetail() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                var currentResponse = ""
                var currentMonth = Calendar.getInstance()
                currentMonth.add(Calendar.MONTH, -1)

                while (true) {
                    // Log the current year and month
                    Log.d("BillingDetail", "Current Year-Month: ${yearMonthFormat.format(currentMonth.time)}")

                    // Construct the API URL with parameters
                    val url =
                        URL("https://www.mysmartel.com/api/sktGetInfo.php?svcNum=$phoneNumber&ifClCd=R5&addInfo=${yearMonthFormat.format(currentMonth.time)}")
                    Log.d("BillingDetail", "API URL: $url")
                    val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
                    connection.setRequestProperty("charset", "euc-kr")
                    connection.requestMethod = "GET"
                    // Get the response in EUC-KR charset
                    val reader = BufferedReader(
                        InputStreamReader(
                            connection.inputStream,
                            Charset.forName("EUC-KR")
                        )
                    )
                    currentResponse = reader.readText()
                    Log.d("BillingDetail", "Response: $currentResponse")

                    // Check if the "E6" value is included in the response
                    if (!currentResponse.contains("E6")) {
                        // If "E6" is not found, break the loop and display the data
                        break
                    }

                    // Decrement the month for the next iteration
                    currentMonth.add(Calendar.MONTH, -1)
                }

                // Display the data in the textView
                displayData(currentResponse)
            } catch (e: Exception) {
                Log.e("BillingDetail", "Error fetching billing detail: ${e.message}")
            }
        }
    }
    // 문자열에 있는 이상한 문자를 제거하는 함수
    private fun removeStrangeChars(input: String): String {
        return input.replace(Regex("[^가-힣0-9\\s]+"), "")
    }

    // 조회된 데이터 처리 및 결과 출력 코드 수정
    private fun displayData(data: String) {
        GlobalScope.launch(Dispatchers.Main) {
            val encodedData = String(data.toByteArray(Charset.forName("UTF-8")), Charset.forName("UTF-8"))

            // 1. Check if the string contains Korean characters and adjust the string accordingly
            val adjustedData = encodedData.chunked(1).joinToString(separator = "") { char ->
                if (char[0].toInt() in 0xAC00..0xD7A3) char + '\u0000' else char
            }

            // 2. Remove the first 60 bytes from the data and create trueValue
            val trueValue = adjustedData.substring(60)

            // Helper function to consume bytes from the string
            var currentIndex = 0

            fun consumeBytes(count: Int): String {
                if (currentIndex >= trueValue.length) {
                    return ""
                }

                val endIndex = if (currentIndex + count > trueValue.length) trueValue.length else currentIndex + count
                val substring = trueValue.substring(currentIndex, endIndex)
                currentIndex += count
                return substring
            }

            // 3. Parse the trueValue according to the format
            val opClCd = consumeBytes(1)
            val opTypCd = consumeBytes(2)
            val svcNum = consumeBytes(12)
            val svAcntNum = consumeBytes(11)

            val INV_YM = consumeBytes(6)
            // Parse the year and month separately
            val year = INV_YM.substring(0, 4)
            val month = INV_YM.substring(4, 6)
            // Format the year and month
            val formattedDate = "${year}년 ${month}월"

            val TOT_INV_AMT = consumeBytes(22)
            val BILL_REC_CNT = consumeBytes(5).trim(' ')
            // 수정된 부분: BILL_REC_CNT가 빈 문자열인 경우에 default 값을 "0"으로 설정
            val billRecCnt = if (BILL_REC_CNT.isEmpty()) 0 else BILL_REC_CNT.toInt()

            // 4. Iterate through the billing items and display additional values
            val stringBuilderDate = StringBuilder()
            stringBuilderDate.append("$formattedDate")

            // 4. Iterate through the billing items
            val stringBuilder2 = StringBuilder()

            fun formatNumber(number: String): String {
                return try {
                    val cleanedNumber = number.trim().filter { it.isDigit() }
                    if (cleanedNumber.isNotEmpty()) {
                        NumberFormat.getInstance().format(cleanedNumber.toInt())
                    } else {
                        "0"
                    }
                } catch (e: NumberFormatException) {
                    Log.e("BillingDetail", "Error formatting number: ${e.message}")
                    "0"
                }
            }
            for (i in 0 until billRecCnt) {
                val BILL_ITM_LCL_NM = removeStrangeChars(consumeBytes(80).trim())
                val BILL_ITM_SCL_NM = removeStrangeChars(consumeBytes(80).trim())
                val BILL_ITM_NM = removeStrangeChars(consumeBytes(80).trim())
                val INV_AMT = consumeBytes(22).trimStart('0').trim()

                Log.d("BillingDetail", "대분류명: $BILL_ITM_LCL_NM")
                Log.d("BillingDetail", "소분류명: $BILL_ITM_SCL_NM")
                Log.d("BillingDetail", "항목명: $BILL_ITM_NM")
                Log.d("BillingDetail", "청구금액: $INV_AMT")

                val totalLength = 45 // 이 값을 필요한 전체 문자열 길이로 변경 가능
                val minGap = 20 // 이 값은 두 문자열 사이의 최소 여백 갯수
                val formattedLclNm = BILL_ITM_LCL_NM.padEnd(totalLength - (BILL_ITM_NM.length + INV_AMT.length), ' ')
                val formattedBillItnNM = BILL_ITM_NM.padEnd(BILL_ITM_LCL_NM.length + minGap, ' ')
                //val formattedInvAmt = INV_AMT.padStart()

                stringBuilder2.append("$formattedLclNm\n")
                // stringBuilder2.append("청구서 소분류명: $BILL_ITM_SCL_NM\n\n")
                stringBuilder2.append("$formattedBillItnNM")
                val formattedInvAmt = formatNumber(INV_AMT)
                val paddedFormattedInvAmt = formattedInvAmt.padStart(50 - formattedInvAmt.length + formattedInvAmt.length, ' ')
                stringBuilder2.append("${paddedFormattedInvAmt}원\n")
            }
            val ErrorCd = consumeBytes(2)

            val stringBuilderTotAmt = StringBuilder()
            val title = "총 납부하실 금액 "
            val value = "\n${formatNumber(TOT_INV_AMT.trimStart('0'))}원\n\n"
            val maxSpacing = 15 // Adjust this value as needed for the maximum spacing

            val formattedTitle = title.padEnd(title.length + maxSpacing, ' ')
            val formattedValue = value.padStart(value.length + maxSpacing, ' ')

            stringBuilderTotAmt.append(formattedTitle)
            stringBuilderTotAmt.append(formattedValue)

            //txtTitle.text = stringBuilder2.toString()
            //txtValue.text = stringBuilder2.append("${INV_YM}원")

            txtThisMonthBillDate.text = stringBuilderDate.toString()

            txtSumAmount.text = "${formatNumber(TOT_INV_AMT.trimStart('0'))}원"


            // Log to show the length of each parsed field
            Log.d("ParsedData", "opClCd: ${opClCd.length}\n" +
                    " opTypCd: ${opTypCd.length}\n" +
                    " svcNum: ${svcNum.length}\n" +
                    " svAcntNum: ${svAcntNum.length}\n" +
                    " INV_YM: ${INV_YM.length}\n" +
                    " TOT_INV_AMT: ${TOT_INV_AMT.length}\n" +
                    " BILL_REC_CNT: ${BILL_REC_CNT}\n" +
                    " ErrorCode: ${ErrorCd.length}")
        }
    }





   // SKT 잔여량 조회 API
    private fun SktFetchDeductData(serviceAcct: String?, telecom: String?) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val serviceAcct = viewModel.serviceAcct
                val telecom = viewModel.Telecom
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

                Log.d("MyInfoFragment. API_CALL", "URL: $url, Params: $serviceAcct, $telecom")

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
        val txtRefreshCall = view?.findViewById<TextView>(R.id.txt_refreshCall)
        val txtRefreshM = view?.findViewById<TextView>(R.id.txt_refreshM)
        val txtRefreshData = view?.findViewById<TextView>(R.id.txt_refreshData)

        if (apiResponse.remainInfo.isNotEmpty()) {
            val remainInfoStr = StringBuilder()
            var totalRemQtyData: Double = 0.0
            var displayCall = ""
            var displayM = ""

            val remainCallstr = StringBuilder()

            for (remainInfo in apiResponse.remainInfo) {
                // If freePlanName is empty, use planNm instead
                val displayName = if (remainInfo.freePlanName.isEmpty()) remainInfo.planNm else remainInfo.freePlanName

                // Default values for when the string is empty or not a valid number
                val totalQtyDefault = if (remainInfo.totalQty.isEmpty()) "0" else remainInfo.totalQty
                val useQtyDefault = if (remainInfo.useQty.isEmpty()) "0" else remainInfo.useQty
                val remQtyDefault = if (remainInfo.remQty.isEmpty()) "0" else remainInfo.remQty

                // Check if freePlanName contains "데이터" or "Data"
                if ((displayName.contains("데이터") || displayName.contains("Data")) && !displayName.contains("테더링")) {
                    // Handle the case where the values are not valid numbers (e.g., "무제한")
                    val totalQtyGB = parseValueToGB(totalQtyDefault)
                    val useQtyGB = parseValueToGB(useQtyDefault)
                    val remQtyGB = parseValueToGB(remQtyDefault)

                    remainInfoStr.append("\n\n$displayName\n\n\n")
                    remainInfoStr.append("총제공량".padEnd(1) + "%.1fGB".format(totalQtyGB) + "\n\n")
                    remainInfoStr.append("사용량".padEnd(1) + "%.1fGB".format(useQtyGB) + "\n\n")
                    remainInfoStr.append("잔여량".padEnd(1) + "%.1fGB".format(remQtyGB) + "\n\n\n\n")

                    // Add remQty to totalRemQtyData
                    totalRemQtyData += remQtyGB.toDouble()
                }
                else if (displayName.contains("음성") || displayName.contains("전화")) {
                    // Handle the case where the values are "음성" or "전화"
                    val totalQtyMin = parseValueToMinutes(remainInfo.totalQty)
                    val useQtyMin = parseValueToMinutes(remainInfo.useQty)
                    val remQtyMin = parseValueToMinutes(remainInfo.remQty)

                    remainInfoStr.append("$displayName\n\n")
                    remainCallstr.append("$totalQtyMin") // 통화 총제공량
                    remainInfoStr.append("사용량".padEnd(1) + "$useQtyMin\n\n") // Add padding between label and value
                    remainCallstr.append("$remQtyMin") // 통화 잔여량 표출

                    if (displayName.contains("부가")) {
                        displayCall = "✆ 무제한/무제한"
                    } else {
                        displayCall = "✆ $remQtyMin/$totalQtyMin"
                    }

                    Log.d("displayCall","$displayCall")

                }
                else if(displayName.contains("원")) {
                    // Default case for other freePlanName values
                    remainInfoStr.append("$displayName\n\n")
                    remainInfoStr.append("${remainInfo.totalQty}원\n\n") // 총제공량
                    remainInfoStr.append("사용량".padEnd(1) + "${remainInfo.useQty}\n\n")
                    remainInfoStr.append("${remainInfo.remQty}원\n\n\n\n") // 잔여량

                    displayM = " ✉︎ ${remainInfo.remQty}원/${remainInfo.totalQty}원"
                }
                else {
                    // Default case for other freePlanName values
                    remainInfoStr.append("$displayName\n\n")
                    remainInfoStr.append("${remainInfo.totalQty}\n\n") // 총제공량
                    remainInfoStr.append("사용량".padEnd(1) + "${remainInfo.useQty}\n\n")
                    remainInfoStr.append("${remainInfo.remQty}\n\n\n\n") // 잔여량

                    displayM = "✉︎ ${remainInfo.remQty}/${remainInfo.totalQty}"
                }
            }
            remainInfoTextView?.text = remainInfoStr.toString()
            remainInfoTextView!!.gravity = Gravity.CENTER

            // Set the value of totalRemQtyData to txtRefreshData
            txtRefreshData?.text = "%.1fGB".format(totalRemQtyData)

            txtRefreshCall?.text = displayCall
            Log.d("txt통화량", "$remainCallstr")

            txtRefreshM?.text = displayM

        } else {
            remainInfoTextView?.text = "No Remain Info found."
        }
    }

    private fun parseValueToGB(value: String): Double {   // KB를 GB로 변환하는 함수
        return try {
            if (!value.contains("무제한")) {
                val number = value.replace(",", "").toDouble() / (1024 * 1024) // 기존 코드에서 단위 변환을 유지합니다.
                number
            } else {
                0.0 // 무제한인 경우 0.0 또는 적절한 값을 설정합니다.
            }
        } catch (e: NumberFormatException) {
            // Handle non-numeric cases, e.g., "unlimited"
            0.0
        }
    }

    private fun parseValueToMinutes(value: String): String {   // 분 단위로 고쳐주는 함수
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
    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        if (savedInstanceState != null) {
            viewModel.custName = savedInstanceState.getString("custName")
            viewModel.phoneNumber = savedInstanceState.getString("phoneNumber")
            viewModel.Telecom = savedInstanceState.getString("Telecom")
            viewModel.serviceAcct = savedInstanceState.getString("serviceAcct")
        }
    }

}