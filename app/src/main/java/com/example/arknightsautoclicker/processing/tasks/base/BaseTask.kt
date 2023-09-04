package com.example.arknightsautoclicker.processing.tasks.base

import android.util.Log
import com.example.arknightsautoclicker.processing.exe.MyResult
import com.example.arknightsautoclicker.processing.exe.ResetRunner
import com.example.arknightsautoclicker.processing.exe.TaskInstance
import com.example.arknightsautoclicker.processing.exe.simple
import com.example.arknightsautoclicker.processing.io.Clicker
import com.example.arknightsautoclicker.processing.io.TextRecognizer
import com.example.arknightsautoclicker.processing.tasks.Task

class BaseTask(
    val clicker: Clicker,
    val recognizer: TextRecognizer
): ResetRunner() {
    override fun newInstance() = OverviewIterator(
        clicker, recognizer
    ) { x, y, block ->
        TaskInstance.simple {
            Log.d("BaseTask", "Room at $x, $y: ${block.text}")
            MyResult.Success(Unit)
        }
    }

    override val task = Task.BASE
}