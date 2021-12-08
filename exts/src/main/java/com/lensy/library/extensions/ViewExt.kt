package com.lensy.library.extensions

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.graphics.PorterDuff
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.lensy.library.extensions.ViewConstants.FULL_OPACITY
import com.lensy.library.extensions.ViewConstants.HALF_OPACITY

/**
 * Developed by
 * @author Aleksandr Artemov
 */

const val ANIMATION_FAST_MILLIS = 50L
const val ANIMATION_SLOW_MILLIS = 100L
const val TOUCH_IS_CLICK = 300L

const val PREVENT_TOUCH_TIME = 300L

inline fun View.setTouchListeners(crossinline onDown: () -> Unit, crossinline onUp: () -> Unit) {
    var touchDownTime = 0L
    setOnTouchListener { view, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchDownTime = System.currentTimeMillis()
                onDown()
            }
            MotionEvent.ACTION_UP -> {
                onUp()
                if (System.currentTimeMillis() - touchDownTime > TOUCH_IS_CLICK) {
                    view.performClick()
                }
            }
        }
        true
    }
}


/**
 * Simulate a button click, including a small delay while it is being pressed to trigger the
 * appropriate animations.
 */
@SuppressLint("ClickableViewAccessibility")
fun View.simulateClick(showPress: Boolean = false, delay: Long = ANIMATION_FAST_MILLIS) {
    if (isEnabled) {
        performClick()
        if (showPress) {
            isPressed = true
            invalidate()
            postDelayed({
                isPressed = false
                invalidate()
            }, delay)
        }
    }
}

@SuppressLint("ClickableViewAccessibility")
fun View.preventSecondTouch(delay: Long = PREVENT_TOUCH_TIME) {
    isClickable = false
    postDelayed({ isClickable = true }, delay)
}

fun ImageView.setSvgColor(@ColorInt color: Int) =
    setColorFilter(color, PorterDuff.Mode.SRC_IN)

@ColorInt
fun getContrastColor(
    @ColorInt background: Int,
    @ColorInt first: Int,
    @ColorInt second: Int
): Int =
    if (ColorUtils.calculateContrast(first, background) > ColorUtils.calculateContrast(second, background))
        first
    else
        second

fun View.setBackgroundDrawableColor(@ColorInt color: Int) =
    DrawableCompat.setTint(
        DrawableCompat.wrap(background).mutate(),
        color
    )

fun ObjectAnimator.disableViewDuringAnimation(view: View) {
    addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationStart(animation: Animator?) {
            view.isEnabled = false
        }

        override fun onAnimationEnd(animation: Animator?) {
            view.isEnabled = true
        }
    })
}

val MotionEvent.isMultiTouch
    get() = pointerCount != 1

val MotionEvent.isSingleTouch
    get() = pointerCount == 1

var View.visible: Boolean
    get() = visibility == View.VISIBLE
    set(value) {
        visibility = if (value) View.VISIBLE else View.INVISIBLE
    }

var View.visibleGone: Boolean
    get() = visibility == View.VISIBLE
    set(value) {
        visibility = if (value) View.VISIBLE else View.GONE
    }

const val RIGHT_DIRECTION: Int = 1
const val LEFT_DIRECTION: Int = -1

fun RecyclerView.addLeftAndRightArrows(leftArrow: ImageView, rightArrow: ImageView) {
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val canScrollToLeft = recyclerView.canScrollHorizontally(LEFT_DIRECTION)
            val canScrollToRight = recyclerView.canScrollHorizontally(RIGHT_DIRECTION)

            leftArrow.post {
                leftArrow.visibility = if (canScrollToLeft) View.VISIBLE else View.INVISIBLE
            }
            rightArrow.post {
                rightArrow.visibility = if (canScrollToRight) View.VISIBLE else View.INVISIBLE
            }
        }
    })
}

internal object ViewConstants {
    const val HALF_OPACITY = 0.5f
    const val PARTIAL_OPACITY = 0.7f
    const val FULL_OPACITY = 1f
}

fun View.setAlphaEnabled(enabled: Boolean) {
    isEnabled = enabled
    alpha = if (enabled) FULL_OPACITY else HALF_OPACITY
}