package com.smartel.mysmartel_ver_1


import android.content.Context
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.mysmartel_ver_1.R

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity" // 로그 태그

    private val viewModel: MyInfoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Retrieve the phoneNumber from SharedPreferences
        val sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val phoneNumber = sharedPrefs.getString("phoneNumber", "")

        // Retrieve the data from the intent extras
        val custName = intent.getStringExtra("custName")
        //val phoneNumber = intent.getStringExtra("phoneNumber")
        val Telecom = intent.getStringExtra("Telecom")
        val serviceAcct = intent.getStringExtra("serviceAcct")

        // Log the data before creating the fragment
        Log.d(TAG, "---------------getStringExtra -> custName: $custName, phoneNumber: $phoneNumber, Telecom: $Telecom, serviceAcct: $serviceAcct---------------")

        // Create a Bundle to hold the data
        val bundle = Bundle()

        // Put the data into the Bundle
        bundle.putString("custName", custName)
        bundle.putString("phoneNumber", phoneNumber)
        bundle.putString("Telecom", Telecom)
        bundle.putString("serviceAcct", serviceAcct)

        // Log the data before creating the fragment
        Log.d(TAG, "---------------putString -> custName: $custName, phoneNumber: $phoneNumber, Telecom: $Telecom, serviceAcct: $serviceAcct---------------")

        // Create a new instance of MyInfoFragment
        val myInfoFragment = MyInfoFragment()

        // Set the arguments (data) for the fragment
        myInfoFragment.arguments = bundle

        // Replace the current fragment with MyInfoFragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.myInfoFragment, myInfoFragment)
            .commit()

        // Log the value of bundle
        Log.d(TAG, "MyInfoFragment  --> custName: $custName, phoneNumber: $phoneNumber, Telecom: $Telecom, serviceAcct: $serviceAcct---------------")

        // Create a new instance of MenuFragment
        val menuFragment = MenuFragment()
        val menuBundle = Bundle()
        menuBundle.putString("phoneNumber", phoneNumber)
        menuBundle.putString("servicesAcct", serviceAcct)
        menuBundle.putString("custName", custName)
        menuBundle.putString("Telecom", Telecom)
        menuFragment.arguments = menuBundle

        // Log the value of phoneNumber
        Log.d(TAG, "MenuFragment    --> custName: $custName, phoneNumber: $phoneNumber, Telecom: $Telecom, serviceAcct: $serviceAcct---------------")

        // Create a new instance of SettingFragment
        val settingFragment = SettingFragment()
        val settingBundle = Bundle()
        settingBundle.putString("phoneNumber", phoneNumber)
        settingBundle.putString("servicesAcct", serviceAcct)
        settingBundle.putString("custName", custName)
        settingBundle.putString("Telecom", Telecom)
        settingFragment.arguments = settingBundle

        Log.d(TAG, "SettingFragment --> custName: $custName, phoneNumber: $phoneNumber, Telecom: $Telecom, serviceAcct: $serviceAcct---------------")
    }
}


