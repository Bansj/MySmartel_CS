package com.smartel.mysmartel_ver_1

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Switch
import androidx.navigation.findNavController
import com.example.mysmartel_ver_1.R



class SettingFragment : Fragment() {

    private lateinit var switchAutoLogin: Switch
    private lateinit var sharedPrefs: SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_setting, container, false)

        sharedPrefs = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        switchAutoLogin = rootView.findViewById(R.id.switch_autoLogin)

        // Set the switch status based on the value stored in shared preferences
        switchAutoLogin.isChecked = sharedPrefs.getBoolean("autoLogin", false)

        // Set click listener for btn_menu button
        rootView.findViewById<ImageButton>(R.id.btn_menu).setOnClickListener {
            it.findNavController().navigate(R.id.action_settingFragment_to_menuFragment)
        }

        // Set click listener for btn_home button
        rootView.findViewById<ImageButton>(R.id.btn_myInfo).setOnClickListener {
            it.findNavController().navigate(R.id.action_settingFragment_to_myInfoFragment)
        }

        rootView.findViewById<ImageButton>(R.id.btn_logOut).setOnClickListener {
            // Clear the autoLogin status in shared preferences when logging out
            sharedPrefs.edit().remove("autoLogin").apply()

            val intent = Intent(requireActivity(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
        // Handle switch changes and save the status in shared preferences
        switchAutoLogin.setOnCheckedChangeListener { _, isChecked ->
            sharedPrefs.edit().putBoolean("autoLogin", isChecked).apply()
        }

        return rootView
    }
}
