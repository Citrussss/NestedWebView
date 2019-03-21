package com.union.bangbang.nestedwebview

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.support.v4.view.NestedScrollingChild
import android.support.v4.view.NestedScrollingChildHelper
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.webkit.WebView

/**
 * @name NestedWebView
 * @anthor bangbang QQ:740090077
 * @time 2019/3/21 9:42 AM
 * 只有编译器可能不骗你。
 */
class NestedWebView : WebView, NestedScrollingChild {
    private val canScrollVertically = true

    private var mScrollPointerId: Int = 0

    private var touchX = 0F
    private var touchY = 0F

    private val isScrollToBottom: Boolean
        get() = (contentHeight * scale - (height + scrollY)).toInt() == 0


    private var mScrollingChildHelper: NestedScrollingChildHelper? = null
    //延迟属性，实现双重校验式
    private val scrollingChildHelper: NestedScrollingChildHelper by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
        NestedScrollingChildHelper(this)
    }
    private val mVelocityTracker: VelocityTracker by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
        VelocityTracker.obtain()
    }
//        get() {
//            return this.mScrollingChildHelper ?: synchronized(this) {
//                return this.mScrollingChildHelper = mScrollingChildHelper ?: NestedScrollingChildHelper(this)
//            }
//        }

    /**
     * Construct a new WebView with a Context object.
     *
     * @param context A Context object used to access application assets.
     */
    constructor(context: Context) : super(context) {}

    /**
     * Construct a new WebView with layout parameters.
     *
     * @param context A Context object used to access application assets.
     * @param attrs   An AttributeSet passed to our parent.
     */
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    /**
     * Construct a new WebView with layout parameters and a default style.
     *
     * @param context      A Context object used to access application assets.
     * @param attrs        An AttributeSet passed to our parent.
     * @param defStyleAttr
     */
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    )

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchX = event.x + 0.5f
                touchY = event.y + 0.5f
                this.scrollingChildHelper.isNestedScrollingEnabled = true
                this.scrollingChildHelper.startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL)
            }
            MotionEvent.ACTION_MOVE -> {
                this.mScrollPointerId = event.getPointerId(0)
                this.mVelocityTracker.addMovement(event)
                val x = event.x + 0.5f
                val y = event.y + 0.5f
                val dx = (touchX - x).toInt()
                val dy = (touchY - y).toInt()
                this.scrollingChildHelper.dispatchNestedPreScroll(dx, dy, intArrayOf(0, 0), intArrayOf(0, 0))
            }
            MotionEvent.ACTION_UP -> {
                this.mVelocityTracker.computeCurrentVelocity(1000)
                val xvel = 0f
                val yvel = if (canScrollVertically) -this.mVelocityTracker.getYVelocity(this.mScrollPointerId) else 0.0f
                this.fling(xvel.toInt(), yvel.toInt())
                mVelocityTracker.recycle()
                this.scrollingChildHelper.stopNestedScroll()
            }
            MotionEvent.ACTION_POINTER_UP -> this.scrollingChildHelper.stopNestedScroll()
        }
        return super.onTouchEvent(event)
    }

    private fun fling(xvel: Int, yvel: Int) {
        if (!dispatchNestedPreFling(xvel.toFloat(), yvel.toFloat())) {
            dispatchNestedFling(xvel.toFloat(), yvel.toFloat(), false)
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        return super.dispatchTouchEvent(ev)
    }

    override fun setNestedScrollingEnabled(enabled: Boolean) {
        this.scrollingChildHelper.isNestedScrollingEnabled = enabled
    }

    override fun isNestedScrollingEnabled(): Boolean {
        return this.scrollingChildHelper.isNestedScrollingEnabled
    }

    override fun startNestedScroll(axes: Int): Boolean {
        return this.scrollingChildHelper.startNestedScroll(axes)
    }

    fun startNestedScroll(axes: Int, type: Int): Boolean {
        return this.scrollingChildHelper.startNestedScroll(axes, type)
    }

    override fun stopNestedScroll() {
        this.scrollingChildHelper.stopNestedScroll()
    }

    fun stopNestedScroll(type: Int) {
        this.scrollingChildHelper.stopNestedScroll(type)
    }

    override fun hasNestedScrollingParent(): Boolean {
        return this.scrollingChildHelper.hasNestedScrollingParent()
    }

    fun hasNestedScrollingParent(type: Int): Boolean {
        return this.scrollingChildHelper.hasNestedScrollingParent(type)
    }

    override fun dispatchNestedScroll(
        dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int,
        dyUnconsumed: Int, offsetInWindow: IntArray?
    ): Boolean {
        return this.scrollingChildHelper.dispatchNestedScroll(
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            offsetInWindow
        )
    }

    fun dispatchNestedScroll(
        dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int,
        dyUnconsumed: Int, offsetInWindow: IntArray, type: Int
    ): Boolean {
        return this.scrollingChildHelper.dispatchNestedScroll(
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            offsetInWindow,
            type
        )
    }

    override fun dispatchNestedPreScroll(
        dx: Int, dy: Int, consumed: IntArray?,
        offsetInWindow: IntArray?
    ): Boolean {
        return this.scrollingChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow)
    }

    fun dispatchNestedPreScroll(
        dx: Int, dy: Int, consumed: IntArray,
        offsetInWindow: IntArray, type: Int
    ): Boolean {
        return this.scrollingChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type)
    }

    override fun dispatchNestedFling(velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {
        return this.scrollingChildHelper.dispatchNestedFling(velocityX, velocityY, consumed)
    }

    override fun dispatchNestedPreFling(velocityX: Float, velocityY: Float): Boolean {
        return this.scrollingChildHelper.dispatchNestedPreFling(velocityX, velocityY)
    }

    override fun flingScroll(vx: Int, vy: Int) {
        super.flingScroll(vx, vy)
    }
}

