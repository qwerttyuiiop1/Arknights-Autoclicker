package com.example.arknightsautoclicker.processing.tasks.base

import android.graphics.Bitmap
import android.graphics.Rect
import com.example.arknightsautoclicker.components.ui.UIButton
import com.example.arknightsautoclicker.components.ui.TextArea
import com.example.arknightsautoclicker.components.ui.TextButton
import com.example.arknightsautoclicker.components.UIGroup
import com.example.arknightsautoclicker.components.similarTo
import com.example.arknightsautoclicker.processing.io.Clicker
import com.example.arknightsautoclicker.processing.io.TextRecognizer

//
class AssignmentUI(
    val clicker: Clicker,
    val recognizer: TextRecognizer
): UIGroup {
    inner class Slot(
        val x: Int, val y: Int,
    ): UIButton by UIButton(
        Rect(
            10, 10, 224 - 10, 422 - 10
        ).apply {
           offset(x, y)
        },
        clicker
    ) {
        val status = TextArea(
            Rect(60, 216, 175, 255).apply {
                offset(x, y)
            },
            recognizer
        )
        val morale = Rect(
            66, 357, 193, 362
        ).apply { offset(x, y) }

        private val borderPoints =
            Rect(0, 0, 224, 422).run {
                offset(x, y)
                val cx = centerX()
                val cy = centerY()
                listOf(
                    cx to top + 5, // top
                    cx to bottom - 5, // bottom
                    left + 5 to cy, // left
                    right - 5 to cy, // right
                )
            }
        fun isSelected(bit: Bitmap) = borderPoints.all {
            val selected = 0x000398dc
            val pix = bit.getPixel(it.first, it.second)
            pix.similarTo(selected, 10)
        }

        fun moraleAbove(b: Bitmap, percent: Float): Boolean {
            val color = 0x00ffffff
            val tinted = 0x00cbeaf5
            val pix = b.getPixel(
                morale.left + (morale.width() * percent).toInt(),
                morale.centerY()
            )
            return pix.similarTo(color, 10)
                    || pix.similarTo(tinted, 10)
        }
    }
    // 10 total slots
    val slots = listOf(
        Slot(609, 113),
        Slot(609, 535),
        Slot(825, 113),
        Slot(825, 535),
        Slot(1041, 113),
        Slot(1041, 535),
        Slot(1257, 113),
        Slot(1257, 535),
        Slot(1473, 113),
        Slot(1473, 535),
    )
    val confirm1 = TextButton(
        Rect(2190, 1000, 2305, 1035),
        clicker,
        recognizer,
        clickArea = Rect(2133, 980, 2366, 1053)
    ).apply { label = setOf("Confirm") }
    val confirm2 = TextButton(
        Rect(1080, 915, 1321, 950),
        clicker,
        recognizer,
        clickArea = Rect(1200, 970, 2400, 1080)
    ).apply { label = setOf("Confirm") }
}