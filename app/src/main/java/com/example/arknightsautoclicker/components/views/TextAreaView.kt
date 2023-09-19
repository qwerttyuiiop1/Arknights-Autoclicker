package com.example.arknightsautoclicker.components.views

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.example.arknightsautoclicker.R

class TextAreaView(context: Context, attrs: AttributeSet?) : AppCompatTextView(context, attrs) {
    var scale: Float? = null

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TextAreaView)
        scale = typedArray.getFloat(R.styleable.TextAreaView_scale, Float.NaN)
        if (scale!!.isNaN()) scale = null
        typedArray.recycle()
    }
}
