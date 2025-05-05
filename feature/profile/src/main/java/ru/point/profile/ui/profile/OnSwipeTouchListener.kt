package ru.point.profile.ui.profile

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs

class OnSwipeTouchListener(
    ctx: Context,
    private val onSwipe: () -> Unit
) : GestureDetector.SimpleOnGestureListener(), View.OnTouchListener {

    private val detector = GestureDetector(ctx, this)
    private val SWIPE_THRESHOLD = 100
    private val SWIPE_VELOCITY_THRESHOLD = 100

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        return detector.onTouchEvent(event)
    }

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        if (e1 == null) return false
        val diffX = e2.x - e1.x
        if (abs(diffX) > SWIPE_THRESHOLD && abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
            onSwipe()
            return true
        }
        return false
    }
}