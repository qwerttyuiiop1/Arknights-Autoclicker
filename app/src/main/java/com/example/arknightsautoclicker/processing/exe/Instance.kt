package com.example.arknightsautoclicker.processing.exe

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

abstract class Instance<T> private constructor(
    protected val scope: TaskScope<T>,
): TaskInstance<T> by scope {
    constructor(): this(TaskScope())
    protected abstract suspend fun run(): MyResult<T>
    override fun start(scope: CoroutineScope) {
        this.scope.start(scope)
        this.scope.launch {
            try {
                val res = run()
                exit(res)
            } finally {
                onClose()
                close()
            }
        }
    }
    protected val tick get() = scope.tick
    protected suspend fun <U> join(r: TaskInstance<U>) = scope.join(r)
    protected suspend fun awaitTick() = scope.awaitTick()
    protected suspend fun exit(msg: MyResult<T>): Nothing = scope.complete(msg)
    protected open suspend fun onClose() {}
}