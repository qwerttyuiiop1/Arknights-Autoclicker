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
            2037, 960, 2347, 1054
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
            2400 - 235, 35, 2400 - 90, 90
        ),
        recognizer,
        scale = 1.5f
    )
    val sanityCostLabel = TextArea(
        Rect(
            2400 - 150, 1080 - 45, 2400 - 80, 1070
        ),
        recognizer,
        scale = 1.5f
    )
}