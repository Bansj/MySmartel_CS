package com.smartelmall.mysmartel_ver_1.Banner

import android.util.DisplayMetrics
import android.view.View
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

class ZoomOutPageTransformer : ViewPager2.PageTransformer {
    private val MIN_SCALE = 0.85f
    private val MIN_ALPHA = 0.5f

    override fun transformPage(view: View, position: Float) {
        view.apply {
            when {
                position < -1 -> { // [-Infinity,-1)
                    alpha = 0f // Page is off-screen to the left.
                }
                position <= 1 -> { // [-1,1]
                    val scaleFactor = MIN_SCALE.coerceAtLeast(1 - kotlin.math.abs(position))
                    val verticalMargin = height * (1 - scaleFactor) / 2
                    val horizontalMargin = width * (1 - scaleFactor) / 2

                    translationX = if (position < 0) {
                        horizontalMargin - verticalMargin / 2
                    } else {
                        horizontalMargin + verticalMargin / 2
                    }

                    scaleX = scaleFactor
                    scaleY = scaleFactor

                    alpha =
                        (MIN_ALPHA +
                                (((scaleFactor - MIN_SCALE) / (1 - MIN_SCALE)) * (1 - MIN_ALPHA)))
                }
                else -> { // [1,+Infinity]
                    alpha = 0f // Page is off-screen to the right.
                }
            }
        }
    }
}

fun ViewPager2.getRecyclerView(): RecyclerView? {
    try {
        val field = ViewPager2::class.java.getDeclaredField("mRecyclerView")
        field.isAccessible = true
        return field.get(this) as? RecyclerView
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return null
}

fun ViewPager2.setPageChangeDuration(duration: Int) { // 넘어가는 속도 조절
    this.getRecyclerView()?.let { recyclerView ->
        try{
            val layoutManagerField=recyclerView::class.java.getDeclaredField("mLayout")
            layoutManagerField.isAccessible=true

            val layoutManager=layoutManagerField.get(recyclerView)

            val scrollerField=
                layoutManager::class.java.superclass?.getDeclaredField("mSmoothScroller")
            scrollerField?.isAccessible=true

            scrollerField?.set(layoutManager,object : LinearSmoothScroller(recyclerView.context){
                override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float{
                    return duration/displayMetrics.densityDpi.toFloat()
                }

                override fun calculateTimeForDeceleration(dx:Int):Int{
                    return duration;
                }

                override fun calculateTimeForScrolling(dx:Int):Int{
                    return duration;
                }

            })
        }catch(e:Exception){
            e.printStackTrace()
        }
    }
}

