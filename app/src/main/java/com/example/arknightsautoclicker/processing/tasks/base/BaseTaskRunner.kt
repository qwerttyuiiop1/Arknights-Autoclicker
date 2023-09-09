package com.example.arknightsautoclicker.processing.tasks.base

import com.example.arknightsautoclicker.processing.exe.Instance
import com.example.arknightsautoclicker.processing.exe.MyResult
import com.example.arknightsautoclicker.processing.exe.ResetRunner
import com.example.arknightsautoclicker.processing.exe.TaskInstance
import com.example.arknightsautoclicker.processing.exe.chained
import com.example.arknightsautoclicker.processing.ext.norm
import com.example.arknightsautoclicker.processing.io.Clicker
import com.example.arknightsautoclicker.processing.io.TextRecognizer
import com.example.arknightsautoclicker.processing.tasks.Task

class BaseTaskRunner(
    val clicker: Clicker,
    val recognizer: TextRecognizer
): ResetRunner() {
    inner class BaseInstance: Instance<String>() {
        suspend fun doAssignments(){
            join(TaskInstance.chained(
                OverviewIterator(
                    clicker, recognizer,
                    each = { x, y ->
                        DormAssignment(clicker, recognizer, x, y)
                    },
                    filter = { block ->
                        block.text.contains("Dormitory")
                    }
                ),
                OverviewIterator(
                    clicker, recognizer,
                    each = { x, y ->
                        RoomAssignment(clicker, recognizer, x, y)
                    },
                    filter = { block ->
                        val t = block.text.norm
                        t.contains("Control Center".norm) ||
                        t.contains("Reception Room".norm) ||
                        t.contains("Trading Post".norm) ||
                        t.contains("Power Plant".norm) ||
                        t.contains("Workshop".norm) ||
                        t.contains("Factory".norm) ||
                        t.contains("Office".norm)
                    }
                )
            ))
        }
        override suspend fun run(): MyResult<String> {
            TODO()
        }
    }
    override fun newInstance() = TaskInstance.chained(
        OverviewIterator(
            clicker, recognizer,
            each = { x, y ->
                DormAssignment(clicker, recognizer, x, y)
            },
            filter = { block ->
                block.text.contains("Dormitory")
            }
        ),
        OverviewIterator(
            clicker, recognizer,
            each = { x, y ->
                RoomAssignment(clicker, recognizer, x, y)
            },
            filter = { block ->
                val t = block.text.norm
                t.contains("Control Center".norm) ||
                t.contains("Reception Room".norm) ||
                t.contains("Trading Post".norm) ||
                t.contains("Power Plant".norm) ||
                t.contains("Workshop".norm) ||
                t.contains("Factory".norm) ||
                t.contains("Office".norm)
            }
        )
    )

    override val task = Task.BASE
}