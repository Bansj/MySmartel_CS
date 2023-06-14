package com.smartel.mysmartel_ver_1

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.mysmartel_ver_1.R
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator
import java.util.*
import kotlin.collections.ArrayList



class MyInfoFragment : Fragment() {
    private lateinit var telecomTextView: TextView
    private lateinit var custNameTextView: TextView
    private lateinit var serviceAcctTextView: TextView

    private lateinit var viewPager2: ViewPager2
    private lateinit var dotsIndicator: DotsIndicator
    private var currentPage = 0
    private val handler = Handler()
    private lateinit var timer: Timer

    // Declare UI elements
    private lateinit var productIDTextView: TextView
    private lateinit var productNameTextView: TextView
    private lateinit var deductionCodeTextView: TextView
    private lateinit var deductionNameTextView: TextView
    private lateinit var basicDeductionAmountTextView: TextView
    private lateinit var usageTextView: TextView
    private lateinit var remainingAmountTextView: TextView
    private lateinit var deductionUnitCodeTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_my_info, container, false)
        telecomTextView = rootView.findViewById(R.id.txt_telecom)
        custNameTextView = rootView.findViewById(R.id.txt_cust_nm)
        serviceAcctTextView = rootView.findViewById(R.id.txt_service_acct)

        viewPager2 = rootView.findViewById(R.id.viewPager2)
        dotsIndicator = rootView.findViewById(R.id.dotsIndicator)


        // Declare fragments
        val fragment1 = banner1Fragment()
        val fragment2 = banner2Fragment()
        val fragment3 = banner3Fragment()
        val fragment4 = banner4Fragment()
        val fragment5 = banner5Fragment()
        val fragment6 = banner6Fragment()

        // Register the fragments to the list
        val fragments = ArrayList<Fragment>()
        fragments.add(fragment1)
        fragments.add(fragment2)
        fragments.add(fragment3)
        fragments.add(fragment4)
        fragments.add(fragment5)
        fragments.add(fragment6)

        // Create a FragmentStateAdapter to bind the fragments to ViewPager2
        val adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int {
                return fragments.size
            }

            override fun createFragment(position: Int): Fragment {
                return fragments[position]
            }
        }

        // Apply adapter to ViewPager2
        viewPager2.adapter = adapter

        // Apply dotsIndicator to ViewPager2
        dotsIndicator.setViewPager2(viewPager2)

        // Set click listener for btn_menu button
        rootView.findViewById<ImageButton>(R.id.btn_menu).setOnClickListener {
            it.findNavController().navigate(R.id.action_myInfoFragment_to_menuFragment)
        }

        // Set click listener for btn_benefit button
        rootView.findViewById<ImageButton>(R.id.btn_setting).setOnClickListener {
            it.findNavController().navigate(R.id.action_myInfoFragment_to_settingFragment)
        }

        // Start auto sliding every 2 seconds
        timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                handler.post {
                    if (currentPage == fragments.size) {
                        currentPage = 0
                    }
                    viewPager2.setCurrentItem(currentPage++, true)
                }
            }
        }, 2000, 2000)


        // Initialize UI elements
        productIDTextView = requireView().findViewById(R.id.txtPlanId)
        productNameTextView = requireView().findViewById(R.id.txtPlanName)
        deductionCodeTextView = requireView().findViewById(R.id.txtSkipCode)
        deductionNameTextView = requireView().findViewById(R.id.txtFreePlanName)
        basicDeductionAmountTextView = requireView().findViewById(R.id.txtTotalQty)
        usageTextView = requireView().findViewById(R.id.txtUseQty)
        remainingAmountTextView = requireView().findViewById(R.id.txtRemQty)
        deductionUnitCodeTextView = requireView().findViewById(R.id.txtUnitCd)

        val productID = arguments?.getString("productID") ?: ""
        val productName = arguments?.getString("productName") ?: ""
        val deductionCode = arguments?.getString("deductionCode") ?: ""
        val deductionName = arguments?.getString("deductionName") ?: ""
        val basicDeductionAmount = arguments?.getString("basicDeductionAmount") ?: ""
        val usage = arguments?.getString("usage") ?: ""
        val remainingAmount = arguments?.getString("remainingAmount") ?: ""
        val deductionUnitCode = arguments?.getString("deductionUnitCode") ?: ""

        productIDTextView.text = productID
        productNameTextView.text = productName
        deductionCodeTextView.text = deductionCode
        deductionNameTextView.text = deductionName
        basicDeductionAmountTextView.text = basicDeductionAmount
        usageTextView.text = usage
        remainingAmountTextView.text = remainingAmount
        deductionUnitCodeTextView.text = deductionUnitCode

  /*      // Retrieve data from arguments bundle
        val arguments = arguments
        if (arguments != null) {
            val planId = arguments.getString("planId")
            val planName = arguments.getString("planName")
            val skipCode = arguments.getString("skipCode")
            val freePlanName = arguments.getString("freePlanName")
            val totalQty = arguments.getString("totalQty")
            val useQty = arguments.getString("useQty")
            val remQty = arguments.getString("remQty")
            val unitCode = arguments.getString("unitCode")

            // Display the SKT deduct amount information in the UI elements
            planIdTextView.text = planId
            planNameTextView.text = planName
            skipCodeTextView.text = skipCode
            freePlanNameTextView.text = freePlanName
            totalQtyTextView.text = totalQty
            useQtyTextView.text = useQty
            remQtyTextView.text = remQty
            unitCodeTextView.text = unitCode*/

        return rootView
    }

    companion object {
        fun newInstance(
            productID: String,
            productName: String,
            deductionCode: String,
            deductionName: String,
            basicDeductionAmount: String,
            usage: String,
            remainingAmount: String,
            deductionUnitCode: String
        ): MyInfoFragment {
            val fragment = MyInfoFragment()
            val args = Bundle()
            args.putString("productID", productID)
            args.putString("productName", productName)
            args.putString("deductionCode", deductionCode)
            args.putString("deductionName", deductionName)
            args.putString("basicDeductionAmount", basicDeductionAmount)
            args.putString("usage", usage)
            args.putString("remainingAmount", remainingAmount)
            args.putString("deductionUnitCode", deductionUnitCode)
            fragment.arguments = args
            return fragment
        }
    }
        override fun onDestroyView() {
        super.onDestroyView()
        // Cancel the timer when the view is destroyed to avoid memory leaks
        timer.cancel()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        updateUserInfo()
    }

    private fun updateUserInfo() {
        val telecom = activity?.intent?.getStringExtra("telecom")
        val custName = activity?.intent?.getStringExtra("custName")
        val serviceAcct = activity?.intent?.getStringExtra("serviceAcct")

        val formattedCustName = "$custName 님 안녕하세요." // "님 안녕하세요."를 추가

        telecomTextView.text = telecom
        custNameTextView.text = formattedCustName
        serviceAcctTextView.text = serviceAcct
    }
}


