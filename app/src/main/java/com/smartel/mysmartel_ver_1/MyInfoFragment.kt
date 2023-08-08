package com.smartel.mysmartel_ver_1

import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
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
import kotlinx.coroutines.launch
import okhttp3.Response

class MyInfoFragment : Fragment() {

    private lateinit var txtcustName: TextView
    private lateinit var txtPhoneNumber: TextView
    private lateinit var txtTelecom: TextView
    private lateinit var sharedPrefs: MyInfoSharedPreferences

    private var doubleBackToExitPressedOnce = false

    private lateinit var bannerImage: ImageView

    // Obtain an instance of the ViewModel from the shared ViewModelStoreOwner
    private val viewModel: MyInfoViewModel by viewModels({ requireActivity() })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_info, container, false)

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



        //UI 업데이트 내정보화면에 남은 사용량
        val updateButton = view.findViewById<ImageButton>(R.id.btn_updateLeftData)

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
        }



       // 버튼을 클릭시 아래에서 위로 올라오는 사용량 상세보기 페이지 클릭이벤트
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

            if (telecom == "SKT") {
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
            else if (telecom == "KT") {
                val ktDeductDetailViewFragment = KtDeductDetailViewFragment()
                val bundle = Bundle()
                bundle.putString("phoneNumber", phoneNumber)
                ktDeductDetailViewFragment.arguments = bundle
                // Log the values for KT
                Log.d("MyInfoFragment", "to KtDeductDetailViewFragment--------------------phoneNumber: $phoneNumber--------------------")
                fragmentTransaction.replace(android.R.id.content, ktDeductDetailViewFragment, "KtDeductDetailViewFragment")
            }
            else if (telecom == "LGT") {
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
            else {
                Log.e(TAG, "Invalid Telecom value: $telecom")
                return@setOnClickListener
            }
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

       /* //버튼 클릭시 밑에서 위로 올라오는 사용량 상세보기 페이지 클릭이벤트
        val btnDeductDetailFragment = view?.findViewById<Button>(R.id.btn_detailDeduct)
        btnDeductDetailFragment?.setOnClickListener {
            val telecom = viewModel.Telecom ?:arguments?. getString("Telecom")
            val fragment: Fragment? = when (telecom) {
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
        }*/

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

        // Set click listener for btn_benefit button
        view.findViewById<ImageButton>(R.id.btn_setting).setOnClickListener {
            it.findNavController().navigate(R.id.action_myInfoFragment_to_settingFragment)
        }

        // Set click listener for the back button
        //requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPressedCallback)
    }



    override fun onDestroyView() {
        super.onDestroyView()

        // Save the data to SharedPreferences before the fragment view is destroyed
        sharedPrefs.custName = txtcustName.text.toString()
        sharedPrefs.phoneNumber = txtPhoneNumber.text.toString()
        sharedPrefs.telecom = txtTelecom.text.toString()
    }
}