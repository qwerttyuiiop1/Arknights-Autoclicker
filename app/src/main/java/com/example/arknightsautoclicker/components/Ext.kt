package com.example.arknightsautoclicker.components

import android.graphics.PixelFormat
import android.graphics.Rect
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.core.view.doOnPreDraw
import androidx.viewbinding.ViewBinding
import com.example.arknightsautoclicker.processing.exe.Promise

val View.rect: Rect
    get() {
        val loc = IntArray(2)
        getLocationOnScreen(loc)
        return Rect(loc[0], loc[1], loc[0] + width, loc[1] + height)
    }

suspend fun <T, U: ViewBinding> U.doOnMeasure(
    ctx: UIContext,
    onMeasure: (U)->T
): T {
    val res = Promise<T>()
    root.alpha = 0f
    ctx.uiHandler.post {
        // leverage android's layout file to get
        // the dimensions of the UI elements
        // this is not the fastest way but it's the
        // easiest way to do flexible UI layouts
        // and comes with the layout editor of android studio
        root.doOnPreDraw {
            res.complete(onMeasure(this))
            ctx.wmgr.removeView(root)
        }
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            ,
            PixelFormat.TRANSPARENT
        ).apply {
            gravity = Gravity.FILL
            layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        ctx.wmgr.addView(root, params)
    }
    return res.await()
}