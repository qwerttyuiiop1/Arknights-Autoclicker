package com.example.arknightsautoclicker.andorid

import android.accessibilityservice.AccessibilityService
import android.app.Activity
import android.content.Intent
import android.os.Looper
import android.view.accessibility.AccessibilityEvent
import com.example.arknightsautoclicker.AppController
import com.example.arknightsautoclicker.andorid.ui.SharedForegroundNotif


/**
 * handles init and close and service related stuff
 * ACTION_INIT:             starts the service; accepts a boolean if the bubble should be shown
 * ACTION_INIT_PERMISSION:  ACTION_INIT but with a media projection intent
 * ACTION_CLOSE:            closes the service and frees resources, once closed, the service will
 *                          no longer accept actions until it is fully destroyed
 * ELSE:                    forwards the task to AppController
 */
class AutoclickService : AccessibilityService() {
    companion object {
        const val ACTION_INIT = "action_init"
        const val ACTION_CLOSE = "action_close"
        const val ACTION_INIT_PERMISSION = "action_init_permission"

        const val INTENT_REQUEST_CODE = 200
        const val EXTRA_RESULT_CODE = "extra_result_code"
        const val EXTRA_RESULT_DATA = "extra_result_data"
        const val EXTRA_HAS_BUBBLE = "extra_has_bubble"
    }

    private var appController: AppController? = null
    val handler = android.os.Handler(Looper.getMainLooper())
    private var cached: Intent? = null

    override fun onCreate() {
        super.onCreate()
        startForeground(
            SharedForegroundNotif.NOTIFICATION_ID,
            SharedForegroundNotif.build(this, false)
        )
    }
    override fun onDestroy() {
        super.onDestroy()
        close()
    }

    override fun onAccessibilityEvent(p0: AccessibilityEvent?){}
    override fun onInterrupt() {}

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action != null)
            handleAction(intent.action!!, intent)
        return super.onStartCommand(intent, flags, startId)
    }

    fun handleAction(action: String, intent: Intent? = null) {
        when (action) {
            ACTION_CLOSE -> close()
            ACTION_INIT ->
                if (appController == null)
                    if (cached != null) {
                        handleAction(ACTION_INIT_PERMISSION, cached)
                    } else {
                        startActivity(
                            Intent(this,
                                PermissionLauncher::class.java).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                if (intent != null) putExtras(intent)
                            }
                        )
                    }
                else {
                    appController!!.handleAction(AppController.ACTION_BUBBLE)
                }
            ACTION_INIT_PERMISSION -> {
                if (appController == null) {
                    cached = intent!!.clone() as Intent
                    val resultCode =
                        intent.getIntExtra(EXTRA_RESULT_CODE, Activity.RESULT_CANCELED)
                    if (resultCode == Activity.RESULT_OK) {
                        val hasBubble = intent.getBooleanExtra(EXTRA_HAS_BUBBLE, true)
                        startForeground(
                            SharedForegroundNotif.NOTIFICATION_ID,
                            SharedForegroundNotif.build(this, hasBubble)
                        )
                        appController = AppController(this)
                        appController!!.onCreate(intent)
                    }
                }
            }
            else -> appController?.handleAction(action)
        }
    }

    private fun close() {
        appController?.close()
        appController = null
        stopForeground(STOP_FOREGROUND_REMOVE)
    }
}