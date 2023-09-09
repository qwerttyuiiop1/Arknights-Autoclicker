package com.example.arknightsautoclicker.processing.tasks

import android.content.Context
import com.example.arknightsautoclicker.processing.exe.TaskExecutor
import com.example.arknightsautoclicker.processing.exe.TaskResult
import com.example.arknightsautoclicker.processing.io.Clicker
import com.example.arknightsautoclicker.processing.io.Screenshot
import com.example.arknightsautoclicker.processing.tasks.autobattle.AutoBattleTask
import com.example.arknightsautoclicker.processing.io.ImageDump
import com.example.arknightsautoclicker.processing.io.TextRecognizer
import com.example.arknightsautoclicker.processing.tasks.base.BaseTaskRunner
import com.example.arknightsautoclicker.processing.tasks.recruitment.ContinuousRecruitmentTask
import com.example.arknightsautoclicker.processing.tasks.recruitment.RecruitmentTask
import com.example.arknightsautoclicker.processing.tasks.recruitment.TagAnalyzer
import kotlin.IllegalArgumentException

/**
 * performs tasks
 */
class TaskHandler(
    private val screenshot: Screenshot,
    private val ctx: Context,
    private val clicker: Clicker
) {
    private val executor = TaskExecutor(screenshot)
    private val recognizer = TextRecognizer()
    private var currentTask = Task.NONE
    var onTaskChangeListener: ((Task)->Unit)? = null
    var onCompleteListener: ((TaskResult)->Unit)? = null

    private var isPaused = false

    init {
        executor.onTaskComplete = { res ->
            if (currentTask ==
                    executor.runner?.task)
                setTask(Task.NONE)
            onCompleteListener?.invoke(res)
        }
    }

    fun pause() {
        isPaused = true
        executor.stop()
    }
    fun resume() {
        isPaused = false
        executor.start()
    }

    private fun setTask(task: Task) {
        currentTask = task
        executor.runner = null
        onTaskChangeListener?.invoke(task)
    }
    fun performTask(task: Task) {
        if (currentTask == task && task.isLongRunning)
            return

        setTask(task)
        when (task) {
            Task.NONE -> {}
            Task.SCREENSHOT -> screenshot.latestBitmap?.let {
                ImageDump(ctx).dump(it)
            }
            Task.BATTLE -> executor.runner =
                AutoBattleTask(clicker, recognizer)
            Task.RECRUIT -> executor.runner =
                RecruitmentTask(clicker, recognizer, TagAnalyzer())
            Task.CONTINUOUS_RECRUIT -> executor.runner =
                ContinuousRecruitmentTask(clicker, recognizer, TagAnalyzer())
            Task.BASE -> executor.runner =
                BaseTaskRunner(clicker, recognizer)
            else -> throw IllegalArgumentException("Unsupported task: ${task.name}")
        }

        if (task.isLongRunning) {
            if (!isPaused)
                executor.start()
        } else {
            setTask(Task.NONE)
        }
    }

    fun close() = executor.close()
}