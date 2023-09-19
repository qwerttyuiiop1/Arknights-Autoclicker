package com.example.arknightsautoclicker.components.impl

import android.graphics.Rect
import android.widget.Button
import com.example.arknightsautoclicker.components.ui.MUIButton
import com.example.arknightsautoclicker.components.UIContext
import com.example.arknightsautoclicker.components.rect
import com.example.arknightsautoclicker.processing.io.Clicker
import kotlinx.coroutines.yield

open class MButtonImpl(
    final override val clickArea: Rect,
    override val clicker: Clicker
): MUIButton {
    constructor(btn: Button, ctx: UIContext): this(btn.rect, ctx.clicker)
    private val orig = Rect(clickArea)
    override suspend fun click() {
        yield() // check for cancellation before clicking
        clicker.click(clickArea)
    }
    override fun setPos(x: Int, y: Int) = clickArea.offsetTo(x, y)
    override fun reset() = clickArea.set(orig)
}