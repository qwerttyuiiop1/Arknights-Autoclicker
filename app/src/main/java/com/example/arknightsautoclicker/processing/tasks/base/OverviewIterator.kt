package com.example.arknightsautoclicker.processing.tasks.base

import android.graphics.Rect
import com.example.arknightsautoclicker.processing.components.MTextArea
import com.example.arknightsautoclicker.processing.exe.Instance
import com.example.arknightsautoclicker.processing.exe.MyResult
import com.example.arknightsautoclicker.processing.exe.TaskInstance
import com.example.arknightsautoclicker.processing.io.Clicker
import com.example.arknightsautoclicker.processing.io.TextRecognizer
import com.google.mlkit.vision.text.Text

class OverviewIterator(
    val clicker: Clicker,
    val recognizer: TextRecognizer,
    val each: (Int, Int, Text.TextBlock) -> TaskInstance<Unit>
): Instance<Unit>() {
    val scrollBar = OverviewScrollBar(clicker)
    val roomLabels = MTextArea(
        Rect(1117, 122, 1368, 944),
        recognizer
    )
    suspend fun update() {
        awaitTick()
        join(scrollBar.update())
    }
    suspend fun scrollDown(dist: Int): Int {
        awaitTick()
        return join(scrollBar.scrollDown(dist))
    }

    suspend fun iterateRooms() {
        do {
            roomLabels.reset()
            val rect = roomLabels.rect
            val labels = roomLabels.getText(tick)
            for (block in labels.textBlocks) {
                val pos = block.boundingBox ?: continue
                update()
                // reverse of crop -> scale -> crop
                val rawX = pos.left / roomLabels.scale + rect.left
                val rawY = pos.top / roomLabels.scale + rect.top
                join(each(
                    rawX.toInt(),
                    rawY.toInt(),
                    block
                ))
            }
            val last = labels.textBlocks.lastOrNull()?.boundingBox
                ?: break
            val dist = (last.bottom / roomLabels.scale - rect.top).toInt()
            scrollDown(dist)
        } while (!scrollBar.isAtBottom())
    }

    override suspend fun run(): MyResult<Unit> {
        awaitTick()
        scrollBar.setup(tick)
        join(scrollBar.scrollToTop())
        iterateRooms()
        return MyResult.Success(Unit)
    }
}