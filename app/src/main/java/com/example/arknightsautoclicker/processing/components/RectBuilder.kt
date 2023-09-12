@file:Suppress("unused")
package com.example.arknightsautoclicker.processing.components

import android.graphics.Rect

/**
 * TEST CLASS
 * Builder for fixed size [Rect] with various positioning
 * options relative to the size of the screen.
 * @param w Width of the Rect.
 * @param h Height of the Rect.
 */
class RectBuilder(
    val w: Int, val h: Int
) {
    private val b = this
    private var _x: (Int)->Int = { 0 }
    private var _y: (Int)->Int = { 0 }

    fun x(x: Int) = this.apply { _x = { x } }
    fun y(y: Int) = this.apply { _y = { y } }
    fun x(x: Float) = this.apply { _x = { (x * it).toInt() } }
    fun y(y: Float) = this.apply { _y = { (y * it).toInt() } }

    fun xTo(offset: Int = 0) = XOffset(offset)
    fun yTo(offset: Int = 0) = YOffset(offset)
    fun xTo(percent: Float) = XPercent(percent)
    fun yTo(percent: Float) = YPercent(percent)

    fun build(x: Int, y: Int) =
        Rect(0, 0, w, h).apply {
            offsetTo(_x(x), _y(y))
        }

    inner class XOffset(val offset: Int) {
        fun leftOf(l: Int) = b.apply {
            _x = { l - offset - w }
        }
        fun rightOf(r: Int) = b.apply {
            _x = { r + offset }
        }
        fun leftOf(l: Float) = b.apply {
            _x = { (l * it).toInt() - offset - w }
        }
        fun rightOf(r: Float) = b.apply {
            _x = { (r * it).toInt() + offset }
        }
        fun leftOf(l: RectBuilder) = b.apply {
            _x = { l._x(it) - offset - w }
        }
        fun rightOf(r: RectBuilder) = b.apply {
            _x = { r._x(it) + r.w + offset }
        }
    }
    inner class YOffset(val offset: Int) {
        fun topOf(a: Int) = b.apply {
            _y = { a - offset - h }
        }
        fun botOf(b: Int) = b.apply {
            _y = { b + offset }
        }
        fun topOf(a: Float) = b.apply {
            _y = { (a * it).toInt() - offset - h }
        }
        fun botOf(b: Float) = b.apply {
            _y = { (b * it).toInt() + offset }
        }
        fun topOf(a: RectBuilder) = b.apply {
            _y = { a._y(it) - offset - h }
        }
        fun botOf(b: RectBuilder) = b.apply {
            _y = { b._y(it) + b.h + offset }
        }
    }

    inner class XPercent(
        val percent: Float, val offset: Int = 0
    ) {
        private var _left: (Int)->Int = { 0 }
        private var _right: (Int)->Int = { it }
        fun left(l: Int) = this.apply {
            _left = { l }
        }
        fun right(r: Int) = this.apply {
            _right = { r }
        }
        fun left(l: Float) = this.apply {
            _left = { (l * it).toInt() }
        }
        fun right(r: Float) = this.apply {
            _right = { (r * it).toInt() }
        }
        fun left(l: RectBuilder) = this.apply {
            _left = { l._x(it) }
        }
        fun right(r: RectBuilder) = this.apply {
            _right = { r._x(it) + r.w }
        }
        fun apply() = b.apply {
            _x = {
                val s = _left(it)
                val e = _right(it)
                s + ((e - s) * percent).toInt() + offset
            }
        }
    }
    inner class YPercent(
        val percent: Float, val offset: Int = 0
    ) {
        private var _top: (Int)->Int = { 0 }
        private var _bot: (Int)->Int = { it }
        fun top(t: Int) = this.apply {
            _top = { t }
        }
        fun bot(b: Int) = this.apply {
            _bot = { b }
        }
        fun top(t: Float) = this.apply {
            _top = { (t * it).toInt() }
        }
        fun bot(b: Float) = this.apply {
            _bot = { (b * it).toInt() }
        }
        fun top(t: RectBuilder) = this.apply {
            _top = { t._y(it) }
        }
        fun bot(b: RectBuilder) = this.apply {
            _bot = { b._y(it) + b.h }
        }
        fun apply() = b.apply {
            _y = {
                val s = _top(it)
                val e = _bot(it)
                s + ((e - s) * percent).toInt() + offset
            }
        }
    }
}