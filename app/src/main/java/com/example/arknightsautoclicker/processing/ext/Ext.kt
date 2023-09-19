@file:Suppress("unused")

package com.example.arknightsautoclicker.processing.ext

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PixelFormat
import android.media.Image
import android.view.View
import com.example.arknightsautoclicker.components.RecyclableBitmap
import com.google.mlkit.vision.text.Text

fun Image.rgbaToBitmap(): Bitmap {
    if (format != PixelFormat.RGBA_8888)
        throw UnsupportedOperationException("Unsupported image format: $format")
    val bitmapWidth = planes[0].rowStride / planes[0].pixelStride
    val bitmap = Bitmap.createBitmap(bitmapWidth, height, Bitmap.Config.ARGB_8888)
    bitmap.copyPixelsFromBuffer(planes[0].buffer)
    return bitmap
}
fun Image.rgbaToBitmap(bitmap: RecyclableBitmap): Bitmap {
    if (format != PixelFormat.RGBA_8888)
        throw UnsupportedOperationException("Unsupported image format: $format")
    val plane = planes[0]
    val bitmapWidth = plane.rowStride / plane.pixelStride
    bitmap.setSize(bitmapWidth, height)
    bitmap.get().copyPixelsFromBuffer(plane.buffer)
    return bitmap.get()
}

fun View.toBitmap(): Bitmap {
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    draw(canvas)
    return bitmap
}


fun Text.flatten(): List<Text.Element> {
    val list = mutableListOf<Text.Element>()
    for (block in this.textBlocks)
        for (line in block.lines)
            for (element in line.elements)
                list.add(element)
    return list
}

fun Text.flattenString(
    sep: String = ", "
): String {
    return flatten().joinToString(sep) { it.text }
}

val String.norm: String
    get() = replace(Regex("[^a-zA-Z]"), "").lowercase()