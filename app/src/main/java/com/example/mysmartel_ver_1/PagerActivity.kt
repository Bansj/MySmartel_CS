package com.example.mysmartel_ver_1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mysmartel_ver_1.databinding.ActivityPagerBinding
import com.google.android.material.tabs.TabLayoutMediator


// 선불폰 충전계좌 및 선불잔액 확인하는 프래그먼트를 띄워주는 액티비트

class PagerActivity : AppCompatActivity() {
    private val binding: ActivityPagerBinding by lazy { ActivityPagerBinding.inflate(layoutInflater) }

    private val tabTextList = listOf("충전계좌", "상세내역")
    private val tabIconList = listOf(R.drawable.ic_payment_24, R.drawable.ic_subject)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.viewPager01.adapter = ViewPagerAdapter(this)

        TabLayoutMediator(binding.tabLayout01, binding.viewPager01) { tab, pos ->
            tab.text = tabTextList[pos]
            tab.setIcon(tabIconList[pos])
        }.attach()
    }
}