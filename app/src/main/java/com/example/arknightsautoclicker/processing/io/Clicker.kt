package com.example.arknightsautoclicker.processing.io

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.graphics.Rect
import android.util.Log
import com.example.arknightsautoclicker.processing.exe.Promise
import kotlinx.coroutines.sync.Mutex

open class Clicker(
    protected val svc: AccessibilityService
) {
    companion object {
        const val CLICK_DURATION = 50L
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
    private val clickLock = Mutex()
    open suspend fun click(r: Rect) = click(r.centerX(), r.centerY())
    suspend fun click(x: Int, y: Int) {
        clickLock.lock()
        val path = Path()
        path.moveTo(x.toFloat(), y.toFloat())
        do {
            val defer = Promise<Boolean>()
            //callback, dispatchGesture sometimes returns the wrong result
            svc.dispatchGesture(
                GestureDescription.Builder().apply {
                    addStroke(
                        GestureDescription.StrokeDescription(path, 0, CLICK_DURATION)
                    )
                }.build(),
                Callback(defer),
                null
            )
        } while (!defer.await())
        clickLock.unlock()
    }

    fun swipe(
        x1: Int, y1: Int,
        x2: Int, y2: Int
    ) {
        val path = Path()
        path.moveTo(x1.toFloat(), y1.toFloat())
        path.lineTo(x2.toFloat(), y2.toFloat())
        svc.dispatchGesture(
            GestureDescription.Builder().apply {
                addStroke(
                    GestureDescription.StrokeDescription(path, 0, CLICK_DURATION)
                )
            }.build(),
            null, null
        )
    }

}