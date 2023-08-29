package com.example.arknightsautoclicker.processing.components

import android.graphics.Rect
import com.example.arknightsautoclicker.processing.io.Clicker
import com.example.arknightsautoclicker.processing.io.TextRecognizer

open class TextButton(
    rect: Rect,
    recognizer: TextRecognizer,
    btn: ButtonType,
    scale: Float,
) : TextArea(rect, recognizer, scale), ButtonType by btn {
    constructor(
        rect: Rect,
        clicker: Clicker,
        recognizer: TextRecognizer,
        clickArea: Rect = rect,
        scale: Float = getDefaultScale(rect),
    ) : this(rect, recognizer, Button(clickArea, clicker), scale)
}