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