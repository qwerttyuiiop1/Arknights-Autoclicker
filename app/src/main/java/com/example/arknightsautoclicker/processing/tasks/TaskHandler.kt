package com.example.arknightsautoclicker.processing.tasks

import android.content.Context
import android.content.Intent
import android.os.Handler
import com.example.arknightsautoclicker.components.UIContext
import com.example.arknightsautoclicker.processing.exe.TaskExecutor
import com.example.arknightsautoclicker.processing.exe.TaskResult
import com.example.arknightsautoclicker.processing.io.Clicker
import com.example.arknightsautoclicker.processing.io.Screenshot
import com.example.arknightsautoclicker.processing.tasks.autobattle.AutoBattleTask
import com.example.arknightsautoclicker.processing.io.ImageDump
import com.example.arknightsautoclicker.processing.io.TextRecognizer
import com.example.arknightsautoclicker.processing.tasks.base.BaseTask
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
    private val clicker: Clicker,
    handler: Handler
) {
    companion object {
        const val GITHUB_URL = "https://github.com/qwerttyuiiop1/Arknights-Autoclicker/"
    }
    private val executor = TaskExecutor(screenshot)
    private val recognizer = TextRecognizer()
    private val uiCtx = UIContext(ctx, clicker, recognizer, handler)
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
            Task.GITHUB -> {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = android.net.Uri.parse(GITHUB_URL)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                ctx.startActivity(intent)
            }
            Task.BATTLE -> executor.runner =
                AutoBattleTask(uiCtx)
            Task.RECRUIT -> executor.runner =
                RecruitmentTask(clicker, recognizer, TagAnalyzer())
            Task.CONTINUOUS_RECRUIT -> executor.runner =
                ContinuousRecruitmentTask(clicker, recognizer, TagAnalyzer())
            Task.BASE -> executor.runner =
                BaseTask(clicker, recognizer)
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