package com.example.arknightsautoclicker.processing.tasks.autobattle

import android.view.LayoutInflater
import com.example.arknightsautoclicker.components.ui.TextArea
import com.example.arknightsautoclicker.components.ui.TextButton
import com.example.arknightsautoclicker.components.UIContext
import com.example.arknightsautoclicker.components.doOnMeasure
import com.example.arknightsautoclicker.databinding.AutobattleLayoutBinding

class AutoBattleUIBinding private constructor(
    binding: AutobattleLayoutBinding,
    ctx: UIContext,
) {
    companion object {
        suspend operator fun invoke(
            ctx: UIContext
        ): AutoBattleUIBinding {
            val inflater = LayoutInflater.from(ctx.ctx)
            val binding = AutobattleLayoutBinding.inflate(inflater)
            return binding.doOnMeasure(ctx) {
                AutoBattleUIBinding(it, ctx)
            }
        }
    }
    val startBtn = TextButton(binding.startBtn, ctx)
    val missionStartBtn = TextButton(binding.missionStartBtn, ctx)
    val missionResultBtn = TextButton(binding.missionResultBtn, ctx)
    val sanityLabel = TextArea(binding.sanityLabel, ctx)
    val sanityCostLabel = TextArea(binding.sanityCostLabel, ctx)
}
//class AutoBattleUIBinding(
//    ctx: UIContext,
//): UIGroup {
//    val clicker = ctx.clicker
//    val recognizer = ctx.recognizer
//    init {
//        val inflater = LayoutInflater.from(ctx.ctx)
//        val binding = AutobattleLayoutBinding.inflate(inflater)
//
//        TextButton(binding.startBtn, ctx)
//    }
//    val startBtn = TextButton(
//        Rect(
//            2037, 960, 2347, 1054
//        ),
//        clicker, recognizer,
//    ).apply { label = setOf("start") }
//    val missionStartBtn = TextButton(
//        Rect(
//            1793, 549, 1997, 969
//        ),
//        clicker, recognizer,
//        scale = 1/3f
//    ).apply { label = setOf("mission", "start") }
//    val missionResultBtn = TextButton(
//        Rect(
//            90, 265, 525, 430
//        ),
//        clicker, recognizer,
//        scale = 1/3f
//    ).apply { label = setOf("mission", "results") }
//    val sanityLabel = TextArea(
//        Rect(
//            2400 - 235, 35, 2400 - 90, 90
//        ),
//        recognizer,
//        scale = 1.5f
//    )
//    val sanityCostLabel = TextArea(
//        Rect(
//            2400 - 150, 1080 - 45, 2400 - 80, 1070
//        ),
//        recognizer,
//        scale = 1.5f
//    )
//}