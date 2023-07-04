package com.smartel.mysmartel_ver_1


import androidx.activity.viewModels
import android.os.Bundle
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
        val custName = intent.getStringExtra("custName")
        val phoneNumber = intent.getStringExtra("phoneNumber")
        val Telecom = intent.getStringExtra("Telecom")
        val serviceAcct = intent.getStringExtra("serviceAcct")

        // Create a Bundle to hold the data
        val bundle = Bundle()

        // Put the data into the Bundle
        bundle.putString("custName", custName)
        bundle.putString("phoneNumber", phoneNumber)
        bundle.putString("Telecom", Telecom)
        bundle.putString("serviceAcct", serviceAcct)

        // Create a new instance of MyInfoFragment
        val myInfoFragment = MyInfoFragment()

        // Set the arguments (data) for the fragment
        myInfoFragment.arguments = bundle

        // Replace the current fragment with MyInfoFragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.myInfoFragment, myInfoFragment)
            .commit()

        // Check the value of Telecom and create the appropriate fragment
        val fragment = when (Telecom) {
            "SKT" -> {
                val sktDeductDetailViewFragment = SktDeductDetailViewFragment()
                val bundle = Bundle()
                //bundle.putString("custName", custName)
                bundle.putString("serviceAcct", serviceAcct)
                sktDeductDetailViewFragment.arguments = bundle
                sktDeductDetailViewFragment
            }
            "KT" -> {
                val ktDeductDetailViewFragment = KtDeductDetailViewFragment()
                val bundle = Bundle()
                //bundle.putString("custName", custName)
                bundle.putString("phoneNumber", phoneNumber)
                ktDeductDetailViewFragment.arguments = bundle
                ktDeductDetailViewFragment
            }
            "LGT" -> {
                val lgtDeductDetailViewFragment = LgtDeductDetailViewFragment()
                val bundle = Bundle()
                bundle.putString("custNm", custName)
                bundle.putString("phoneNumber", phoneNumber)
                lgtDeductDetailViewFragment.arguments = bundle
                lgtDeductDetailViewFragment
            }
            else -> {
                // Handle the case when Telecom is not SKT, KT, or LGT
                // You can show an error message or handle it in any other way appropriate for your app
                null
            }
        }

    }
}

