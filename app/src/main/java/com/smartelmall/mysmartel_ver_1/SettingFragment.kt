package com.smartelmall.mysmartel_ver_1

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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


class SettingFragment : Fragment() {

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

        view.findViewById<ImageButton>(R.id.btn_logOut).setOnClickListener {
            // Clear the autoLogin status in shared preferences when logging out
            sharedPrefs.edit().remove("autoLogin").apply()

            val intent = Intent(requireActivity(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
        // Handle switch changes and save the status in shared preferences
        switchAutoLogin.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                sharedPrefs.edit().putBoolean("autoLogin", isChecked).apply()
            }
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // MainActivity.kt로 부터 phoneNumber 받아오기
        // Retrieve the data from the ViewModel or arguments
        val custName =
            viewModel.custName ?: arguments?.getString("custName")?.also { viewModel.custName = it }
        val phoneNumber = viewModel.phoneNumber ?: arguments?.getString("phoneNumber")?.also { viewModel.phoneNumber = it }
            ?.also { viewModel.phoneNumber = it }
        val Telecom =
            viewModel.Telecom ?: arguments?.getString("Telecom")?.also { viewModel.Telecom = it }
        val serviceAcct = viewModel.serviceAcct ?: arguments?.getString("serviceAcct")
            ?.also { viewModel.serviceAcct = it }


        // Set click listener for newPW button
        view.findViewById<ImageButton>(R.id.btn_newPW).setOnClickListener {
            val intent = Intent(requireActivity(), IdentificationSelfActivity::class.java)
            intent.putExtra("phoneNumber", phoneNumber)
            Log.d("----------------SettingFragment","send phoneNumber: $phoneNumber--------------------")
            startActivity(intent)
        }
        //val phoneNumber = arguments?.getString("phoneNumber")
        Log.d("----------------SettingFragment","get phoneNumber: $phoneNumber-----------------------------")

    }
}
