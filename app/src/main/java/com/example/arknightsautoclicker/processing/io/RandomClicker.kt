package com.example.arknightsautoclicker.processing.io

import android.accessibilityservice.AccessibilityService
import android.graphics.Rect
import android.os.Handler
import java.util.Random

class RandomClicker(
    svc: AccessibilityService
) : Clicker(svc) {
    private val random = Random(System.currentTimeMillis())
    private fun rand(): Double {
        var d: Double
        do {
            d = random.nextGaussian()
        } while (d < -1 || d > 1)
        return d / 2 + 0.5
    }
    override suspend fun click(r: Rect) {
        val x = r.left + (rand() * r.width()).toInt()
        val y = r.top + (rand() * r.height()).toInt()
        click(x, y)
    }
}