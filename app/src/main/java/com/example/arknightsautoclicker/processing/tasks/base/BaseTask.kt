package com.example.arknightsautoclicker.processing.tasks.base

import com.example.arknightsautoclicker.processing.exe.Instance
import com.example.arknightsautoclicker.processing.exe.MyResult
import com.example.arknightsautoclicker.processing.exe.ResetRunner
import com.example.arknightsautoclicker.processing.ext.norm
import com.example.arknightsautoclicker.processing.io.Clicker
import com.example.arknightsautoclicker.processing.io.TextRecognizer
import com.example.arknightsautoclicker.processing.tasks.Task
import com.example.arknightsautoclicker.processing.tasks.recruitment.ClickInst
import kotlinx.coroutines.delay


class BaseInstance(
    val clicker: Clicker,
    val recognizer: TextRecognizer
): Instance<String>() {
    val ui = BaseUIBinding(clicker, recognizer)
    suspend fun assignDorm() =
        join(OverviewIterator(
            clicker, recognizer,
            each = { x, y ->
                DormAssignment(clicker, recognizer, x, y)
            },
            filter = { block ->
                block.text.contains("Dormitory")
            }
        ))
    suspend fun assignEmpty() =
        join(OverviewIterator(
            clicker, recognizer,
            each = { x, y ->
                RoomAssignment(clicker, recognizer, x, y)
            },
            filter = { block ->
                val t = block.text.norm
                t.contains("Control Center".norm) ||
                t.contains("Reception Room".norm) ||
                t.contains("Trading Post".norm) ||
                t.contains("Power Plant".norm) ||
                t.contains("Workshop".norm) ||
                t.contains("Factory".norm) ||
                t.contains("Office".norm)
            }
        ))

    suspend fun claimRewards() {
        for (btn in ui.claimMenu.claimBtns) {
            while (true) {
                awaitTick()
                val str = btn.getText(tick).text
                if (str.isBlank()) return
                if (
                    str.contains("Collectable") ||
                    str.contains("Orders") ||
                    str.contains("Trust")
                ) {
                    btn.click()
                    delay(500)
                }
                else {
                    break
                }
            }
        }
    }
    suspend fun navigateToClaimMenu() =
        ui.claimMenu.claimLabels.find {
            it.isRecognized(tick)
        }?.let { btn ->
            var i=0
            while (!btn.isSelected(tick)) {
                if (i++ % 5 == 0) btn.click()
                awaitTick()
            }
            true
        } ?: false
    suspend fun navigateToOverview(): Boolean {
        join(ClickInst(ui.overviewBtn))
        delay(500)
        awaitTick()
        return ui.overviewMenuLabel.matchesLabel(tick)
    }

    override suspend fun run(): MyResult<String> {
        awaitTick()
        if (navigateToClaimMenu()) {
            claimRewards()
            join(ClickInst(ui.claimMenuLabel)) // return to original menu
            awaitTick()
        }
        if (navigateToOverview()) {
            assignDorm()
            assignEmpty()
        }
        return MyResult.Success("Complete")
    }
}
class BaseTask(
    val clicker: Clicker,
    val recognizer: TextRecognizer
): ResetRunner() {
    override fun newInstance() = BaseInstance(clicker, recognizer)
    override val task = Task.BASE
}