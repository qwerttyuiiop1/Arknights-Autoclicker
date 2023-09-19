package com.example.arknightsautoclicker.components

import android.graphics.Bitmap
import kotlin.reflect.KProperty

class RecyclableBitmap(
    width: Int = 0,
    height: Int = 0,
) {
    var width = width
        private set
    var height = height
        private set

    fun setSize(width: Int, height: Int): RecyclableBitmap {
        this.width = width
        this.height = height
        return this
    }

    var bitmap: Bitmap? = null
    fun get(): Bitmap {
        if (bitmap == null || bitmap!!.isRecycled ||
            bitmap!!.width != width || bitmap!!.height != height
        ) {
            bitmap?.recycle()
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        }
        return bitmap!!
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>) = get()
    fun close() {
        bitmap?.recycle()
        bitmap = null
    }
}