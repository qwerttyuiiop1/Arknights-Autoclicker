package com.example.arknightsautoclicker.processing.components

import android.graphics.Rect
import com.example.arknightsautoclicker.processing.io.Clicker
import kotlinx.coroutines.yield

interface ButtonType: UIElement {
    val clicker: Clicker
    val clickArea: Rect
    suspend fun click()
}

open class Button(
    override val clickArea: Rect,
    override val clicker: Clicker
): ButtonType {
    override suspend fun click() {
        yield() // check for cancellation before clicking
        clicker.click(clickArea)
    }
}