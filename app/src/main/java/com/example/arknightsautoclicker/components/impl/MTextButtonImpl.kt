package com.example.arknightsautoclicker.components.impl

import android.graphics.Rect
import android.widget.Button
import android.widget.TextView
import com.example.arknightsautoclicker.components.ui.MTextArea
import com.example.arknightsautoclicker.components.ui.MTextButton
import com.example.arknightsautoclicker.components.ui.MUIButton
import com.example.arknightsautoclicker.components.ui.TextArea.Companion.getDefaultScale
import com.example.arknightsautoclicker.components.ui.UIButton
import com.example.arknightsautoclicker.components.UIContext
import com.example.arknightsautoclicker.components.rect
import com.example.arknightsautoclicker.components.views.TextAreaView
import com.example.arknightsautoclicker.processing.io.TextRecognizer

open class MTextButtonImpl private constructor(
    protected val text: MTextArea,
    protected val btn: MUIButton,
) : MTextButton, MTextArea by text, UIButton by btn {
    constructor(
        rect: Rect,
        recognizer: TextRecognizer,
        btn: MUIButton,
        scale: Float = getDefaultScale(rect),
    ) : this(
        MTextAreaImpl(rect, recognizer, scale), btn
    )
    constructor(
        v: TextView, ctx: UIContext, btn: Button? = null
    ) : this(
        MTextAreaImpl(v, ctx),
        if (btn == null) MButtonImpl(v.rect, ctx.clicker)
        else MButtonImpl(btn, ctx)
    )
    constructor(
        v: TextAreaView, ctx: UIContext, btn: Button? = null
    ) : this(
        MTextArea(v, ctx),
        if (btn == null) MButtonImpl(v.rect, ctx.clicker)
        else MButtonImpl(btn, ctx)
    )
    override fun setPos(x: Int, y: Int) {
        text.setPos(x, y)
        btn.setPos(x, y)
    }
    override fun reset() {
        text.reset()
        btn.reset()
    }
}