package com.example.arknightsautoclicker.andorid.ui

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import com.example.arknightsautoclicker.R
import com.torrydo.floatingbubbleview.BubbleBehavior
import com.torrydo.floatingbubbleview.ExpandableView
import com.torrydo.floatingbubbleview.FloatingBubble

class BubbleInflater(
    private val ctx: Context,
    private val onShow: ()->Unit = {},
    private val onHidden: ()->Unit = {},
) {
    private val _bubble: ImageView
    init {
        val bubblePx = ctx.resources.getDimension(R.dimen.bubble_size).toInt()
        _bubble = ImageView(ctx).apply {
            setImageResource(R.mipmap.ic_launcher_round)
            layoutParams = FrameLayout.LayoutParams(bubblePx, bubblePx)
            imageAlpha = 255/2
        }
    }
    val bubble: View = _bubble
    private val listener = object : FloatingBubble.Listener {
        val handler = Handler(Looper.getMainLooper())
        val run = Runnable {
            _bubble.imageAlpha = 255/2
        }
        override fun onUp(x: Float, y: Float) {
            handler.postDelayed(run, 500)
        }
        override fun onDown(x: Float, y: Float) {
            handler.removeCallbacks(run)
            _bubble.imageAlpha = 255
        }
    }

    fun setupBubble(action: FloatingBubble.Action): FloatingBubble.Builder  {
        val density = ctx.resources.displayMetrics.density
        val closeSize: Int = (ctx.resources.getDimension(R.dimen.close_bubble_size) / density).toInt()
        bubble.setOnClickListener {
            action.navigateToExpandableView()
        }
        (bubble.parent as? ViewGroup)?.removeView(bubble)

        return FloatingBubble.Builder(ctx).apply {
            bubble(bubble)
            closeBubble(
                com.torrydo.floatingbubbleview.R.drawable.ic_close_bubble,
                closeSize, closeSize
            )
            distanceToClose(closeSize)
            behavior(BubbleBehavior.DYNAMIC_CLOSE_BUBBLE)
            addFloatingBubbleListener(listener)
        }
    }


    private val contentWrapper = object : FrameLayout(ctx) {
        var action: ExpandableView.Action? = null
        fun setup(action: ExpandableView.Action) {
            this.action = action
            setOnClickListener { action.popToBubble() }
        }
        override fun dispatchKeyEvent(event: KeyEvent): Boolean {
            if (event.keyCode == KeyEvent.KEYCODE_BACK) {
                action?.popToBubble()
                return true
            }
            return super.dispatchKeyEvent(event)
        }
    }
    val expandableView: View = contentWrapper

    fun setupBubbleContent(action: ExpandableView.Action): ExpandableView.Builder {
        contentWrapper.setup(action)
        if (contentWrapper.parent != null)
            (contentWrapper.parent as ViewGroup).removeView(contentWrapper)
        return ExpandableView.Builder(ctx).apply {
            view(expandableView)
            dimAmount(0.8f)
        }.addExpandableViewListener(object : ExpandableView.Listener {
            override fun onCloseExpandableView() = onHidden()
            override fun onOpenExpandableView() = onShow()
        })
    }

    fun setContent(view: View): BubbleInflater {
        contentWrapper.removeAllViews()
        contentWrapper.addView(view)
        return this
    }
}