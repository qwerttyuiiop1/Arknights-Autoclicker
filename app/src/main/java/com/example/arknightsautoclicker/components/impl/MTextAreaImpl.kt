package com.example.arknightsautoclicker.components.impl

import android.graphics.Bitmap
import android.graphics.Rect
import android.widget.TextView
import com.example.arknightsautoclicker.components.ui.MTextArea
import com.example.arknightsautoclicker.components.RecyclableBitmap
import com.example.arknightsautoclicker.components.ui.TextArea.Companion.getDefaultScale
import com.example.arknightsautoclicker.components.UIContext
import com.example.arknightsautoclicker.components.UIElement
import com.example.arknightsautoclicker.components.rect
import com.example.arknightsautoclicker.components.views.TextAreaView
import com.example.arknightsautoclicker.processing.ext.cropped
import com.example.arknightsautoclicker.processing.ext.flatten
import com.example.arknightsautoclicker.processing.ext.scaled
import com.example.arknightsautoclicker.processing.io.TextRecognizer
import com.google.mlkit.vision.text.Text

open class MTextAreaImpl(
    final override val area: Rect,
    override val recognizer: TextRecognizer,
    override val scale: Float = getDefaultScale(area),
    label: Set<String>? = null
): MTextArea, UIElement {
    constructor(
        v: TextView, ctx: UIContext, scale: Float? = null
    ): this(
        v.rect,
        ctx.recognizer,
        scale ?: getDefaultScale(v.rect),
        v.text.let {
            if (it == null || it == "") null
            else it.split(",").toSet()
        }
    )
    constructor(
        v: TextAreaView, ctx: UIContext
    ): this(v, ctx, v.scale)

    private val orig = Rect(area)
    protected val cropped = RecyclableBitmap(area.width(), area.height())
    protected val scaled = RecyclableBitmap(area.width(), area.height())

    protected fun getCropped(bitmap: Bitmap): Bitmap {
        return bitmap.cropped(area, cropped)
    }
    override suspend fun getText(full: Bitmap): Text {
        getCropped(full).scaled(
            scale, scaled, filter = false
        )
        return recognizer.getText(scaled.get())
    }

    override var label: Set<String>? = label
        set(value) {
            if (value == null) throw IllegalArgumentException("Text cannot be null")
            if (field != null) throw IllegalStateException("Text already set")
            field = value
        }
    override suspend fun matchesLabel(full: Bitmap): Boolean {
        if (area.right > full.width ||
            area.bottom > full.height)
            return false
        return matchesLabel(getText(full))
    }
    override fun matchesLabel(text: Text): Boolean {
        val list = text.flatten()
        return this.label?.all { str ->
            list.any {
                it.text.contains(str, ignoreCase = true)
            }
        } ?: throw IllegalStateException("Label not set")
    }
    override fun setPos(x: Int, y: Int) = area.offsetTo(x, y)
    override fun setRect(r: Rect) = area.set(r)
    override fun reset() = area.set(orig)
}