package com.example.arknightsautoclicker.processing.tasks.recruitment

import android.graphics.Bitmap
import com.example.arknightsautoclicker.components.ui.TextButton
import com.example.arknightsautoclicker.processing.exe.Instance
import com.example.arknightsautoclicker.processing.exe.MyResult
import com.example.arknightsautoclicker.processing.exe.Promise
import com.example.arknightsautoclicker.processing.exe.SimpleInstance
import com.example.arknightsautoclicker.processing.exe.TaskInstance
import com.example.arknightsautoclicker.processing.exe.multi
import com.example.arknightsautoclicker.processing.ext.flattenString
import com.example.arknightsautoclicker.processing.ext.norm

/**
 * test for concurrent analysis of tags; the original is slow
 */
internal class RecruitPage(
    val ui: RecruitmentUIBinding,
    val analyzer: TagAnalyzer,
): Instance<Unit>() {

    val timer = ui.recruit.timer
    val recruit = ui.recruit

    class TagCombInst(
        val btns: List<TextButton>,
        val analyzer: TagAnalyzer,
        val res: Promise<Pair<Int, List<TextButton>>>
    ): Instance<Unit>() {
        override suspend fun run(): MyResult<Unit> {
            while (true) {
                awaitTick()
                val tagMap = join(
                    TaskInstance.multi(btns.map { TextInst(it) })
                ).associate {
                    val (btn, text) = it.data
                    text.flattenString("").norm to btn
                }
                val tags = tagMap.keys.toList()
                if (tags.size != 5 ||
                    tags.any { it !in analyzer.availableTagsNorm })
                    continue // bad result; retry
                val comb = analyzer.getBestCombination(tags)
                if (comb.minRarity >= 5)
                    return MyResult.Fail("5 star or higher combination found!")
                res.complete(comb.minRarity to comb.tags.map { tagMap[it]!! })
                return MyResult.Success(Unit)
            }
        }
    }
    class HasRefreshInst(
        val btn: TextButton,
        val res: Promise<Boolean>
    ): SimpleInstance<Unit>() {
        override suspend fun run(tick: Bitmap): MyResult<Unit> {
            res.complete(btn.matchesLabel(tick))
            return MyResult.Success(Unit)
        }
    }
    inner class TagSelectInst(
        val tag: RecruitmentUIBinding.TagButton,
        val select: Boolean
    ): Instance<Unit>() {
        override suspend fun run(): MyResult<Unit> {
            awaitTick()
            var i=0
            while (tag.isSelected(tick) != select) {
                if (i++ % 8 == 0)
                    tag.click()
                awaitTick()
            }
            return MyResult.Success(Unit)
        }
    }
    inner class MinsInst(val rarity: Int): Instance<Unit>() {
        suspend fun getMins(): Int {
            while (true) {
                awaitTick()
                val res = timer.timerMins.getText(tick)
                    .flattenString("").toIntOrNull()
                    ?: continue
                if (res !in 0..50)
                    continue
                return res
            }
        }
        override suspend fun run(): MyResult<Unit> {
            if (rarity >= 4) // this is 9:00; mins don't matter
                return MyResult.Success(Unit)
            while (true) {
                when (getMins()) {
                    40 -> return MyResult.Success(Unit)
                    in 10..40 -> timer.increaseMinsBtn.click()
                    else -> timer.decreaseMinsBtn.click()
                }
            }
        }
    }
    inner class HoursInst(val rarity: Int): Instance<Unit>() {
        suspend fun getHours(): Int {
            while (true) {
                awaitTick()
                val res = timer.timerHours.getText(tick)
                    .flattenString("").toIntOrNull()
                    ?: continue
                if (res !in 0..9)
                    continue
                return res
            }
        }
        override suspend fun run(): MyResult<Unit> {
            if (rarity >= 4) {
                while (true) {
                    when (getHours()) {
                        9 -> return MyResult.Success(Unit)
                        in 4..9 -> timer.increaseHoursBtn.click()
                        else -> timer.decreaseHoursBtn.click()
                    }
                }
            }
            while (true) {
                when (getHours()) {
                    7 -> return MyResult.Success(Unit)
                    in 2..7 -> timer.increaseHoursBtn.click()
                    else -> timer.decreaseHoursBtn.click()
                }
            }
        }
    }

    suspend fun getComb(): Pair<Int, List<TextButton>>? {
        val p1 = Promise<Pair<Int, List<TextButton>>>()
        val p2 = Promise<Boolean>()
        join(TaskInstance.multi(
            TagCombInst(recruit.tagBtns, analyzer, p1),
            HasRefreshInst(recruit.refreshBtn, p2)
        ))

        val (rarity, tags) = p1.await()
        val refresh = p2.await()

        return if (rarity <= 3 && refresh)
            null
        else
            rarity to tags
    }
    suspend fun refresh() {
        val refresh = recruit.refreshBtn
        val marker = recruit.tagBtns[0]
        val confirm = ui.other.confirmRefreshBtn
        join(TagSelectInst(marker, true))
        join(ClickInst(refresh))
        join(ClickInst(confirm))
        while (marker.isSelected(tick))
            awaitTick()
    }
    suspend fun selectOptions(rarity: Int, tags: List<TextButton>) {
        val tasks = mutableListOf(
            HoursInst(rarity),
            MinsInst(rarity)
        )
        recruit.tagBtns.mapTo(tasks) {
            TagSelectInst(it, it in tags)
        }
        join(TaskInstance.multi(tasks))
    }
    suspend fun confirm() = recruit.confirmBtn.click()

    override suspend fun run(): MyResult<Unit> {
        awaitTick()
        while (true) {
            val comb = getComb()
            if (comb == null) {
                refresh()
                continue
            }
            selectOptions(comb.first, comb.second)
            awaitTick()
            selectOptions(comb.first, comb.second) // to be sure
            awaitTick()
            selectOptions(comb.first, comb.second)
            confirm()
            return MyResult.Success(Unit)
        }
    }
}