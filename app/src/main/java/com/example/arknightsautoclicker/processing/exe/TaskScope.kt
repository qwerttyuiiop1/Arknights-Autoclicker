package com.example.arknightsautoclicker.processing.exe

import android.graphics.Bitmap
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel

open class TaskScope<T>: TaskInstance<T>, CoroutineScope {
    private val resultChannel = Channel<MyResult<T>?>(Channel.RENDEZVOUS)
    private val tickChannel = Channel<Bitmap>(Channel.RENDEZVOUS)
    private var _tick: Bitmap? = null
    val tick get() = _tick!!

    private var closed = false
    val isClosed get() = closed
    val isStarted get() = scope != null

    private var scope: CoroutineScope? = null
    override val coroutineContext
        get() = scope!!.coroutineContext

    private fun check() {
        if (isClosed || !isStarted) throw CancellationException()
    }
    override fun start(scope: CoroutineScope) {
        if (isClosed || isStarted) throw IllegalStateException()
        this.scope = scope
    }
    suspend fun run(
        r: suspend TaskScope<T>.() -> MyResult<T>
    ) = check().let { r(this) }
    /**
     * run the task until it completes
     * on error, exit the task
     */
    suspend fun <U> join(r: TaskInstance<U>): U {
        val res = tryJoin(r)
        if (res !is MyResult.Success)
            complete(MyResult.Fail(
                if (res is MyResult.Fail) res.error
                else res
            ))
        return res.data
    }
    suspend fun <U> tryJoin(r: TaskInstance<U>): MyResult<U> {
        check()
        try {
            r.start(this)
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
    suspend fun awaitTick(): Bitmap {
        check()
        resultChannel.send(null)
        _tick = tickChannel.receive()
        return tick
    }

    /**
     * notify that the task is completed and exit
     */
    suspend fun complete(msg: MyResult<T>): Nothing {
        resultChannel.send(msg)
        close()
        throw CancellationException(msg.toString())
    }
    override fun close() {
        if (closed) return
        closed = true
        resultChannel.close()
        tickChannel.close()
    }
}