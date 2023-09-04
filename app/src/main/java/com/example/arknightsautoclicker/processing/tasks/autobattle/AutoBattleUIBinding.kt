package com.example.arknightsautoclicker.processing.tasks.autobattle

import android.graphics.Rect
import com.example.arknightsautoclicker.processing.components.TextArea
import com.example.arknightsautoclicker.processing.components.TextButton
import com.example.arknightsautoclicker.processing.components.UIGroup
import com.example.arknightsautoclicker.processing.io.Clicker
import com.example.arknightsautoclicker.processing.io.TextRecognizer

class AutoBattleUIBinding(
    val clicker: Clicker,
    val recognizer: TextRecognizer,
): UIGroup {
    val startBtn = TextButton(
        Rect(
            2038, 960, 2400, 1080
        ),
        clicker, recognizer,
    ).apply { label = setOf("start") }
    val missionStartBtn = TextButton(
        Rect(
            1793, 549, 1997, 969
        ),
        clicker, recognizer,
        scale = 1/3f
    ).apply { label = setOf("mission", "start") }
    val missionResultBtn = TextButton(
        Rect(
            90, 265, 525, 430
        ),
        clicker, recognizer,
        scale = 1/3f
    ).apply { label = setOf("mission", "results") }
    val sanityLabel = TextArea(
        Rect(
            2400 - 230, 0, 2400, 100
        ),
        recognizer,
    )
}