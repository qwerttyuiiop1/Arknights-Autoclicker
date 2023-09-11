package com.example.arknightsautoclicker.processing.tasks.recruitment

import android.graphics.Bitmap
import com.example.arknightsautoclicker.processing.components.Button
import com.example.arknightsautoclicker.processing.components.TextArea
import com.example.arknightsautoclicker.processing.components.TextButton
import com.example.arknightsautoclicker.processing.exe.Instance
import com.example.arknightsautoclicker.processing.exe.MyResult
import com.example.arknightsautoclicker.processing.exe.SimpleInstance
import com.google.mlkit.vision.text.Text


class TextInst<T : TextArea>(
    val area: T
): SimpleInstance<Pair<T, Text>>() {
    override suspend fun run(tick: Bitmap) =
        MyResult.Success(area to area.getText(tick))
}

/**
 * repeatedly clicks a button while its label is recognized
 *
 * @return 0 if the label is not recognized
 * @return the total number of ticks if the label is recognized
 */
class ClickInst(
    val label: TextArea,
    val btn: Button,
    val every: Int = 5,
): Instance<Int>() {
    constructor(
        btn: TextButton,
        every: Int = 5,
    ): this(btn, btn, every)
    override suspend fun run(): MyResult<Int> {
        awaitTick()
        var i = 0
        while (label.matchesLabel(tick)) {
            if (i++ % every == 0)
                btn.click()
            awaitTick()
        }
        return MyResult.Success(i)
    }
}