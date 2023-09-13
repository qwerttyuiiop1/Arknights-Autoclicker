package com.example.arknightsautoclicker.processing.tasks.base

import com.example.arknightsautoclicker.processing.exe.Instance
import com.example.arknightsautoclicker.processing.exe.MyResult
import com.example.arknightsautoclicker.processing.exe.TaskInstance
import com.example.arknightsautoclicker.processing.exe.multi
import com.example.arknightsautoclicker.processing.io.Clicker
import com.example.arknightsautoclicker.processing.io.TextRecognizer
import com.example.arknightsautoclicker.processing.tasks.recruitment.ClickInst

class RoomAssignment(
    val clicker: Clicker,
    val recognizer: TextRecognizer,
    val x: Int, val y: Int
): Instance<Boolean>() {
    val summary = RoomUI(clicker, recognizer)
    val assignment = AssignmentUI(clicker, recognizer)
    suspend fun assignOps(count: Int) {
        val select = ArrayList<DormAssignment.SelectInst>()
        for (i in 0 until 5) {
            val slot = assignment.slots[i]
            if (slot.isSelected(tick)) continue
            select.add(DormAssignment.SelectInst(slot, true))
            if (select.size == count) break
        }
        join(TaskInstance.multi(select))
        join(ClickInst(assignment.confirm1))
        awaitTick()
        join(ClickInst(assignment.confirm2))
        do awaitTick()
        while (!summary.overviewLabel.matchesLabel(tick))
    }
    override suspend fun run(): MyResult<Boolean> {
        awaitTick()
        val y = summary.locateRoomTop(tick, y)
        summary.setPos(y)
        val nSelect = summary.countEmpty(tick)
        if (nSelect > 0) {
            join(ClickInst(
                summary.overviewLabel, summary.slots[0]
            ))
            assignOps(nSelect)
        }
        return MyResult.Success(true)
    }
}