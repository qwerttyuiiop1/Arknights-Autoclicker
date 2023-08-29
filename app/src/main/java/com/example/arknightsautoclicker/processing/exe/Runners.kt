package com.example.arknightsautoclicker.processing.exe

import android.graphics.Bitmap
import com.example.arknightsautoclicker.processing.tasks.Task
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * For doing long-running and sequential tick-based tasks
 */
abstract class ResetRunner: TaskRunner {
    private var currTask: TaskInstance<*>? = null
    abstract fun newInstance(): TaskInstance<*>

    override suspend fun nextTick(tick: Bitmap) =
        currTask!!.nextTick(tick)
    override suspend fun awaitResult(): TaskResult? =
        currTask!!.awaitResult()?.let {
            stop()
            it.asTaskResult(task)
        }

    override fun start(scope: CoroutineScope){
        if (currTask != null) return
        currTask = newInstance().also { it.start(scope) }
    }
    override fun stop() {
        currTask?.close()
        currTask = null
    }
    override fun close() = stop()
}
private class ResetImpl(
    override val task: Task,
    private val factory: () -> TaskInstance<*>
): ResetRunner() {
    override fun newInstance() = factory()
}
fun TaskRunner.Companion.resetRunner(
    factory: () -> TaskInstance<*>,
    task: Task
) = ResetImpl(task, factory) as ResetRunner

abstract class ResumeRunner: TaskRunner {
    private var currTask: TaskInstance<*>? = null
    abstract fun newInstance(): TaskInstance<*>
    private var isRunning = false

    override suspend fun nextTick(tick: Bitmap) {
        if (!isRunning)
            throw IllegalStateException("task is not running")
        currTask!!.nextTick(tick)
    }
    override suspend fun awaitResult(): TaskResult? {
        if (!isRunning)
            throw IllegalStateException("task is not running")
        return currTask!!.awaitResult()?.let {
            close()
            it.asTaskResult(task)
        }
    }

    override fun start(scope: CoroutineScope){
        isRunning = true
        if (currTask == null)
            currTask = newInstance().apply { start(scope) }
    }
    override fun stop() {
        isRunning = false
    }
    override fun close() {
        stop()
        currTask?.close()
        currTask = null
    }
}
private class ResumeImpl(
    override val task: Task,
    private val factory: () -> TaskInstance<*>
): ResumeRunner() {
    override fun newInstance() = factory()
}
fun TaskRunner.Companion.resumeRunner(
    task: Task,
    factory: () -> TaskInstance<*>,
) = ResumeImpl(task, factory) as ResumeRunner