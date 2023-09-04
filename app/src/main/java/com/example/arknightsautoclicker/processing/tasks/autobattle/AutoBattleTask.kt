package com.example.arknightsautoclicker.processing.tasks.autobattle

import com.example.arknightsautoclicker.processing.tasks.Task
import com.example.arknightsautoclicker.processing.io.Clicker
import com.example.arknightsautoclicker.processing.exe.ResetRunner
import com.example.arknightsautoclicker.processing.exe.Instance
import com.example.arknightsautoclicker.processing.exe.MyResult
import com.example.arknightsautoclicker.processing.io.TextRecognizer
import com.example.arknightsautoclicker.processing.ext.flattenString
import kotlinx.coroutines.delay
import java.util.regex.Pattern

private class AutoBattleInstance (
    val ui: AutoBattleUIBinding
): Instance<String>() {
    suspend fun startBattle(): Boolean {
        val text = ui.startBtn.getText(tick)
        if (ui.startBtn.matchesLabel(text)) {
            val sanityCostText = text.flattenString("")
            val totalSanityText =
                ui.sanityLabel.getText(tick).flattenString("")

            val pattern = Pattern.compile("\\d+")
            val sanityMatcher = pattern.matcher(sanityCostText)
            val totalSanityMatcher = pattern.matcher(totalSanityText)

            if (!sanityMatcher.find()) return false
            if (!totalSanityMatcher.find()) return false

            val totalSanity = totalSanityMatcher.group().toInt()
            var sanityCost = sanityMatcher.group().toInt()
            if (sanityCost < 0) sanityCost *= -1

            if (totalSanity < sanityCost) {
                exit(MyResult.Success("Out of sanity"))
            } else {
                ui.startBtn.click()
                delay(3 * 60) // delay 3 seconds for animation
            }
            return true
        }
        return false
    }
    override suspend fun run(): MyResult<String> {
        while (true) {
            awaitTick()
            startBattle() && continue
            val list = listOf(
                ui.missionStartBtn,
                ui.missionResultBtn
            )
            for (btn in list) {
                if (btn.matchesLabel(tick))
                    btn.click()
            }
        }
    }
}

class AutoBattleTask(
    clicker: Clicker,
    recognizer: TextRecognizer,
): ResetRunner() {
    override val task = Task.BATTLE
    private val ui = AutoBattleUIBinding(clicker, recognizer)
    override fun newInstance() = AutoBattleInstance(ui) as Instance<String>
}