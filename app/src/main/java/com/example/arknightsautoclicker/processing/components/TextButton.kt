package com.example.arknightsautoclicker.processing.components

import android.graphics.Rect
import com.example.arknightsautoclicker.processing.components.TextArea.Companion.getDefaultScale
import com.example.arknightsautoclicker.processing.io.Clicker
import com.example.arknightsautoclicker.processing.io.TextRecognizer

interface TextButton: TextArea, Button {
    companion object {
        operator fun invoke(
            rect: Rect,
            recognizer: TextRecognizer,
            btn: Button,
            scale: Float = getDefaultScale(rect),
        ): TextButton = TextButtonImpl(rect, recognizer, btn, scale)
        operator fun invoke(
            rect: Rect,
            clicker: Clicker,
            recognizer: TextRecognizer,
            clickArea: Rect = rect,
            scale: Float = getDefaultScale(rect),
        ) = invoke(rect, recognizer, Button(clickArea, clicker), scale)
    }
}
interface MTextButton: TextButton, MTextArea, MButton {
    companion object {
        operator fun invoke(
            rect: Rect,
            recognizer: TextRecognizer,
            btn: MButton,
            scale: Float = getDefaultScale(rect),
        ): MTextButton = MTextButtonImpl(rect, recognizer, btn, scale)
        operator fun invoke(
            rect: Rect,
            clicker: Clicker,
            recognizer: TextRecognizer,
            clickArea: Rect = rect,
            scale: Float = getDefaultScale(rect),
        ): MTextButton = MTextButtonImpl(rect, recognizer, MButton(clickArea, clicker), scale)
    }
}
open class TextButtonImpl(
    rect: Rect,
    recognizer: TextRecognizer,
    btn: Button,
    scale: Float,
) : MTextAreaImpl(rect, recognizer, scale),
    TextButton, Button by btn
open class MTextButtonImpl(
    rect: Rect,
    recognizer: TextRecognizer,
    protected val btn: MButton,
    scale: Float,
) : TextButtonImpl(rect, recognizer, btn, scale), MTextButton {
    override fun setPos(x: Int, y: Int) {
        super.setPos(x, y)
        btn.setPos(x, y)
    }

    override fun reset() {
        super.reset()
        btn.reset()
    }
}