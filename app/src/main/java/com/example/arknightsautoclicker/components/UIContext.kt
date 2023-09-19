package com.example.arknightsautoclicker.components

import android.content.Context
import android.os.Handler
import android.view.WindowManager
import com.example.arknightsautoclicker.processing.io.Clicker
import com.example.arknightsautoclicker.processing.io.TextRecognizer

data class UIContext(
    val ctx: Context,
    val clicker: Clicker,
    val recognizer: TextRecognizer,
    val uiHandler: Handler,
    val wmgr: WindowManager = ctx.getSystemService(Context.WINDOW_SERVICE) as WindowManager,
)