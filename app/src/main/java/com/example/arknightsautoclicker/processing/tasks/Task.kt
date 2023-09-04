package com.example.arknightsautoclicker.processing.tasks

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.arknightsautoclicker.R

/**
 * the tasks available on the bubble menu
 */
enum class Task(
    @DrawableRes
    val icon: Int,
    @StringRes
    val displayName: Int,
    val isLongRunning: Boolean,
) {
    NONE(R.drawable.ic_pause, R.string.pause, true),
    CLOSE(R.drawable.ic_close, R.string.close, false),
    SCREENSHOT(R.drawable.ic_screenshot, R.string.take_screenshot, false),
    BATTLE(R.drawable.ic_sanity, R.string.auto_battle, true),
    RECRUIT(R.drawable.ic_recruit, R.string.recruit, true),
    CONTINUOUS_RECRUIT(R.drawable.ic_cont_recruit, R.string.continuous_recruit, true),
    BASE(R.drawable.ic_base, R.string.base, true),
}