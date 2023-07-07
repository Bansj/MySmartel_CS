package com.smartel.mysmartel_ver_1


import android.content.Context
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

        // Log the value of Telecom
        Log.d(TAG, "Telecom: $Telecom")
    }
}


