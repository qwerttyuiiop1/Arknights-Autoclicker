package com.example.arknightsautoclicker.andorid.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.recyclerview.widget.RecyclerView
import com.example.arknightsautoclicker.R
import com.google.android.flexbox.FlexboxLayoutManager
import com.torrydo.floatingbubbleview.ExpandableView
import com.torrydo.floatingbubbleview.FloatingBubble
import com.torrydo.floatingbubbleview.FloatingBubbleService
import com.torrydo.floatingbubbleview.Route

/**
 * the bubble service, by default, the bubble is not shown
 * open class to allow sharing of the foreground notification
 */
class BubbleMenu: FloatingBubbleService(), LifecycleOwner {
    inner class Binder : android.os.Binder() {
        fun getService() = this@BubbleMenu
    }
    override fun onBind(intent: Intent?): IBinder {
        return Binder()
    }

    //lifecycle part
    private val lifecycleRegistry = LifecycleRegistry(this)
    override fun getLifecycle() = lifecycleRegistry
    init {
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
    }
    override fun onCreate() {
        super.onCreate()
        lifecycleRegistry.currentState = Lifecycle.State.STARTED
        startForeground(
            SharedForegroundNotif.NOTIFICATION_ID,
            SharedForegroundNotif.build(this, true)
        )
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        lifecycleRegistry.currentState = Lifecycle.State.RESUMED
        return super.onStartCommand(intent, flags, startId)
    }
    override fun onDestroy() {
        super.onDestroy()
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        close()
    }
    private var isClosed = false
    fun close() {
        if (isClosed) return
        isClosed = true

        removeAllViews()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    //bubble part
    override fun initialRoute() = Route.Empty
    override fun initialNotification() = null


    private lateinit var inflater: BubbleInflater
    val bubble: View
        get() = inflater.bubble
    val expandableView: View
        get() = inflater.expandableView
    fun setController(controller: BubbleController) {
        @SuppressLint("InflateParams")
        val content = LayoutInflater.from(this)
            .inflate(R.layout.bubble_activity, null).apply {
                val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
                recyclerView.layoutManager = FlexboxLayoutManager(this@BubbleMenu)
                recyclerView.adapter = TaskAdapter(
                    controller.getTasks(),
                    this@BubbleMenu, controller,
                    this@BubbleMenu
                )
                //TODO: add gap
            }
        inflater = BubbleInflater(this,
            onShow = { controller.onVisible() },
            onHidden = { controller.onHidden() }
        ).setContent(content)
        showBubbles()
        showExpandableView()
    }

    private var bubbleAction: FloatingBubble.Action? = null
    private var viewAction: ExpandableView.Action? = null
    override fun setupBubble(action: FloatingBubble.Action): FloatingBubble.Builder {
        bubbleAction = action
        return inflater.setupBubble(action)
    }
    override fun setupExpandableView(action: ExpandableView.Action): ExpandableView.Builder {
        viewAction = action
        return inflater.setupBubbleContent(action)
    }

    fun show() {
        bubbleAction?.navigateToExpandableView()
    }
    fun hide() {
        viewAction?.popToBubble()
    }
}