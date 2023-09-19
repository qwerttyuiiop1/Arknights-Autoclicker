package com.example.arknightsautoclicker.components.ui

import android.graphics.Rect
import com.example.arknightsautoclicker.processing.io.Clicker

class SwipeArea(
    val clicker: Clicker,
    val rect: Rect,
    val speed: Float = Clicker.SWIPE_SPEED,
    val hold: Long = 1000
) {
    suspend fun swipeV(distance: Int) = clicker.swipeV(rect, distance, speed, hold)
    suspend fun swipeH(distance: Int) = clicker.swipeH(rect, distance, speed, hold)
}