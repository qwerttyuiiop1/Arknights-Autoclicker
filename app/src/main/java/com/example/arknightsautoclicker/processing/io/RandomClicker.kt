package com.example.arknightsautoclicker.processing.io

import android.accessibilityservice.AccessibilityService
import android.graphics.Rect
import java.util.Random
import kotlin.math.absoluteValue

class RandomClicker(
    svc: AccessibilityService
) : Clicker(svc) {
    private val random = Random(System.currentTimeMillis())
    private fun rand(): Double {
        var d: Double
        do {
            d = random.nextGaussian()
        } while (d < -1 || d > 1)
        return d
    }
    private fun rand01() = (rand() + 1) / 2
    override suspend fun click(r: Rect, duration: Long) {
        val x = r.left + (rand01() * r.width()).toInt()
        val y = r.top + (rand01() * r.height()).toInt()
        click(x, y, duration + (rand() * 49).toLong())
    }

    override suspend fun swipeV(rect: Rect, distance: Int, speed: Float, hold: Long) {
        val abs = distance.absoluteValue
        val x = (rect.width() * rand01() + rect.left).toInt()
        val offset = rect.height() - abs
        val rand = (offset * rand01()).toInt()
        val from = if (distance > 0)
            rect.top + rand
        else rect.bottom - rand
        val to = if (distance > 0)
            rect.bottom - offset + rand
        else rect.top + offset - rand
        swipe(
            x, from, x, to,
            (abs / speed).toLong(),
            if (hold > 0) hold + (rand() * 49).toLong() else 0
        )
    }

    override suspend fun swipeH(rect: Rect, distance: Int, speed: Float, hold: Long) {
        val abs = distance.absoluteValue
        val y = (rect.height() * rand01() + rect.top).toInt()
        val offset = rect.width() - abs
        val rand = (offset * rand01()).toInt()
        val from = if (distance > 0)
            rect.left + rand
        else rect.right - rand
        val to = if (distance > 0)
            rect.right - offset + rand
        else rect.left + offset - rand
        swipe(
            from, y, to, y,
            (abs / speed).toLong(),
            if (hold > 0) hold + (rand() * 49).toLong() else 0
        )
    }
}