package com.smartelmall.mysmartel_ver_1

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Switch
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.smartelmall.mysmartel_ver_1.NewPW.IdentificationSelfActivity
import android.provider.Settings

class SettingFragment : Fragment() {

    private lateinit var autoLoginSwitch: Switch

    // Obtain an instance of the ViewModel from the shared ViewModelStoreOwner
    private val viewModel: MyInfoViewModel by viewModels({ requireActivity() })

    private lateinit var switchAutoLogin: Switch
    private lateinit var sharedPrefs: SharedPreferences

    private var phoneNumber: String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_setting, container, false)

        sharedPrefs = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        switchAutoLogin = view.findViewById(R.id.switch_autoLogin)

        // Set the switch status based on the value stored in shared preferences
        switchAutoLogin.isChecked = sharedPrefs.getBoolean("autoLogin", false)

        // Set click listener for btn_menu button
        view.findViewById<ImageButton>(R.id.btn_menu).setOnClickListener {
            it.findNavController().navigate(R.id.action_settingFragment_to_menuFragment)
        }

        // Set click listener for btn_home button
        view.findViewById<ImageButton>(R.id.btn_myInfo).setOnClickListener {
            it.findNavController().navigate(R.id.action_settingFragment_to_myInfoFragment)
        }

        // Set click listener for newPW button
        view.findViewById<ImageButton>(R.id.btn_newPW).setOnClickListener {
            val intent = Intent(requireActivity(), IdentificationSelfActivity::class.java)
            intent.putExtra("phoneNumber", phoneNumber)
            Log.d("----------------SettingFragment","send phoneNumber: $phoneNumber--------------------")
            startActivity(intent)
        }

        // 로그아웃 이미지 버튼 클릭 이벤트
        view.findViewById<ImageButton>(R.id.btn_logOut).setOnClickListener {
            val editor = sharedPrefs?.edit()

            if (editor != null) {
                editor.clear() // Clear all saved data
                editor.putBoolean("autoLogin", false)
                editor.apply()

                // Navigate to LoginActivity after logging out.
                startActivity(Intent(activity, LoginActivity::class.java))
                activity?.finish()
            }
        }
        // Handle switch changes and save the status in shared preferences
       /* switchAutoLogin.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                sharedPrefs.edit().putBoolean("autoLogin", isChecked).apply()
            }
        }*/
        autoLoginSwitch = view.findViewById(R.id.switch_autoLogin)

        //val sharedPreferences = activity?.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE) ?: return view

        val sharedPrefs = activity?.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        if (sharedPrefs != null) {
            autoLoginSwitch.isChecked = sharedPrefs.getBoolean("autoLogin", false)
        }

        autoLoginSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) {
                val editor = sharedPrefs?.edit()

                if (editor != null) {
                    editor.clear() // Clear all saved data
                    editor.apply()
                }
            }
        }

        // 알림 권한설정 클릭 이벤트
        val btnPermit: ImageButton = view.findViewById(R.id.btn_permit)
        btnPermit.setOnClickListener {
            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
            startActivity(intent)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // MainActivity.kt로 부터
        // 뷰모델에서 데이터 받아오기
        val custName =
            viewModel.custName ?: arguments?.getString("custName")?.also { viewModel.custName = it }
        val phoneNumber = viewModel.phoneNumber ?: arguments?.getString("phoneNumber")?.also { viewModel.phoneNumber = it }
            ?.also { viewModel.phoneNumber = it }
        val Telecom =
            viewModel.Telecom ?: arguments?.getString("Telecom")?.also { viewModel.Telecom = it }
        val serviceAcct = viewModel.serviceAcct ?: arguments?.getString("serviceAcct")
            ?.also { viewModel.serviceAcct = it }


        // 비밀번호 변경 버튼
        view.findViewById<ImageButton>(R.id.btn_newPW).setOnClickListener {
            val intent = Intent(requireActivity(), IdentificationSelfActivity::class.java)
            intent.putExtra("phoneNumber", phoneNumber)
            Log.d("----------------SettingFragment","send phoneNumber: $phoneNumber--------------------")
            startActivity(intent)
        }

        // 회원 탈퇴 버튼
        view.findViewById<ImageButton>(R.id.btn_out).setOnClickListener {
            val intent = Intent(requireActivity(), DeleteAccountActivity::class.java)
            intent.putExtra("phoneNumber",phoneNumber)
            intent.putExtra("custNm",custName)
            Log.d("----------------SettingFragment","send phoneNumber: $phoneNumber--------------------")
            Log.d("----------------SettingFragment","send custNm: $custName--------------------")
            startActivity(intent)
        }
        //val phoneNumber = arguments?.getString("phoneNumber")
        Log.d("----------------SettingFragment","get phoneNumber: $phoneNumber-----------------------------")

        // 권한 설정 전체 페이지로 이동하는 버튼 클릭 이벤트
     /*   val btnPermit = view.findViewById<ImageButton>(R.id.btn_permit) // 버튼 ID에 따라서 변경하세요.

        btnPermit.setOnClickListener {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", requireContext().packageName, null)
            }
            startActivity(intent)
        }*/


    }
}
