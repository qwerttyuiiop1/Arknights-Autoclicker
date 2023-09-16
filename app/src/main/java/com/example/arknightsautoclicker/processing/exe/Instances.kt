package com.example.arknightsautoclicker.processing.exe

import android.graphics.Bitmap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

typealias ResList<T> = List<MyResult.Success<out T>>
fun <T> ResList<T>.flatten() = map { it.data }
/**
 * wrapper Instance running multiple instances
 * with synchronized ticks in a single scope
 */
open class MultiInstance<T>(
    val tasks: List<TaskInstance<out T>>
): Instance<ResList<T>>() {
    override suspend fun run(): MyResult<ResList<T>> {
        tasks.map { it.start(ctx) }
        val results = MutableList<MyResult<out T>?>(tasks.size) { null }
        var tasks = tasks.mapIndexed { i, it ->
            i to it
        }
        while (true) {
            awaitTick()
            val list = tasks.map { (_, task) ->
                ctx.async {
                    val res = task.awaitResult()
                    res ?: task.nextTick(tick)
                    res
                }
            }
            tasks = tasks.filterIndexed { listI, (resI, _) ->
                val res = list[listI].await()
                if (res != null) {
                    results[resI] = res
                    if (res !is MyResult.Success)
                        return MyResult.Fail(results)
                    false
                } else {
                    true
                }
            }
            if (tasks.isEmpty())
                @Suppress("UNCHECKED_CAST")
                return MyResult.Success(results as ResList<T>)
        }
    }
    override fun close() {
        tasks.forEach { it.close() }
        super.close()
    }
}
fun <T> TaskInstance.Companion.multi(
    tasks: List<TaskInstance<out T>>
) = MultiInstance(tasks)
fun <T> TaskInstance.Companion.multi(
    vararg tasks: TaskInstance<out T>
) = multi(tasks.toList())

/**
 * wrapper Instance running multiple instances one after another
 */
open class ChainedInstance<T>(
    val tasks: List<TaskInstance<out T>>,
): Instance<ResList<T>>() {
    override suspend fun run(): MyResult<ResList<T>> {
        awaitTick()
        val results = MutableList<MyResult<out T>?>(tasks.size) { null }
        tasks.forEachIndexed { i, it ->
            val res = ctx.tryJoin(it)
            results[i] = res
            if (res !is MyResult.Success)
                return MyResult.Fail(results)
        }
        @Suppress("UNCHECKED_CAST")
        return MyResult.Success(results as ResList<T>)
    }
}
fun <T> TaskInstance.Companion.chained(
    tasks: List<TaskInstance<out T>>
) = ChainedInstance(tasks)
fun <T> TaskInstance.Companion.chained(
    vararg tasks: TaskInstance<out T>
) = chained(tasks.toList())

abstract class SimpleInstance<T>: TaskInstance<T> {
    abstract suspend fun run(tick: Bitmap): MyResult<T>
    private var scope: CoroutineScope? = null
    private var res: Deferred<MyResult<T>>? = null
    override suspend fun nextTick(tick: Bitmap) {
        res = scope!!.async { run(tick) }
    }
    override suspend fun awaitResult() = res?.let {
        close()
        it.await()
    }
    override fun start(scope: CoroutineScope) {
        this.scope = scope
    }
    override fun close() {
        scope = null
        res = null
    }
}
private class SimpleImpl<T>(
    private val a: suspend (Bitmap) -> MyResult<T>
): SimpleInstance<T>() {
    override suspend fun run(tick: Bitmap) = a(tick)
}
fun <T> TaskInstance.Companion.simple(
    run: suspend (Bitmap) -> MyResult<T>
) = SimpleImpl(run) as SimpleInstance<T>
private class DefaultImpl<T>(
    val a: suspend TaskScope<T>.() -> MyResult<T>
): TaskScope<T>() {
    override fun start(scope: CoroutineScope) {
        super.start(scope)
        launch {
            try {
                complete(run(a))
            } finally {
                close()
            }
        }
    }
}
operator fun <T> TaskInstance.Companion.invoke(
    run: suspend TaskScope<T>.() -> MyResult<T>
) = DefaultImpl(run) as TaskInstance<T>