package com.example.arknightsautoclicker.processing.exe

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

abstract class Instance<T> private constructor(
    protected val ctx: TaskScope<T>,
): TaskInstance<T> by ctx {
    constructor(): this(TaskScope())
    protected abstract suspend fun run(): MyResult<T>
    override fun start(scope: CoroutineScope) {
        ctx.start(scope)
        ctx.launch {
            try {
                val res = run()
                exit(res)
            } finally {
                onClose()
                close()
            }
        }
    }
    protected val tick get() = ctx.tick
    protected suspend fun <U> join(r: TaskInstance<U>) = ctx.join(r)
    protected suspend fun awaitTick() = ctx.awaitTick()
    protected suspend fun exit(msg: MyResult<T>): Nothing = ctx.complete(msg)
    protected open suspend fun onClose() {}
}