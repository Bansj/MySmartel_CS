package com.smartel.mysmartel_ver_1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.example.mysmartel_ver_1.R

class MyInfoFragment : Fragment() {
    // 배너광고
/*    private lateinit var viewPager2: ViewPager2
    private lateinit var dotsIndicator: DotsIndicator
    private var currentPage = 0
    private val handler = Handler()
    private lateinit var timer: Timer*/

    private lateinit var telecomTextView: TextView
    private lateinit var custNameTextView: TextView
    private lateinit var serviceAcctTextView: TextView
    private lateinit var phoneNumber: TextView

    private lateinit var remainInfoTextView: TextView

    private lateinit var telecom: String
    private lateinit var custName: String
    private lateinit var serviceAcct: String
    private lateinit var requestQueue: RequestQueue
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            telecom = it.getString("telecom", "")
            custName = it.getString("custName", "")
            serviceAcct = it.getString("serviceAcct", "")
            //phoneNumber= it.getString("phoneNumber","")
        }
        requestQueue = Volley.newRequestQueue(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_my_info, container, false)
        telecomTextView = rootView.findViewById(R.id.txt_telecom)
        custNameTextView = rootView.findViewById(R.id.txt_cust_nm)
        serviceAcctTextView = rootView.findViewById(R.id.txt_service_acct)

//        remainInfoTextView = rootView.findViewById(R.id.remainInfoTextView)

        // 버튼을 클릭시 아래에서 위로 올라오는 상세보기 페이지 클릭이벤트
        val btnShowFragment = view?.findViewById<Button>(R.id.btn_detailDeduct)
        btnShowFragment?.setOnClickListener {
            val fragment = LgtDeductDetailViewFragment()
            requireFragmentManager().beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_up, // Animation for fragment enter
                    R.anim.slide_out_down, // Animation for fragment exit
                    R.anim.slide_in_up, // Animation for fragment pop-enter
                    R.anim.slide_out_down // Animation for fragment pop-exit
                )
                .add(android.R.id.content, fragment)
                .addToBackStack(null)
                .commit()
        }
        // Set click listener for btn_menu button 하단 메뉴이동 네비게이션바 컨트롤러
        rootView.findViewById<ImageButton>(R.id.btn_menu).setOnClickListener {
            it.findNavController().navigate(R.id.action_myInfoFragment_to_menuFragment)
        }

        // Set click listener for btn_benefit button
        rootView.findViewById<ImageButton>(R.id.btn_setting).setOnClickListener {
            it.findNavController().navigate(R.id.action_myInfoFragment_to_settingFragment)
        }
        // 배너광고
   /*     viewPager2 = rootView.findViewById(R.id.viewPager2)
        dotsIndicator = rootView.findViewById(R.id.dotsIndicator)*/


      /*  // Declare fragments
        val fragment1 = banner1Fragment()
        val fragment2 = banner2Fragment()
        val fragment3 = banner3Fragment().

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
        fragments.add(fragment6)*/

   /*     // Create a FragmentStateAdapter to bind the fragments to ViewPager2
        val adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int {
                return fragments.size
            }

            override fun createFragment(position: Int): Fragment {
                return fragments[position]
            }
        }*/

     // Apply adapter to ViewPager2
         /*  viewPager2.adapter = adapter

        // Apply dotsIndicator to ViewPager2
        dotsIndicator.setViewPager2(viewPager2)*/

        // Start auto sliding every 2 seconds
       /* timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                handler.post {
                    if (currentPage == fragments.size) {
                        currentPage = 0
                    }
                    viewPager2.setCurrentItem(currentPage++, true)
                }
            }
        }, 2000, 2000) 여기까지 배너광고 */

        return rootView
    }

    /*    override fun onDestroyView() {
        super.onDestroyView()
        // Cancel the timer when the view is destroyed to avoid memory leaks
        timer.cancel()
    }*/

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


