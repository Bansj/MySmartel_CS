package com.smartel.mysmartel_ver_1

import android.content.ContentValues.TAG
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.example.mysmartel_ver_1.R

class MyInfoFragment : Fragment() {


    private lateinit var txtcustName: TextView
    private lateinit var txtPhoneNumber: TextView
    private lateinit var txtTelecom: TextView
    private lateinit var sharedPrefs: MyInfoSharedPreferences

    private var doubleBackToExitPressedOnce = false

    // Obtain an instance of the ViewModel from the shared ViewModelStoreOwner
    private val viewModel: MyInfoViewModel by viewModels({ requireActivity() })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        txtcustName = view.findViewById(R.id.txt_cust_nm)
        txtPhoneNumber = view.findViewById(R.id.txt_phoneNumber)
        txtTelecom = view.findViewById(R.id.txt_telecom)

       /* // Retrieve the data from the arguments
        val custName = arguments?.getString("custName")
        val phoneNumber = arguments?.getString("phoneNumber")
        val Telecom = arguments?.getString("Telecom")*/

        sharedPrefs = MyInfoSharedPreferences(requireContext())

        // Retrieve the data from the ViewModel or arguments
        val custName = viewModel.custName ?: arguments?.getString("custName")
        val phoneNumber = viewModel.phoneNumber ?: arguments?.getString("phoneNumber")
        val Telecom = viewModel.Telecom ?: arguments?.getString("Telecom")
        val serviceAcct = viewModel.serviceAcct?: arguments?.getString("service_acct")

       // Set the data in the views
        txtcustName.text = " ${custName}님, 안녕하세요. "
        txtPhoneNumber.text = " ✆ [$Telecom] ${phoneNumber ?: "Unknown"} "
        txtTelecom.text = Telecom

        // Set the data in the ViewModel
        viewModel.custName = custName
        viewModel.phoneNumber = phoneNumber
        viewModel.Telecom = Telecom

        // 버튼을 클릭시 아래에서 위로 올라오는 상세보기 페이지 클릭이벤트
        val btnShowFragment = view?.findViewById<Button>(R.id.btn_detailDeduct)
        btnShowFragment?.setOnClickListener {
            val telecom = viewModel.Telecom ?: arguments?.getString("Telecom")
            val fragment: Fragment? = when (telecom) {
                "SKT" -> {
                    val sktDeductDetailViewFragment = SktDeductDetailViewFragment()
                    val bundle = Bundle()
                    bundle.putString("serviceAcct", serviceAcct)
                    sktDeductDetailViewFragment.arguments = bundle

                    // Log the values for SKT
                    Log.d(TAG, "serviceAcct: $serviceAcct")

                    sktDeductDetailViewFragment
                }
                "KT" -> {
                    val ktDeductDetailViewFragment = KtDeductDetailViewFragment()
                    val bundle = Bundle()
                    bundle.putString("phoneNumber", phoneNumber)
                    ktDeductDetailViewFragment.arguments = bundle

                    // Log the values for KT
                    Log.d(TAG, "phoneNumber: $phoneNumber")

                    ktDeductDetailViewFragment
                }
                "LGT" -> {
                    val lgtDeductDetailViewFragment = LgtDeductDetailViewFragment()
                    val bundle = Bundle()
                    bundle.putString("custNm", custName)
                    bundle.putString("phoneNumber", phoneNumber)
                    lgtDeductDetailViewFragment.arguments = bundle

                    // Log the values for LGT
                    Log.d(TAG, "custName: $custName")
                    Log.d(TAG, "phoneNumber: $phoneNumber")

                    lgtDeductDetailViewFragment
                }
                else -> {
                    // Handle the case when Telecom is not SKT, KT, or LGT
                    // You can show an error message or handle it in any other way appropriate for your app
                    Log.e(TAG, "Invalid Telecom value: $telecom")
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
                    .add(android.R.id.content, it)
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
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPressedCallback)
    }
    override fun onDestroyView() {
        super.onDestroyView()

        // Save the data to SharedPreferences before the fragment view is destroyed
        sharedPrefs.custName = txtcustName.text.toString()
        sharedPrefs.phoneNumber = txtPhoneNumber.text.toString()
        sharedPrefs.telecom = txtTelecom.text.toString()
    }

    // Set click listener for the back button
    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (doubleBackToExitPressedOnce) {
                // If the back button is pressed twice, exit the app
                requireActivity().finishAffinity() // Exit the app completely
            } else {
                doubleBackToExitPressedOnce = true
                Toast.makeText(requireContext(), "Press back again to exit", Toast.LENGTH_SHORT).show()

                // Reset the flag after a short delay (2 seconds)
                Handler(Looper.getMainLooper()).postDelayed({
                    doubleBackToExitPressedOnce = false
                }, 2000)
            }
        }
    }
}



