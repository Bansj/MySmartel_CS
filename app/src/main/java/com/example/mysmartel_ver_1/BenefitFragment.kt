package com.example.mysmartel_ver_1

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator
import java.util.*


class BenefitFragment : Fragment() {

    private lateinit var viewPager1: ViewPager2
    private lateinit var viewPager2: ViewPager2
    private lateinit var viewPager3: ViewPager2
    private lateinit var viewPager4: ViewPager2
    private lateinit var indicator1: DotsIndicator
    private lateinit var indicator2: DotsIndicator
    private lateinit var indicator3: DotsIndicator
    private lateinit var indicator4: DotsIndicator

    private val handler = Handler(Looper.getMainLooper())
    private val timer = Timer()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_benefit, container, false)

        // 첫 번째 뷰페이저에 연결할 Fragment 리스트 생성
        val fragmentList1 = listOf(
            banner1Fragment(),
            banner2Fragment(),
            banner3Fragment(),
            banner4Fragment(),
            banner5Fragment(),
            banner6Fragment()
        )
        // 첫 번째 뷰페이저 어댑터 생성 및 연결
        viewPager1 = view.findViewById(R.id.viewPager1)
        val pagerAdapter1 = MyPagerAdapter(fragmentList1, requireActivity())
        viewPager1.adapter = pagerAdapter1
        // 첫 번째 뷰페이저 다트 인디케이터 연결
        indicator1 = view.findViewById(R.id.indicator1)
        indicator1.setViewPager2(viewPager1)




        // 두 번째 뷰페이저에 연결할 Fragment 리스트 생성
        val fragmentList2 = listOf(
            BannerFragment12(),
            BannerFragment11(),
            BannerFragment10(),
            BannerFragment9(),
            BannerFragment8(),
            BannerFragment7()
        )
        // 두 번째 뷰페이저 어댑터 생성 및 연결
        viewPager2 = view.findViewById(R.id.viewPager2)
        val pagerAdapter2 = MyPagerAdapter(fragmentList2, requireActivity())
        viewPager2.adapter = pagerAdapter2
        // 두 번째 뷰페이저 다트 인디케이터 연결
        indicator2 = view.findViewById(R.id.indicator2)
        indicator2.setViewPager2(viewPager2)




        // 세 번째 뷰페이저에 연결할 Fragment 리스트 생성
        val fragmentList3 = listOf(
            BannerFragment12(),
            BannerFragment11(),
            BannerFragment10(),
            BannerFragment9(),
            BannerFragment8(),
            BannerFragment7()
        )
        // 세 번째 뷰페이저 어댑터 생성 및 연결
        viewPager3 = view.findViewById(R.id.viewPager3)
        val pagerAdapter3 = MyPagerAdapter(fragmentList2, requireActivity())
        viewPager3.adapter = pagerAdapter3
        // 두 번째 뷰페이저 다트 인디케이터 연결
        indicator3 = view.findViewById(R.id.indicator3)
        indicator3.setViewPager2(viewPager3)





        // 네 번째 뷰페이저에 연결할 Fragment 리스트 생성
        val fragmentList4 = listOf(
            banner4Fragment(),
            banner5Fragment(),
            banner6Fragment(),
            banner1Fragment(),
            banner2Fragment(),
            banner3Fragment()
        )
        // 네 번째 뷰페이저 어댑터 생성 및 연결
        viewPager4 = view.findViewById(R.id.viewPager4)
        val pagerAdapter4 = MyPagerAdapter(fragmentList4, requireActivity())
        viewPager4.adapter = pagerAdapter4
        // 네 번째 뷰페이저 다트 인디케이터 연결
        indicator4 = view.findViewById(R.id.indicator4)
        indicator4.setViewPager2(viewPager4)




        // 뷰페이저1을 2초마다 자동으로 넘기는 타이머 설정
        timer.schedule(object : TimerTask() {
            override fun run() {
                handler.post {
                    val currentItem = viewPager1.currentItem
                    viewPager1.setCurrentItem(if (currentItem < pagerAdapter1.itemCount - 1) currentItem + 1 else 0, true)
                }
            }
        }, 2000, 2000)

        // 뷰페이저2를 1초마다 자동으로 넘기는 타이머 설정
        timer.schedule(object : TimerTask() {
            override fun run() {
                handler.post {
                    val currentItem = viewPager2.currentItem
                    viewPager2.setCurrentItem(if (currentItem < pagerAdapter2.itemCount - 1) currentItem + 1 else 0, true)
                }
            }
        }, 1500, 1500)

        // 뷰페이저3를 1초마다 자동으로 넘기는 타이머 설정
        timer.schedule(object : TimerTask() {
            override fun run() {
                handler.post {
                    val currentItem = viewPager3.currentItem
                    viewPager3.setCurrentItem(if (currentItem < pagerAdapter3.itemCount - 1) currentItem + 1 else 0, true)
                }
            }
        }, 1500, 1500)

        // 뷰페이저4를 2초마다 자동으로 넘기는 타이머 설정
        timer.schedule(object : TimerTask() {
            override fun run() {
                handler.post {
                    val currentItem = viewPager4.currentItem
                    viewPager4.setCurrentItem(if (currentItem < pagerAdapter4.itemCount - 1) currentItem + 1 else 0, true)
                }
            }
        }, 2000, 2000)


        // Set click listener for btn_menu button
        view.findViewById<ImageButton>(R.id.btn_menu).setOnClickListener {
            it.findNavController().navigate(R.id.action_benefitFragment_to_menuFragment)
        }

        // Set click listener for btn_benefit button
        view.findViewById<ImageButton>(R.id.btn_myInfo).setOnClickListener {
            it.findNavController().navigate(R.id.action_benefitFragment_to_myInfoFragment)
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // 타이머 종료
        timer.cancel()
    }

    private inner class MyPagerAdapter(private val fragmentList: List<Fragment>, fragmentActivity: FragmentActivity) :
        FragmentStateAdapter(fragmentActivity) {

        override fun getItemCount(): Int = fragmentList.size

        override fun createFragment(position: Int): Fragment = fragmentList[position]
    }
}



