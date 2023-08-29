package com.example.arknightsautoclicker.processing.exe

import com.example.arknightsautoclicker.processing.tasks.Task
import kotlinx.coroutines.CompletableDeferred

typealias Promise<T> = CompletableDeferred<T>
@Suppress("FunctionName", "NOTHING_TO_INLINE")
// the original is also created by a function
inline fun <T> Promise(): Promise<T> = CompletableDeferred()

sealed interface MyResult<T> {
    val isSuccess get() = this is Success
    val isFail get() = this is Fail
    fun getOrNull() = if (this is TaskResult.Success) data else null

    open class Success<T>(
        val data: T,
    ) : MyResult<T>
    open class Fail<T>(
        val error: Any,
    ) : MyResult<T>
    fun asTaskResult(task: Task): TaskResult =
        when (this) {
            is Success -> TaskResult.Success(task, data.toString())
            is Fail -> TaskResult.Fail(task, error)
            else -> throw IllegalStateException()
        }
}
sealed interface TaskResult : MyResult<String?> {
    val task: Task
    class Success(
        override val task: Task,
        message: String? = null,
    ) : MyResult.Success<String?>(message), TaskResult {
        val message get() = data
    }
    class Fail(
        override val task: Task,
        data: Any,
    ) : MyResult.Fail<String?>(data), TaskResult
}