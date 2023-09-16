package com.example.arknightsautoclicker.processing.exe

import android.graphics.Bitmap
import com.example.arknightsautoclicker.processing.io.Screenshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * For executing tasks
 */
class TaskExecutor(
    private val screenshot: Screenshot,
) {
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)

    private var job: Job? = null
    var runner: TaskRunner? = null
        set(value) {
            stop()
            field = value
        }
    var onTaskComplete: ((TaskResult) -> Unit)? = null

    private suspend fun startLoop(runner: TaskRunner) = coroutineScope {
        runner.start(this)
        while (true) {
            runner.awaitResult()?.let { stopSync(it) }
            var tick: Bitmap?
            do {
                delay(runner.tickRate)
                tick = screenshot.latestRawBitmap
            } while (tick == null)
            //Log.d("COROUTINE_TEST", "TaskExecutor: sending tick")
            runner.nextTick(tick)
        }
    }

    fun stop() {
        if (job == null) return
        job?.cancel()
        runner?.stop()
        job = null
    }
    private suspend fun stopSync(res: TaskResult) {
        scope.launch {
            job?.cancelAndJoin()
            stop()
            onTaskComplete?.invoke(res)
        }.join()
    }

    fun start() {
        if (job != null || runner == null) return
        job = scope.launch { startLoop(runner!!) }
    }

    fun close() {
        scope.launch {
            job?.cancelAndJoin()
            stop()
            scope.cancel()
        }
    }
}