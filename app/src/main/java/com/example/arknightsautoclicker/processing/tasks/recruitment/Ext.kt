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

class ClickInst(
    val area: TextArea,
    val btn: Button,
    val every: Int = 5,
): Instance<Unit>() {
    constructor(
        btn: TextButton,
        every: Int = 5,
    ): this(btn, btn, every)
    override suspend fun run(): MyResult<Unit> {
        awaitTick()
        var i=0
        while (area.matchesLabel(tick)) {
            if (i++ % every == 0)
                btn.click()
            awaitTick()
        }
        return MyResult.Success(Unit)
    }
}