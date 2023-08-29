package com.example.arknightsautoclicker.processing.exe

import android.graphics.Bitmap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

typealias ResList<T> = List<MyResult.Success<out T>>
fun <T> MyResult.Success<ResList<T>>.flatten() =
    data.map{ it.data }
fun <T> MyResult<ResList<T>>.flatten() =
    (this as? MyResult.Success)?.flatten()
/**
 * wrapper Instance running multiple instances
 * concurrently in a single scope
 */
open class MultiInstance<T>(
    val tasks: List<TaskInstance<out T>>
): Instance<ResList<T>>() {
    private lateinit var scope: CoroutineScope
    override fun start(scope: CoroutineScope) {
        this.scope = scope
        tasks.forEach { it.start(scope) }
        super.start(scope)
    }
    override suspend fun run(): MyResult<ResList<T>> {
        val results = MutableList<MyResult<out T>?>(tasks.size) { null }
        var tasks = tasks.mapIndexed { i, it ->
            i to it
        }
        while (true) {
            val list = tasks.map {
                scope.async { it.second.awaitResult() }
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
            awaitTick()
            tasks.map { (_, it) ->
                scope.launch { it.nextTick(tick) }
            }.forEach { it.join() }
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

/**
 * wrapper Instance running multiple instances one after another
 */
open class ChainedInstance<T>(
    val tasks: List<TaskInstance<out T>>,
): Instance<ResList<T>>() {
    override suspend fun run(): MyResult<ResList<T>> {
        val results = MutableList<MyResult<out T>?>(tasks.size) { null }
        tasks.forEachIndexed { i, it ->
            val res = tryJoin(it)
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