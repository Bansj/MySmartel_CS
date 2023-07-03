package com.smartel.mysmartel_ver_1


import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.mysmartel_ver_1.R

class MainActivity : AppCompatActivity() {

    private val viewModel: MyInfoViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Retrieve the data from the intent extras
        val userName = intent.getStringExtra("userName")
        val userPhoneNumber = intent.getStringExtra("userPhoneNumber")
        val userTelecom = intent.getStringExtra("userTelecom")
        val serviceAcct = intent.getStringExtra("serviceAcct")

        // Create a Bundle to hold the data
        val bundle = Bundle()

        // Put the data into the Bundle
        bundle.putString("userName", userName)
        bundle.putString("userPhoneNumber", userPhoneNumber)
        bundle.putString("userTelecom", userTelecom)
        bundle.putString("serviceAcct", serviceAcct)

        // Create a new instance of MyInfoFragment
        val myInfoFragment = MyInfoFragment()

        // Set the arguments (data) for the fragment
        myInfoFragment.arguments = bundle

        // Replace the current fragment with MyInfoFragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.myInfoFragment, myInfoFragment)
            .commit()

        // Check the value of userTelecom and create the appropriate fragment
        val fragment = when (userTelecom) {
            "SKT" -> {
                val sktDeductDetailViewFragment = SktDeductDetailViewFragment()
                val bundle = Bundle()
                bundle.putString("userName", userName)
                bundle.putString("serviceAcct", serviceAcct)
                sktDeductDetailViewFragment.arguments = bundle
                sktDeductDetailViewFragment
            }
            "KT" -> {
                val ktDeductDetailViewFragment = KtDeductDetailViewFragment()
                val bundle = Bundle()
                bundle.putString("userName", userName)
                bundle.putString("phoneNumber", userPhoneNumber)
                ktDeductDetailViewFragment.arguments = bundle
                ktDeductDetailViewFragment
            }
            "LGT" -> {
                val lgtDeductDetailViewFragment = LgtDeductDetailViewFragment()
                val bundle = Bundle()
                bundle.putString("custNm", userName)
                bundle.putString("phoneNumber", userPhoneNumber)
                lgtDeductDetailViewFragment.arguments = bundle
                lgtDeductDetailViewFragment
            }
            else -> {
                // Handle the case when userTelecom is not SKT, KT, or LGT
                // You can show an error message or handle it in any other way appropriate for your app
                null
            }
        }
    }
}

