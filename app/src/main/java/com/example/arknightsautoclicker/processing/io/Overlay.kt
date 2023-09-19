package com.example.arknightsautoclicker.processing.io

import android.graphics.Bitmap
import android.graphics.Color
import android.view.View
import com.example.arknightsautoclicker.components.Pixel
import com.example.arknightsautoclicker.components.a
import com.example.arknightsautoclicker.components.b
import com.example.arknightsautoclicker.components.clamped
import com.example.arknightsautoclicker.components.g
import com.example.arknightsautoclicker.components.r
import com.example.arknightsautoclicker.processing.ext.toBitmap

interface Preprocess {
    fun applyTo(b: Bitmap)
}

class Overlay(
    val view: View
): Preprocess {
    val buf: IntArray
    val topData: IntArray
    init {
        val bitmap = view.toBitmap()
        topData = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(topData, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        buf = IntArray(topData.size)
    }

    // https://en.wikipedia.org/wiki/Alpha_compositing#Description
    // reverse alpha blending
    private fun calc(top: Pixel, out: Pixel): Pixel {
        // out.a = top.a + bot.a * (255 - top.a) / 255
        // bot.a = (out.a - top.a) * 255 / (255 - top.a)

        // out.r * out.a = top.r * top.a + bot.r * bot.a * (255 - top.a) / 255
        // (out.r * out.a - top.r * top.a) * 255 / (255 - top.a) = bot.r * bot.a
        // out.r * out_a + top.r * top_a = bot.r * bot.a
        // bot.r = (out.r * out_a + top.r * top_a) / bot.a

        if (top.a == 255) return 0
        val out_a = out.a * 255 / (255 - top.a)
        val top_a = top.a * 255 / (255 - top.a)
        val a = (out_a - top_a).clamped
        if (a == 0) return 0

        val r = (out.r * out_a - top.r * top_a) / a
        val g = (out.g * out_a - top.g * top_a) / a
        val b = (out.b * out_a - top.b * top_a) / a

        // there is a margin of error, which sometimes
        // overflow as a 8-bit int ex: 256 -> 0, not 255
        return Color.argb(a, r.clamped, g.clamped, b.clamped)
    }

    fun removeFrom(bitmap: Bitmap) {
        if (!view.isAttachedToWindow) return

        val viewLoc = IntArray(2)
        view.getLocationOnScreen(viewLoc)
        val x = viewLoc[0]
        val y = viewLoc[1]

        val width = view.width
        val height = view.height
        bitmap.getPixels(buf,
            0, width,
            x, y, width, height
        )
        for (i in topData.indices)
            buf[i] = calc(topData[i], buf[i])

        bitmap.setPixels(buf,
            0, width,
            x, y, width, height
        )
    }

    override fun applyTo(b: Bitmap) = removeFrom(b)
}
