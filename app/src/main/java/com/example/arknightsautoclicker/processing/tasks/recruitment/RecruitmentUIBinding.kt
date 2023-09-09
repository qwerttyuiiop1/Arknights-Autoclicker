@file:Suppress("FunctionName")
package com.example.arknightsautoclicker.processing.tasks.recruitment

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import com.example.arknightsautoclicker.processing.components.Button
import com.example.arknightsautoclicker.processing.components.TextArea
import com.example.arknightsautoclicker.processing.components.TextButton
import com.example.arknightsautoclicker.processing.components.UIGroup
import com.example.arknightsautoclicker.processing.components.b
import com.example.arknightsautoclicker.processing.components.g
import com.example.arknightsautoclicker.processing.components.r
import com.example.arknightsautoclicker.processing.components.similarTo
import com.example.arknightsautoclicker.processing.io.Clicker
import com.example.arknightsautoclicker.processing.io.TextRecognizer


// 2400 x 1080
class RecruitmentUIBinding(
    val clicker: Clicker,
    val recognizer: TextRecognizer,
): UIGroup {
    inner class TagButton(rect: Rect): TextButton by TextButton(
        rect, clicker, recognizer,
        scale = 1f
    ) {
        constructor(x: Int, y: Int): this(Rect(x, y, x + 217, y + 70))
        fun isSelected(bit: Bitmap): Boolean {
            val rect = clickArea
            val p1 = bit.getPixel(rect.left + 5, rect.top + 5)
            val p2 = bit.getPixel(rect.left + 5, rect.bottom - 5)
            val p3 = bit.getPixel(rect.right - 5, rect.top + 5)
            val p4 = bit.getPixel(rect.right - 5, rect.bottom - 5)

            val r = (p1.r + p2.r + p3.r + p4.r) / 4
            val g = (p1.g + p2.g + p3.g + p4.g) / 4
            val b = (p1.b + p2.b + p3.b + p4.b) / 4

            val notSelected = 0x00313131
            val color = Color.argb(0, r, g, b)

            return !color.similarTo(notSelected, 5)
        }
    }
    inner class TimerGroup: UIGroup {
        val timerHours = TextArea(
            Rect(880, 290, 1000, 390),
            recognizer,
            scale = 0.4f
        )
        val timerMins = TextArea(
            Rect(1111, 290, 1234, 390),
            recognizer,
            scale = 0.5f
        )
        val increaseHoursBtn = Button(
            Rect(807, 189, 1023, 261),
            clicker
        )
        val decreaseHoursBtn = Button(
            Rect(807, 409, 1023, 482),
            clicker
        )
        val increaseMinsBtn = Button(
            Rect(1053, 189, 1271, 261),
            clicker
        )
        val decreaseMinsBtn = Button(
            Rect(1053, 409, 1271, 482),
            clicker
        )
    }
    inner class RecruitGroup: UIGroup {
        val tagBtns = listOf(
            TagButton(803,540),
            TagButton(1053,540),
            TagButton(1303,540),
            TagButton(803,648),
            TagButton(1053,648),
        )

        // circle: 1645, 560, 1745, 660
        // 50 - 50 / sqrt(2) = 14.6446609407 ~ 15
        // square: 1660, 575, 1730, 645
        val refreshBtn = TextButton(
            Rect(1606, 674, 1784, 711),
            clicker, recognizer,
            clickArea = Rect(1660, 575, 1730, 645)
        ).apply { label = setOf("Tap", "To", "Refresh") }

        val confirmBtn = Button(
            Rect(1570, 834, 1847, 911),
            clicker
        )
        val cancelBtn = Button(
            Rect(1570, 932, 1847, 1009),
            clicker
        )

        val label = TextArea(
            Rect(600, 600, 720, 635),
            recognizer
        ).apply { label = setOf("Job", "Tags") }

        val timer = TimerGroup()
    }

    private fun RecruitArea(
        x: Int, y: Int
    ) = TextButton(
        Rect(x + 364, y + 215, x + 554, y + 265),
        clicker, recognizer,
        clickArea = Rect(x, y, x + 918, y + 371)
    ).apply { label = setOf("Recruit", "Now") }

    private fun CompleteBtn(
        x: Int, y: Int
    ) = TextButton(
        Rect(x + 425, y + 280, x + 500, y + 325),
        clicker, recognizer,
        clickArea = Rect(x + 18, y + 251, x + 906, y + 348)
    ).apply { label = setOf("Hire") }

    private fun ExpediteBtn(
        x: Int, y: Int
    ) = TextButton(
        Rect(x + 610, y + 280, x + 745, y + 325),
        clicker, recognizer,
        clickArea = Rect(x + 470, y + 247, x + 895, y + 345)
    ).apply { label = setOf("Expedite") }

    inner class RecruitMenuGroup: UIGroup {
        val label = TextArea(
            Rect(450, 190, 570, 230),
            recognizer,
        ).apply { label = setOf("Recruit") }

        val recruitAreas = listOf(
            RecruitArea(266, 273),
            RecruitArea(266, 690),
            RecruitArea(1214, 273),
            RecruitArea(1214, 690),
        )

        val completeBtns = listOf(
            CompleteBtn(266, 273),
            CompleteBtn(266, 690),
            CompleteBtn(1214, 273),
            CompleteBtn(1214, 690),
        )

        val expediteBtns = listOf(
            ExpediteBtn(266, 273),
            ExpediteBtn(266, 690),
            ExpediteBtn(1214, 273),
            ExpediteBtn(1214, 690),
        )
    }

    inner class OtherGroup : UIGroup {
        val skipBtn = Button(
            Rect(2244, 15, 2383, 115),
            clicker
        )

        val confirmExpediteBtn = TextButton(
            Rect(795, 610, 1610, 660),
            clicker, recognizer,
            clickArea = Rect(1200, 704, 2400, 817)
        ).apply { label = setOf("Expedited", "Plan") }

        val confirmRefreshBtn = TextButton(
            Rect(1010, 455, 1610, 510),
            clicker, recognizer,
            clickArea = Rect(1200, 683, 2400, 801)
        ).apply { label = setOf("refresh", "attempt") }
    }

    val recruit = RecruitGroup()
    val recruitMenu = RecruitMenuGroup()
    val other = OtherGroup()
}