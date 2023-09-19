package com.example.arknightsautoclicker.components.ui

import android.graphics.Rect
import android.widget.Button
import com.example.arknightsautoclicker.components.UIContext
import com.example.arknightsautoclicker.components.UIElement
import com.example.arknightsautoclicker.components.impl.MButtonImpl
import com.example.arknightsautoclicker.components.rect
import com.example.arknightsautoclicker.processing.io.Clicker

interface UIButton: UIElement {
    companion object {
        operator fun invoke(
            clickArea: Rect,
            clicker: Clicker,
        ): UIButton = MButtonImpl(clickArea, clicker)
        operator fun invoke(
            btn: Button, ctx: UIContext,
        ): UIButton = MUIButton(btn.rect, ctx.clicker)
    }
    val clicker: Clicker
    val clickArea: Rect
    suspend fun click()
}

interface MUIButton: UIButton {
    companion object {
        operator fun invoke(
            clickArea: Rect,
            clicker: Clicker,
        ): MUIButton = MButtonImpl(clickArea, clicker)
        operator fun invoke(
            btn: Button, ctx: UIContext,
        ): MUIButton = MButtonImpl(btn, ctx)
    }
    fun setPos(x: Int, y: Int)
    fun reset()
}