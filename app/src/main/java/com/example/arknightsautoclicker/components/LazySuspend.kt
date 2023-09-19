package com.example.arknightsautoclicker.components

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class LazySuspend<T>(
    private val get: suspend ()->T
) {
    private var value: T? = null
    private var mutex = Mutex()
    operator fun getValue(
        thisRef: Any?, property: Any?
    ): suspend ()->T {
        if (value != null)
            return { value!! }
        return {
            mutex.withLock {
                if (value == null)
                    value = get()
                value!!
            }
        }
    }
}