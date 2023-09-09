package com.example.arknightsautoclicker.processing.exe

import android.graphics.Bitmap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield

abstract class Instance<T>: TaskInstance<T> {
    private val resultChannel = Channel<MyResult<T>?>(Channel.RENDEZVOUS)
    private val tickChannel = Channel<Bitmap>(Channel.RENDEZVOUS)
    private var _tick: Bitmap? = null
    protected val tick get() = _tick!!
    private var job: Job? = null
    private var scope: CoroutineScope? = null
    private var isClosed = false

    protected abstract suspend fun run(): MyResult<T>

    override fun start(scope: CoroutineScope) {
        if (job != null) throw IllegalStateException("task has already started")
        if (isClosed) throw IllegalStateException("task has already been closed")
        this.scope = scope
        job = scope.launch {
            try {
                val res = run()
                exit(res)
            } finally {
                close()
            }
        }
    }

    /**
     * run the task until it completes
     * on error, exit the task
     */
    protected suspend fun <U> join(r: TaskInstance<U>): U {
        val res = tryJoin(r)
        if (res !is MyResult.Success) {
            exit(MyResult.Fail(
                if (res is MyResult.Fail) res.error
                else res
            ))
            throw IllegalStateException("should not reach here")
        }
        return res.data
    }
    protected suspend fun <U> tryJoin(r: TaskInstance<U>): MyResult<U> {
        try {
            r.start(scope!!)
            r.awaitResult()?.let { return it }
            while (true) {
                r.nextTick(tick) // reuse the first tick
                r.awaitResult()?.let { return it }
                awaitTick()
            }
        } finally {
            r.close()
        }
    }

    override suspend fun nextTick(tick: Bitmap) =
        tickChannel.send(tick)
    override suspend fun awaitResult() =
        resultChannel.receive()
    /**
     * block until the next tick is received
     */
    protected suspend fun awaitTick(): Bitmap {
        if (isClosed) yield() // to prevent crash, since channels are closed
        resultChannel.send(null)
        _tick = tickChannel.receive()
        return tick
    }

    /**
     * notify that the task is complete
     * then close the task and wait for it to complete
     */
    protected suspend fun exit(msg: MyResult<T>) {
        scope?.launch {
            resultChannel.send(msg)
            job?.cancelAndJoin()
            close()
        }?.join()
        yield()
    }
    protected open suspend fun onClose() {
        job?.cancelAndJoin()
        resultChannel.close()
        tickChannel.close()
        job = null
        scope = null
    }
    override fun close() {
        if (isClosed) return
        isClosed = true
        scope?.launch { onClose() }
    }
}