package com.example.arknightsautoclicker.processing.tasks.recruitment
import com.example.arknightsautoclicker.processing.tasks.Task
import com.example.arknightsautoclicker.processing.io.Clicker
import com.example.arknightsautoclicker.processing.io.TextRecognizer
import com.example.arknightsautoclicker.processing.exe.ResetRunner
import com.example.arknightsautoclicker.processing.exe.Instance
import com.example.arknightsautoclicker.processing.exe.MyResult

open class RecruitmentTask(
    protected val clicker: Clicker,
    protected val recognizer: TextRecognizer,
    protected val analyzer: TagAnalyzer
) : ResetRunner() {
    override val task = Task.RECRUIT
    protected val ui = RecruitmentUIBinding(clicker, recognizer)
    override fun newInstance() = RecruitmentInstance(ui, analyzer) as Instance<String>

    open class RecruitmentInstance(
        val ui: RecruitmentUIBinding,
        val analyzer: TagAnalyzer
    ): Instance<String>() {
        val menu = ui.recruitMenu
        val recruit = ui.recruit
        val other = ui.other

        suspend fun inRecruitMenu() = menu.label.matchesLabel(tick)
        suspend fun inRecruit() = recruit.label.matchesLabel(tick)
        suspend fun navigateToRecruitMenu() {
            while (!inRecruitMenu()) {
                if (inRecruit())
                    recruit.cancelBtn.click()
                // can be safely clicked even if not present
                other.skipBtn.click()
                awaitTick()
            }
        }
        suspend fun completeRecruits() {
            navigateToRecruitMenu()
            for (btn in menu.completeBtns) {
                join(ClickInst(btn))
                navigateToRecruitMenu()
            }
        }
        suspend fun navigateToRecruit(): Boolean {
            if (inRecruit()) return true
            navigateToRecruitMenu()
            for (area in menu.recruitAreas) {
                if (!area.matchesLabel(tick)) continue
                var i = 0
                while (!inRecruit()) {
                    if (i++ % 5 == 0)
                        area.click()
                    awaitTick()
                }
                return true
            }
            return false
        }

        override suspend fun run(): MyResult<String> {
            while (true) {
                awaitTick()
                if (inRecruitMenu())
                    completeRecruits()
                if (!navigateToRecruit()) // no recruit available
                    return MyResult.Success("Recruit completed")
                join(RecruitPage(ui, analyzer))
                awaitTick()
                navigateToRecruitMenu()
            }
        }
    }
}