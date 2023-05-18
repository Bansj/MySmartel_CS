package com.example.mysmartel_ver_1

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter


// 선불폰 충전계좌 및 선불잔액 확인하는 프래그먼트 연결 어댑터

class ViewPagerAdapter(fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> PrepaidAccountFragment()
            else -> PrepaidDetailFragment()
        }
    }
}