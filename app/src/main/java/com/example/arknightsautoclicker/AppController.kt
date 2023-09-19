package com.example.arknightsautoclicker

import android.content.Context
import android.content.Intent
import com.example.arknightsautoclicker.andorid.AutoclickService
import com.example.arknightsautoclicker.processing.io.Clicker
import com.example.arknightsautoclicker.processing.io.RandomClicker
import com.example.arknightsautoclicker.processing.tasks.TaskHandler
import com.example.arknightsautoclicker.andorid.ui.BubbleController
import com.example.arknightsautoclicker.processing.io.Overlay
import com.example.arknightsautoclicker.processing.io.Screenshot
import com.example.arknightsautoclicker.processing.tasks.Task

/**
 * Links the components of the app
 */
class AppController (
    private val svc: AutoclickService
) {
    companion object {
        const val ACTION_BUBBLE = "action_bubble"
    }
    private val ctx: Context = svc
    private lateinit var taskHandler: TaskHandler
    private var uiController: BubbleController? = null
    private lateinit var clicker: Clicker
    private lateinit var screenshot: Screenshot

    fun onCreate(intent: Intent) {
        val hasBubble = intent.getBooleanExtra(
            AutoclickService.EXTRA_HAS_BUBBLE, true)
        clicker = RandomClicker(svc)
        screenshot = Screenshot(svc, intent)
        taskHandler = TaskHandler(
            screenshot, ctx, clicker, svc.handler
        )
        taskHandler.onTaskChangeListener = { task ->
            svc.handler.post {
                uiController?.onTaskSelect(task)
            }
        }
        taskHandler.onCompleteListener = { res ->
            svc.handler.post {
                uiController?.alert(res)
            }
        }
        taskHandler
        if (hasBubble) {
            uiController = BubbleController(svc).apply {
                onShowListener = { taskHandler.pause() }
                onHideListener = { taskHandler.resume() }
                onConfirmTaskListener = { handleTask(it) }
                onBindListener = {
                    screenshot.preprocess = Overlay(getOverlaysToHide()[0])
                }
                start()
            }
        }
    }

    /**
     * close this app controller
     */
    fun close() {
        taskHandler.close()
        uiController?.close()
        screenshot.close()
    }

    fun handleAction(action: String) {
        when (action) {
            ACTION_BUBBLE -> uiController?.showView()
            AutoclickService.ACTION_CLOSE -> svc.handleAction(action)
            else -> handleTask(Task.valueOf(action))
        }
    }

    fun handleTask(task: Task) {
        when (task) {
            Task.CLOSE -> svc.handleAction(AutoclickService.ACTION_CLOSE)
            else -> taskHandler.performTask(task)
        }
    }
}