package com.example.arknightsautoclicker.components.ui

import android.graphics.Bitmap
import android.graphics.Rect
import android.widget.TextView
import com.example.arknightsautoclicker.components.UIContext
import com.example.arknightsautoclicker.components.ui.TextArea.Companion.getDefaultScale
import com.example.arknightsautoclicker.components.impl.MTextAreaImpl
import com.example.arknightsautoclicker.components.views.TextAreaView
import com.example.arknightsautoclicker.processing.io.TextRecognizer
import com.google.mlkit.vision.text.Text
import kotlin.math.max
import kotlin.math.min

interface TextArea {
    companion object {
        const val DEFAULT_SCALE_FACTOR = 0.5f
        fun getDefaultScale(rect: Rect): Float {
            return max(
                DEFAULT_SCALE_FACTOR,
                36f / min(rect.width(), rect.height())
            )
        }
        operator fun invoke(
            rect: Rect,
            recognizer: TextRecognizer,
            scale: Float = getDefaultScale(rect)
        ): TextArea = MTextAreaImpl(
            rect, recognizer, scale
        )
        operator fun invoke(
            v: TextAreaView, ctx: UIContext
        ): TextArea = MTextAreaImpl(v, ctx)
        operator fun invoke(
            v: TextView, ctx: UIContext
        ): TextArea = MTextAreaImpl(v, ctx)
    }
    val area: Rect
    val recognizer: TextRecognizer
    val scale: Float
    /**
     * the label of the button
     * the button is recognized if all of
     * the strings in this set are found in the text
     */
    var label: Set<String>?
    suspend fun getText(full: Bitmap): Text
    suspend fun matchesLabel(full: Bitmap): Boolean
    fun matchesLabel(text: Text): Boolean
}
interface MTextArea: TextArea {
    companion object {
        operator fun invoke(
            rect: Rect,
            recognizer: TextRecognizer,
            scale: Float = getDefaultScale(rect)
        ): MTextArea = MTextAreaImpl(
            rect, recognizer, scale
        )
        operator fun invoke(
            v: TextAreaView, ctx: UIContext
        ): MTextArea = MTextAreaImpl(v, ctx)
        operator fun invoke(
            v: TextView, ctx: UIContext
        ): MTextArea = MTextAreaImpl(v, ctx)
    }
    fun setPos(x: Int, y: Int)
    fun setRect(r: Rect)
    fun reset()
}