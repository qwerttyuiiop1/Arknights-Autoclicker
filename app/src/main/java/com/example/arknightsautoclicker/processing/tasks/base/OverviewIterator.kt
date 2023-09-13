package com.example.arknightsautoclicker.processing.tasks.base

import android.graphics.Rect
import com.example.arknightsautoclicker.processing.components.MTextArea
import com.example.arknightsautoclicker.processing.exe.Instance
import com.example.arknightsautoclicker.processing.exe.MyResult
import com.example.arknightsautoclicker.processing.exe.TaskInstance
import com.example.arknightsautoclicker.processing.io.Clicker
import com.example.arknightsautoclicker.processing.io.TextRecognizer
import com.google.mlkit.vision.text.Text

/**
 * Iterates over all rooms in the dorm
 * @param each: called for each room, returns whether to continue
 * @param filter: called to filter rooms
 */
class OverviewIterator(
    val clicker: Clicker,
    val recognizer: TextRecognizer,
    val each: (Int, Int) -> TaskInstance<Boolean>,
    val filter: (Text.TextBlock) -> Boolean
): Instance<Unit>() {
    val scrollBar = OverviewScrollBar(clicker)
    val roomLabels = MTextArea(
        Rect(1117, 122, 1368, 944),
        recognizer,
        scale = 0.75f
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
        var offset = 0
        while (true) {
            roomLabels.reset()
            val rect = roomLabels.area
            rect.top += offset
            val labels = roomLabels.getText(tick)
            for (block in labels.textBlocks) {
                val pos = block.boundingBox ?: continue
                if (filter(block)) {
                    update()
                    // reverse of crop -> scale -> crop
                    val rawX = pos.left / roomLabels.scale + rect.left
                    val rawY = pos.top / roomLabels.scale + rect.top
                    join(each(
                        rawX.toInt(),
                        rawY.toInt()
                    )) || return
                }
            }
            val last = labels.textBlocks.lastOrNull()?.boundingBox
                ?: break
            if (scrollBar.isAtBottom()) break
            val dist = (last.bottom / roomLabels.scale).toInt()
            offset = scrollDown(dist + offset)
        }
    }

    override suspend fun run(): MyResult<Unit> {
        awaitTick()
        join(scrollBar.setup())
        join(scrollBar.scrollToTop())
        iterateRooms()
        return MyResult.Success(Unit)
    }
}