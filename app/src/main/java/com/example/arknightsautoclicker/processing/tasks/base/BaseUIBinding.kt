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

class BaseUIBinding(
    val clicker: Clicker,
    val recognizer: TextRecognizer
): UIGroup {
    val overviewBtn = TextButton(
        Rect(65, 165, 178, 198),
        clicker, recognizer,
        clickArea = Rect(33, 130, 351, 233)
    ).apply { label = setOf("Overview") }
    val claimMenuLabel = TextButton(
        Rect(75, 1030, 180, 1067),
        clicker, recognizer,
        clickArea = Rect(0, 1005, 222, 1080)
    ).apply { label = setOf("Backlog") }
    val overviewMenuLabel = TextArea(
        Rect(715, 50, 855, 90), recognizer
    ).apply { label = setOf("Overview") }

    inner class ClaimLabel(y: Int): UIButton by UIButton(
        Rect(2242, y, 2400, y + 65),
        clicker,
    ) {
        fun isRecognized(b: Bitmap): Boolean {
            val r = clickArea
            val p1 = b.getPixel(r.right - 10, r.centerY())
            val p2 = b.getPixel(r.left + 40, r.centerY())
            return (
                p1.similarTo(0x002ca4d3, 16) &&
                p2.similarTo(0x00ffffff, 5)
            ) || (
                p1.similarTo(0x00f5f5f5, 5) &&
                p2.similarTo(0x000093d1, 16)
            )
        }
        fun isSelected(b: Bitmap): Boolean {
            val r = clickArea
            return b.getPixel(
                r.right - 10, r.centerY()
            ).similarTo(0x00f5f5f5, 5)
        }
    }
    val claimMenu = ClaimMenu()
    inner class ClaimMenu: UIGroup {
        val claimBtns = (0 until 10).map { i ->
            val x = 255 * i
            TextButton(
                Rect(325 + x, 1020, 483 + x, 1080),
                clicker, recognizer,
                clickArea = Rect(228 + x, 1005, 483 + x, 1080)
            )
        }
        val claimLabels = listOf(
            ClaimLabel(107),
            ClaimLabel(180)
        )
    }
}