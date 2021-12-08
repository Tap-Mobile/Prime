package com.prime.features.main

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.TypedValue
import androidx.core.content.ContextCompat
import com.vpn.prime.R

/**
 * Developed by
 * @author Aleksandr Artemov
 */
class BubbleDrawable(
    private val context: Context,
) : Drawable() {

    companion object {
        private fun dpToPixels(context: Context, dipValue: Float) =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, context.resources.displayMetrics)
    }

    enum class PointerAnchor {
        TOP,
        BOTTOM
    }

    private var mColor = ContextCompat.getColor(context, R.color.primeColorPrimary)

    private val mPaint = Paint().apply {
        isAntiAlias = true
        color = mColor
    }
    private lateinit var mPointer: Path
    private lateinit var mBoxRect: RectF

    private var mBoxWidth = 0
    private var mBoxHeight = 0
    private var mCornerRad = 0f
    private val mBoxPadding = Rect()
    private var mPointerWidth = 0
    private var mPointerHeight = 0

    var anchor: PointerAnchor = PointerAnchor.BOTTOM

    init {
        setPointerWidth(12f)
        setPointerHeight(6f)
    }

    fun setPadding(left: Float, top: Float, right: Float, bottom: Float) {
        mBoxPadding.left = dpToPixels(context, left).toInt()
        mBoxPadding.top = dpToPixels(context, top).toInt()
        mBoxPadding.right = dpToPixels(context, right).toInt()
        mBoxPadding.bottom = dpToPixels(context, bottom).toInt()
    }

    fun setCornerRadius(cornerRad: Float) {
        mCornerRad = dpToPixels(context, cornerRad)
    }

    fun setPointerWidth(pointerWidth: Float) {
        mPointerWidth = dpToPixels(context, pointerWidth).toInt()
    }

    fun setPointerHeight(pointerHeight: Float) {
        mPointerHeight = dpToPixels(context, pointerHeight).toInt()
    }

    private fun updatePointerPath() {
        mPointer = Path().apply {
            fillType = Path.FillType.EVEN_ODD

            val halfBoxWidth = (mBoxWidth / 2).toFloat()
            val halfPointerWidth = (mPointerWidth / 2).toFloat()

            when (anchor) {
                PointerAnchor.TOP -> {
                    moveTo(halfBoxWidth, 0f)
                    lineTo(halfBoxWidth + halfPointerWidth, mPointerHeight.toFloat() + 1)
                    lineTo(halfBoxWidth - halfPointerWidth, mPointerHeight.toFloat() + 1)
                    lineTo(halfBoxWidth, 0f)
                }
                PointerAnchor.BOTTOM -> {
                    moveTo(halfBoxWidth - halfPointerWidth, mBoxHeight.toFloat() - 1)
                    lineTo(halfBoxWidth + halfPointerWidth, mBoxHeight.toFloat() - 1)
                    lineTo(halfBoxWidth, (mBoxHeight + mPointerHeight).toFloat())
                    lineTo(halfBoxWidth - halfPointerWidth, mBoxHeight.toFloat() - 1)
                }
            }

            close()
        }
    }

    override fun draw(canvas: Canvas) {
        mBoxRect = when (anchor) {
            PointerAnchor.TOP -> RectF(0f, mPointerHeight.toFloat(), mBoxWidth.toFloat(), (mPointerHeight + mBoxHeight).toFloat())
            PointerAnchor.BOTTOM -> RectF(0f, 0f, mBoxWidth.toFloat(), mBoxHeight.toFloat())
        }
        canvas.drawRoundRect(mBoxRect, mCornerRad, mCornerRad, mPaint)
        updatePointerPath()
        canvas.drawPath(mPointer, mPaint)
    }

    override fun getOpacity() = PixelFormat.OPAQUE

    override fun setAlpha(alpha: Int) {}

    override fun setColorFilter(cf: ColorFilter?) {}

    override fun getPadding(padding: Rect): Boolean {
        padding.set(mBoxPadding)

        // Adjust the padding to include the height of the pointer
        when (anchor) {
            PointerAnchor.TOP -> {
                padding.top += mPointerHeight
            }
            PointerAnchor.BOTTOM -> {
                padding.bottom += mPointerHeight;
            }
        }
        return true
    }

    override fun onBoundsChange(bounds: Rect) {
        mBoxWidth = bounds.width()
        mBoxHeight = getBounds().height() - mPointerHeight
        super.onBoundsChange(bounds)
    }
}