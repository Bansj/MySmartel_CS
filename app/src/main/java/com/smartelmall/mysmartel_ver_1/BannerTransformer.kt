package com.smartelmall.mysmartel_ver_1

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import java.lang.Math.abs

// 배너 애니메이션 설정 클래스
class BannerTransformer : ViewPager2.PageTransformer {
    // 페이지 변환 함수
    override fun transformPage(page: View, position: Float) {
        // X축 이동 설정 (부드러운 스크롤)
        page.translationX = -position * page.width
        // Y축 이동 설정 (인접한 페이지를 위한 공간 조절)
        page.translationY = abs(position) * 50
    }
}