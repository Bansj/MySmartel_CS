package com.smartelmall.mysmartel_ver_1

import android.view.GestureDetector

abstract class OnSwipeListener : GestureDetector.SimpleOnGestureListener() {

/*    companion object {
        private const val SWIPE_THRESHOLD = 100
        private const val SWIPE_VELOCITY_THRESHOLD = 100
    }

    override fun onDown(e: MotionEvent?): Boolean {
        return true
    }

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent?,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        var result = false
        try {
            val deltaY = e2!!.y - e1!!.y
            val deltaYAbs = Math.abs(deltaY)

            if (deltaYAbs > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                if (deltaY > 0) {
                    onSwipeDown()
                } else {
                    onSwipeUp()
                }
                result = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return result
    }

    open fun onSwipeUp() {}

    open fun onSwipeDown() {}*/
}
