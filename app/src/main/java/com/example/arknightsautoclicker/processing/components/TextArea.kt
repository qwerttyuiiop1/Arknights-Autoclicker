package com.example.arknightsautoclicker.processing.components

import android.graphics.Bitmap
import android.graphics.Rect
import com.example.arknightsautoclicker.processing.io.TextRecognizer
import com.example.arknightsautoclicker.processing.ext.cropped
import com.example.arknightsautoclicker.processing.ext.flatten
import com.example.arknightsautoclicker.processing.ext.scaled
import com.google.mlkit.vision.text.Text
import kotlin.math.max
import kotlin.math.min

open class TextArea(
    val rect: Rect,
    val recognizer: TextRecognizer,
    val scale: Float = getDefaultScale(rect)
): UIElement {
    companion object {
        const val DEFAULT_SCALE_FACTOR = 0.5f
        fun getDefaultScale(rect: Rect): Float {
            return max(
                DEFAULT_SCALE_FACTOR,
                36f / min(rect.width(), rect.height())
            )
        }
    }
    protected val cropped = RecyclableBitmap(rect.width(), rect.height())

    protected val scaled = RecyclableBitmap(rect.width(), rect.height())

    protected fun getCropped(bitmap: Bitmap): Bitmap {
        return bitmap.cropped(rect, cropped)
    }
    suspend fun getText(full: Bitmap): Text {
        getCropped(full).scaled(
            scale, scaled, filter = false
        )
        return recognizer.getText(scaled.get())
    }

    /**
     * the label of the button
     * the button is recognized if all of
     * the strings in this set are found in the text
     */
    var label: Set<String>? = null
        set(value) {
            if (value == null)
                throw IllegalArgumentException("Text cannot be null")
            field = value
        }
    suspend fun matchesLabel(full: Bitmap): Boolean {
        if (rect.right > full.width ||
            rect.bottom > full.height)
            return false
        return matchesLabel(getText(full))
    }
    fun matchesLabel(text: Text): Boolean {
        val list = text.flatten()
        return this.label!!.all { str ->
            list.any {
                it.text.contains(str, ignoreCase = true)
            }
        }
    }
}