@file:Suppress("unused")

package com.example.arknightsautoclicker.processing.ext

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import com.example.arknightsautoclicker.components.RecyclableBitmap

fun Bitmap.cropped(rect: Rect, recycle: Boolean = false): Bitmap {
    return Bitmap.createBitmap(
        this,
        rect.left, rect.top,
        rect.width(), rect.height()
    ).also {
        if (recycle) recycle()
    }
}
fun Bitmap.cropped(rect: Rect, dst: RecyclableBitmap): Bitmap {
    dst.setSize(rect.width(), rect.height())
    val canvas = Canvas(dst.get())
    canvas.drawBitmap(
        this, rect,
        Rect(0, 0, rect.width(), rect.height()), null
    )
    return dst.get()
}

fun Bitmap.scaled(
    scale: Float,
    recycle: Boolean = false,
    filter: Boolean = false
): Bitmap {
    return Bitmap.createScaledBitmap(
        this,
        (width * scale).toInt(),
        (height * scale).toInt(),
        filter
    ).also {
        if (recycle) recycle()
    }
}
fun Bitmap.scaled(scale: Float, dst: RecyclableBitmap, filter: Boolean = false): Bitmap {
    val w = (width * scale).toInt()
    val h = (height * scale).toInt()
    dst.setSize(w, h)
    val canvas = Canvas(dst.get())
    val paint = Paint().apply {
        isFilterBitmap = filter
    }
    canvas.drawBitmap(this, null,
        Rect(0, 0, w, h), paint)
    return dst.get()
}