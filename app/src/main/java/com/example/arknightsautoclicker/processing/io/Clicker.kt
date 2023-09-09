package com.example.arknightsautoclicker.processing.io

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.graphics.Rect
import com.example.arknightsautoclicker.processing.exe.Promise
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlin.math.absoluteValue

open class Clicker(
    protected val svc: AccessibilityService
) {
    companion object {
        const val CLICK_DURATION = 50L
        const val SWIPE_SPEED = 4/3f
    }
    private class Callback(
        private val defer: Promise<Boolean>
    ) : AccessibilityService.GestureResultCallback() {
        override fun onCompleted(gestureDescription: GestureDescription?) {
            super.onCompleted(gestureDescription)
            defer.complete(true)
        }
        override fun onCancelled(gestureDescription: GestureDescription?) {
            super.onCancelled(gestureDescription)
            defer.complete(false)
        }
    }
    protected suspend fun doStroke(
        stroke: GestureDescription.StrokeDescription
    ) = doStroke(GestureDescription.Builder().addStroke(stroke).build())
    protected suspend fun doStroke(
        desc: GestureDescription
    ): Boolean {
        val p = Promise<Boolean>()
        svc.dispatchGesture(desc, Callback(p),null)
        return p.await()
    }
    private val lock = Mutex()

    open suspend fun click(r: Rect, duration: Long = CLICK_DURATION) = click(r.centerX(), r.centerY(), duration)
    suspend fun click(x: Int, y: Int, duration: Long = CLICK_DURATION) = click(x.toFloat(), y.toFloat(), duration)
    suspend fun click(x: Float, y: Float, duration: Long) {
        try {
            lock.lock()
            val path = Path()
            path.moveTo(x, y)
            val stroke = GestureDescription.StrokeDescription(path, 0, duration)
            while (!doStroke(stroke)) { /* retry */ }
        } finally {
            lock.unlock()
        }
    }


    suspend fun swipe(
        x1: Int, y1: Int,
        x2: Int, y2: Int,
        duration: Long,
        hold: Long
    ) = swipe(
        x1.toFloat(), y1.toFloat(),
        x2.toFloat(), y2.toFloat(),
        duration, hold
    )
    suspend fun swipe(
        x1: Float, y1: Float,
        x2: Float, y2: Float,
        duration: Long,
        hold: Long
    ) {
        try {
            lock.lock()
            val swipePath = Path()
            swipePath.moveTo(x2, y2)
            swipePath.lineTo(x1, y1)
            val swipeStroke = GestureDescription
                .StrokeDescription(swipePath, 0, duration, hold > 0)
            doStroke(swipeStroke)

            if (hold > 0) {
                delay(hold-1)
                val holdPath = Path()
                holdPath.moveTo(x2, y2)
                val holdStroke = swipeStroke
                    .continueStroke(holdPath, 0, 1, false)
                doStroke(holdStroke) // a short click to cancel the hold (if not cancelled already)
            }
        } finally {
            lock.unlock()
        }
    }

    open suspend fun swipeV(
        rect: Rect,
        distance: Int,
        speed: Float = SWIPE_SPEED,
        hold: Long = 100
    ) {
        val x = rect.centerX()
        val offset = (rect.height() - distance) / 2
        swipe(
            x, rect.top + offset,
            x, rect.bottom - offset,
            (distance / speed).toLong().absoluteValue,
            hold
        )
    }

    open suspend fun swipeH(
        rect: Rect,
        distance: Int,
        speed: Float = SWIPE_SPEED,
        hold: Long = 100
    ) {
        val y = rect.centerY()
        val offset = (rect.width() - distance) / 2
        swipe(
            rect.left + offset, y,
            rect.right - offset, y,
            (distance / speed).toLong().absoluteValue,
            hold
        )
    }
}