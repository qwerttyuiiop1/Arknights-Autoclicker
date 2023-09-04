package com.example.arknightsautoclicker.processing.tasks.recruitment

import com.example.arknightsautoclicker.processing.tasks.Task
import com.example.arknightsautoclicker.processing.exe.Instance
import com.example.arknightsautoclicker.processing.exe.MyResult
import com.example.arknightsautoclicker.processing.io.Clicker
import com.example.arknightsautoclicker.processing.io.TextRecognizer


private class ExpediteRecruitmentInstance(
    ui: RecruitmentUIBinding,
    analyzer: TagAnalyzer
): RecruitmentTask.RecruitmentInstance(ui, analyzer) {
    override suspend fun run(): MyResult<String> {
        awaitTick()

        while (true) {
            if (inRecruitMenu()) {
                for (btn in menu.expediteBtns) {
                    if (btn.matchesLabel(tick)) {
                        join(ClickInst(btn, 10))
                        join(ClickInst(other.confirmExpediteBtn, 10))
                    }
                }
            }
            super.run()
        }
    }
}

class ContinuousRecruitmentTask(
    clicker: Clicker,
    recognizer: TextRecognizer,
    analyzer: TagAnalyzer
): RecruitmentTask(clicker, recognizer, analyzer) {
    override val task = Task.CONTINUOUS_RECRUIT
    override fun newInstance() = ExpediteRecruitmentInstance(ui, analyzer) as Instance<String>
}