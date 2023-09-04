package com.example.arknightsautoclicker.processing.components

import android.graphics.Rect
import com.example.arknightsautoclicker.processing.io.Clicker
import kotlinx.coroutines.yield

interface Button: UIElement {
    companion object {
        operator fun invoke(
            clickArea: Rect,
            clicker: Clicker,
        ): Button = MButtonImpl(clickArea, clicker)
    }
    val clicker: Clicker
    val clickArea: Rect
    suspend fun click()
}

interface MButton: Button {
    companion object {
        operator fun invoke(
            clickArea: Rect,
            clicker: Clicker,
        ): MButton = MButtonImpl(clickArea, clicker)
    }
    fun setPos(x: Int, y: Int)
    fun reset()
}

open class MButtonImpl(
    final override val clickArea: Rect,
    override val clicker: Clicker
): MButton {
    private val orig = Rect(clickArea)
    override suspend fun click() {
        yield() // check for cancellation before clicking
        clicker.click(clickArea)
    }
    override fun setPos(x: Int, y: Int) = clickArea.offsetTo(x, y)
    override fun reset() = clickArea.set(orig)
}