package com.example.arknightsautoclicker.processing.tasks.base

import com.example.arknightsautoclicker.processing.exe.Instance
import com.example.arknightsautoclicker.processing.exe.MyResult
import com.example.arknightsautoclicker.processing.exe.TaskInstance
import com.example.arknightsautoclicker.processing.exe.multi
import com.example.arknightsautoclicker.processing.io.Clicker
import com.example.arknightsautoclicker.processing.io.TextRecognizer
import com.example.arknightsautoclicker.processing.tasks.recruitment.ClickInst
import com.example.arknightsautoclicker.processing.tasks.recruitment.TextInst
import kotlinx.coroutines.delay

class DormAssignment(
    val clicker: Clicker,
    val recognizer: TextRecognizer,
    val x: Int, val y: Int
): Instance<Unit>() {
    val summary = RoomUI(clicker, recognizer)
    val assignment = AssignmentUI(clicker, recognizer)
    class SelectInst(
        val slot: AssignmentUI.Slot,
        val target: Boolean
    ): Instance<Unit>() {
        override suspend fun run(): MyResult<Unit> {
            awaitTick()
            var i=0
            while (slot.isSelected(tick) != target) {
                if (i % 5 == 0) slot.click()
                i++
                awaitTick()
            }
            return MyResult.Success(Unit)
        }
    }
    suspend fun navigateToSelect(): Boolean {
        val y = summary.locateRoomTop(tick, y)
        summary.setPos(y)
        val statuses = join(TaskInstance.multi(
            summary.slots.map {
                TextInst(it.status)
            }
        ))
        // all resting slots are ignored
        if (statuses.all {
            val label = it.data.second
            label.text.contains("Resting")
        }) return false

        join(ClickInst(
            summary.overviewLabel, summary.slots[0]
        ))
        return true
    }
    suspend fun assignOps() {
        val deselect = ArrayList<SelectInst>()
        val selected = assignment.slots.take(5).filter {
            it.isSelected(tick)
        }
        val statuses = join(TaskInstance.multi(
            selected.map {
                TextInst(it.status)
            }
        ))
        for (i in selected.indices) {
            val status = statuses[i].data.second.text
            if (status.contains("Resting")) continue
            deselect.add(
                SelectInst(selected[i], false)
            )
        }
        join(TaskInstance.multi(deselect))
        val select = ArrayList<SelectInst>()
        for (i in selected.size until 5 + deselect.size) {
            val slot = assignment.slots[i]
            if (
                slot.moraleAbove(tick, 0.5f)
            ) break
            select.add(SelectInst(slot, true))
        }
        join(TaskInstance.multi(select))
        join(ClickInst(assignment.confirm1))
        awaitTick()
        join(ClickInst(assignment.confirm2))
        do awaitTick()
        while (!summary.overviewLabel.matchesLabel(tick))
    }
    override suspend fun run(): MyResult<Unit> {
        delay(500)
        awaitTick()
        if (navigateToSelect())
            assignOps()
        return MyResult.Success(Unit)
    }
}