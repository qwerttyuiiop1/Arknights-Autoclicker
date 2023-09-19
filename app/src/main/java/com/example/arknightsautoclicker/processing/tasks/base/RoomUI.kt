package com.example.arknightsautoclicker.processing.tasks.base

import android.graphics.Bitmap
import android.graphics.Rect
import com.example.arknightsautoclicker.components.impl.MButtonImpl
import com.example.arknightsautoclicker.components.ui.MTextArea
import com.example.arknightsautoclicker.components.ui.TextArea
import com.example.arknightsautoclicker.components.UIGroup
import com.example.arknightsautoclicker.components.similarTo
import com.example.arknightsautoclicker.processing.io.Clicker
import com.example.arknightsautoclicker.processing.io.TextRecognizer

class RoomUI(
    val clicker: Clicker,
    val recognizer: TextRecognizer
) : UIGroup {
    companion object {
        const val X_OFFSET = 1117
    }
    inner class Slot(val x: Int): MButtonImpl(
        Rect(0, 0, 149, 148).apply {
            offset(x, 23)
        }, clicker
    ) {
        val status = MTextArea(
            Rect(0, 0, 151, 35).apply {
                offset(x, 133)
            }, recognizer
        )
        fun isEmpty(bit: Bitmap): Boolean {
            val rect = clickArea
            val empty = 0x00898989
            val p1 = bit.getPixel(rect.left + 5, rect.top + 5)
            val p2 = bit.getPixel(rect.right - 5, rect.top + 5)
            return p1.similarTo(empty, 5)
                    && p2.similarTo(empty, 5)
        }
        fun setPos(y: Int) {
            setPos(x, y + 23)
            status.setPos(x, y + 133)
        }
    }

    /**
     * set the y position relative to the
     * top left corner of the room
     * @param y the y position
     */
    fun setPos(y: Int) {
        label.setPos(X_OFFSET, y + 20)
        slots.forEach { it.setPos(y) }
    }
    val label = MTextArea(
        Rect(0, 25, 215, 65).apply {
            offset(X_OFFSET, 20)
        },
        recognizer
    )
    val slots = listOf(296, 464, 632, 800, 968)
        .map { Slot(it + X_OFFSET) }
    val overviewLabel = TextArea(
        Rect(715, 50, 855, 90), recognizer
    ).apply { label = setOf("Overview") }
    fun countEmpty(b: Bitmap) = slots.count { it.isEmpty(b) }
    /**
     * locate the top of the room
     * @param b the bitmap to search
     * @param y a y position in the room
     */
    fun locateRoomTop(b: Bitmap, y: Int): Int {
        if (y < 500)
            return locateRoomBottom(b, y) - 201
        var i = y
        while (b.getPixel(2300, i)
            .similarTo(0x00313131, 5)
        ) i -= 5
        while (!b.getPixel(2300, ++i)
            .similarTo(0x00313131, 5)
        ) { /**/ }
        return i
    }
    private fun locateRoomBottom(b: Bitmap, y: Int): Int {
        var i = y
        while (b.getPixel(2300, i)
            .similarTo(0x00313131, 5)
        ) i += 10
        while (!b.getPixel(2300, --i)
            .similarTo(0x00313131, 5)
        ) { /**/ }
        return i
    }
}