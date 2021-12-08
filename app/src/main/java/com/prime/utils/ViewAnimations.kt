package com.prime.utils

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.OvershootInterpolator

/**
 * Developed by
 * @author Aleksandr Artemov
 */
object ViewAnimations {

    fun fadeOut(view: View, duration: Long, showIfHidden: Boolean = false): Animation? {
        if (showIfHidden.not() && (view.visibility in arrayOf(View.INVISIBLE, View.GONE))) return null

        view.visibility = View.VISIBLE

        return alpha(view, duration, 1f, 0f, object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) = Unit

            override fun onAnimationRepeat(animation: Animation) = Unit

            override fun onAnimationEnd(animation: Animation) {
                view.visibility = View.INVISIBLE
            }
        })
    }

    fun fadeIn(view: View, duration: Long, delay: Long = 0): Animation? {
        if (view.visibility == View.VISIBLE) return null

        view.visibility = View.INVISIBLE
        return alpha(view, duration, 0f, 1f, object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) = Unit

            override fun onAnimationRepeat(animation: Animation) = Unit

            override fun onAnimationEnd(animation: Animation) {
                view.visibility = View.VISIBLE
            }
        })
    }

    private fun alpha(
        view: View,
        animDuration: Long,
        fromAlpha: Float,
        toAlpha: Float,
        listener: Animation.AnimationListener
    ) = AlphaAnimation(fromAlpha, toAlpha)
        .apply {
            duration = animDuration
            setAnimationListener(listener)
        }.also {
            view.startAnimation(it)
        }

    fun makeAlphaAnim(view: View?, animDuration: Long, from: Float, to: Float): ObjectAnimator =
        ObjectAnimator.ofFloat(view, View.ALPHA, from, to).apply {
            duration = animDuration
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            start()
        }

    fun makeBounceAnim(view: View?, animDuration: Long, from: Float, to: Float): ObjectAnimator =
        ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, from, to).apply {
            duration = animDuration
            interpolator = OvershootInterpolator()
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            start()
        }

    fun stopAnim(animator: ObjectAnimator?) = animator?.cancel()

    fun stopAnim(anim: Animation?) = anim?.cancel()
}