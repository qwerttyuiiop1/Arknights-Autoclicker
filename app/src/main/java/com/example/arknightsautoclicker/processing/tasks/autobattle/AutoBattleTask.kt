package com.example.arknightsautoclicker.processing.tasks.autobattle

import com.example.arknightsautoclicker.components.LazySuspend
import com.example.arknightsautoclicker.components.ui.TextArea
import com.example.arknightsautoclicker.components.UIContext
import com.example.arknightsautoclicker.processing.tasks.Task
import com.example.arknightsautoclicker.processing.exe.ResetRunner
import com.example.arknightsautoclicker.processing.exe.Instance
import com.example.arknightsautoclicker.processing.exe.MyResult
import kotlinx.coroutines.delay
import java.util.regex.Pattern

private class AutoBattleInstance (
    private val _ui: suspend ()->AutoBattleUIBinding
): Instance<String>() {
    lateinit var ui: AutoBattleUIBinding
    private val pattern = Pattern.compile("\\d+")
    suspend fun getInt(
        label: TextArea
    ): Int {
        val text = label.getText(tick).text
        val matcher = pattern.matcher(text)
        if (!matcher.find()) return -1
        return matcher.group().toInt()
    }
    suspend fun startBattle(): Boolean {
        if (ui.startBtn.matchesLabel(tick)) {
            val totalSanity = getInt(ui.sanityLabel)
            val sanityCost = getInt(ui.sanityCostLabel)
            if (totalSanity == -1 || sanityCost == -1)
                return true

            if (totalSanity < sanityCost) {
                exit(MyResult.Success("Out of sanity"))
            } else {
                ui.startBtn.click()
                delay(500) // delay 0.5 seconds for animation
            }
            return true
        }
        return false
    }
    override suspend fun run(): MyResult<String> {
        ui = _ui()
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

class AutoBattleTask(val ctx: UIContext): ResetRunner() {
    override val task = Task.BATTLE
    private val ui by LazySuspend {
        AutoBattleUIBinding(ctx)
    }
    override fun newInstance() = AutoBattleInstance(ui) as Instance<String>
}