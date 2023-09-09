package com.example.arknightsautoclicker.processing.components

import android.graphics.Bitmap
import android.graphics.Rect
import com.example.arknightsautoclicker.processing.components.TextArea.Companion.getDefaultScale
import com.example.arknightsautoclicker.processing.io.TextRecognizer
import com.example.arknightsautoclicker.processing.ext.cropped
import com.example.arknightsautoclicker.processing.ext.flatten
import com.example.arknightsautoclicker.processing.ext.scaled
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
    }
    val rect: Rect
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
    }
    fun setPos(x: Int, y: Int)
    fun setRect(r: Rect)
    fun reset()
}

open class MTextAreaImpl(
    override final val rect: Rect,
    override val recognizer: TextRecognizer,
    override val scale: Float = getDefaultScale(rect)
): MTextArea, UIElement {
    private val orig = Rect(rect)
    protected val cropped = RecyclableBitmap(rect.width(), rect.height())

    protected val scaled = RecyclableBitmap(rect.width(), rect.height())

    protected fun getCropped(bitmap: Bitmap): Bitmap {
        return bitmap.cropped(rect, cropped)
    }
    override suspend fun getText(full: Bitmap): Text {
        getCropped(full).scaled(
            scale, scaled, filter = false
        )
        return recognizer.getText(scaled.get())
    }

    override var label: Set<String>? = null
        set(value) {
            if (value == null) throw IllegalArgumentException("Text cannot be null")
            if (field != null) throw IllegalStateException("Text already set")
            field = value
        }
    override suspend fun matchesLabel(full: Bitmap): Boolean {
        if (rect.right > full.width ||
            rect.bottom > full.height)
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
    override fun setPos(x: Int, y: Int) = rect.offsetTo(x, y)
    override fun setRect(r: Rect) = rect.set(r)
    override fun reset() = rect.set(orig)
}