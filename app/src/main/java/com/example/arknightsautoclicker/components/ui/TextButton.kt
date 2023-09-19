package com.example.arknightsautoclicker.components.ui

import android.graphics.Rect
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.arknightsautoclicker.components.UIContext
import com.example.arknightsautoclicker.components.ui.TextArea.Companion.getDefaultScale
import com.example.arknightsautoclicker.components.impl.MTextButtonImpl
import com.example.arknightsautoclicker.components.views.TextAreaView
import com.example.arknightsautoclicker.processing.io.Clicker
import com.example.arknightsautoclicker.processing.io.TextRecognizer

interface TextButton: TextArea, UIButton {
    companion object {
        operator fun invoke(
            rect: Rect,
            recognizer: TextRecognizer,
            btn: UIButton,
            scale: Float = getDefaultScale(rect),
        ): TextButton = MTextButtonImpl(
            rect, recognizer, MUIButton(btn.clickArea, btn.clicker), scale
        )
        operator fun invoke(
            rect: Rect,
            clicker: Clicker,
            recognizer: TextRecognizer,
            clickArea: Rect = rect,
            scale: Float = getDefaultScale(rect),
        ) = invoke(rect, recognizer, UIButton(clickArea, clicker), scale)
        operator fun invoke(
            g: ViewGroup, ctx: UIContext,
        ): TextButton = MTextButton(g, ctx)
        operator fun invoke(
            v: TextView, ctx: UIContext, btn: Button? = null,
        ): TextButton = MTextButton(v, ctx, btn)
        operator fun invoke(
            v: TextAreaView, ctx: UIContext, btn: Button? = null,
        ): TextButton = MTextButton(v, ctx, btn)
    }
}
interface MTextButton: TextButton, MTextArea, MUIButton {
    companion object {
        operator fun invoke(
            rect: Rect,
            recognizer: TextRecognizer,
            btn: MUIButton,
            scale: Float = getDefaultScale(rect),
        ): MTextButton = MTextButtonImpl(rect, recognizer, btn, scale)
        operator fun invoke(
            rect: Rect,
            clicker: Clicker,
            recognizer: TextRecognizer,
            clickArea: Rect = rect,
            scale: Float = getDefaultScale(rect),
        ) = invoke(rect, recognizer, MUIButton(clickArea, clicker), scale)
        operator fun invoke(
            v: TextView, ctx: UIContext, btn: Button? = null,
        ): MTextButton = MTextButtonImpl(v, ctx, btn)
        operator fun invoke(
            v: TextAreaView, ctx: UIContext, btn: Button? = null,
        ): MTextButton = MTextButtonImpl(v, ctx, btn)
        operator fun invoke(
            g: ViewGroup, ctx: UIContext,
        ): MTextButton {
            var textArea: TextAreaView? = null
            var btn: Button? = null
            var textView: TextView? = null
            for (i in 0 until g.childCount) {
                when (val child = g.getChildAt(i)) {
                    is Button -> btn = child
                    is TextAreaView -> textArea = child
                    is TextView -> textView = child
                }
            }
            return if (textArea != null)
                invoke(textArea, ctx, btn)
            else
                invoke(textView!!, ctx, btn)
        }
    }
}