package com.example.arknightsautoclicker.processing.tasks.base

import android.graphics.Bitmap
import android.graphics.Rect
import com.example.arknightsautoclicker.processing.components.SwipeArea
import com.example.arknightsautoclicker.processing.components.similarTo
import com.example.arknightsautoclicker.processing.exe.Instance
import com.example.arknightsautoclicker.processing.exe.MyResult
import com.example.arknightsautoclicker.processing.io.Clicker

class OverviewScrollBar(
    clicker: Clicker
) {
    companion object {
        // min scroll distance that can be detected
        const val MIN_SCROLL = 30
    }

    val area = SwipeArea(
        clicker,
        Rect(1117, 122, 2314, 1080),
        speed = 0.25f,
        hold = 333
    )
    // scroll position constants
    private val scrollTop = 122
    private val scrollBot = 1017
    private val scrollX = 2359

    // the current position (top of the screen) in the scrollable area
    val currPos get() = _currPos
    private var _currPos = -1
    // the raw position of the top of the scroll bar in the screenshot
    private var scrollbarTop = -1
        set(value) {
            _currPos = ((value - scrollTop) * scrollScale).toInt()
            field = value
        }


    // the total height of the scrollable area
    var totalHeight = -1
        private set
    // the scrollPos when the scrollbar is at the bottom
    private var endPos = -1
    // how much each pixel in the scroll bar corresponds to the actual scrollable area
    private var scrollScale = -1.0
    // acceptable error in pixels when scrolling
    private var pixErr = -1

    private fun Bitmap.isScrollBar(i: Int): Boolean {
        val scrollColor = 0x006c6c6c
        return getPixel(scrollX, i).similarTo(scrollColor, 18)
    }
    private fun locateScrollBar(screen: Bitmap): Int {
        var step = scrollBot - scrollTop
        while (step > 0) {
            for (i in (scrollTop + step / 2)..scrollBot step step)
                if (screen.isScrollBar(i))
                    return i
            step /= 2
        }
        throw IllegalStateException("Could not find scroll bar")
    }
    private fun updateScrollPos(
        screen: Bitmap, loc: Int = locateScrollBar(screen)
    ) {
        var lo = scrollTop
        var hi = loc
        while (lo <= hi) {
            val mid = (lo + hi) / 2
            if (screen.isScrollBar(mid))
                hi = mid - 1
            else
                lo = mid + 1
        }
        scrollbarTop = lo
    }

    fun setup(screen: Bitmap) {
        var minBot = locateScrollBar(screen)
        updateScrollPos(screen, minBot)
        var bot = scrollBot
        while (minBot <= bot) {
            val mid = (minBot + bot) / 2
            if (screen.isScrollBar(mid))
                minBot = mid + 1
            else
                bot = mid - 1
        }
        val h = area.rect.height()
        val scrollbarH = bot - scrollbarTop + 1
        scrollScale = h / scrollbarH.toDouble()
        pixErr = (scrollScale * 3).toInt()
        val scrollH = scrollBot - scrollTop + 1
        totalHeight = (scrollH * scrollScale).toInt()
        endPos = totalHeight - h
    }

    fun isAtTop() = currPos in 0..pixErr
    fun isAtBottom() = (endPos - currPos) in 0..pixErr

    /**
     * @return the actual number of pixels scrolled
     */
    inner class ScrollInst(
        val target: Int = currPos
    ): Instance<Int>() {
        override suspend fun run(): MyResult<Int> {
            val maxScroll = area.rect.height() - 1
            awaitTick()
            updateScrollPos(tick)
            var diff = target - currPos
            while (
                (diff > pixErr && !isAtBottom()) ||
                (diff < -pixErr && !isAtTop())
            ) {
                // if diff is too small, it will not scroll at all
                // to scroll that amount, we scroll a larger amount
                // then undo it in the next iteration
                diff = if (diff in -MIN_SCROLL..MIN_SCROLL)
                    if (diff > 0) MIN_SCROLL * 2 else -MIN_SCROLL * 2
                else
                    diff.coerceIn(-maxScroll, maxScroll)

                area.swipeV(diff)
                awaitTick()
                updateScrollPos(tick)
                diff = target - currPos
            }
            return MyResult.Success(currPos - target)
        }
    }
    fun scrollToTop() = ScrollInst(0)
    fun scrollDown(px: Int) = ScrollInst(currPos + px)
    fun update() = ScrollInst()
}