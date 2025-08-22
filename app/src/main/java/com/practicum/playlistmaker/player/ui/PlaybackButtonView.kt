package com.practicum.playlistmaker.player.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.graphics.drawable.toBitmap
import com.practicum.playlistmaker.R

class PlaybackButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {
    private val playImageBitmap: Bitmap?
    private val pauseImageBitmap: Bitmap?
    private var playImageRect = RectF(0f, 0f, 0f, 0f)
    private var pauseImageRect = RectF(0f, 0f, 0f, 0f)
    private var isPlaying: Boolean = false
    private val bgPaint = Paint().apply {
        style = Paint.Style.FILL
        color = context.getColor(R.color.button_play)
    }

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.PlaybackButtonView,
            defStyleAttr,
            defStyleRes
        ).apply {
            try {
                playImageBitmap =
                    getDrawable(R.styleable.PlaybackButtonView_playButtonResId)?.toBitmap()
                pauseImageBitmap =
                    getDrawable(R.styleable.PlaybackButtonView_pauseButtonResId)?.toBitmap()
            } finally {
                recycle()
            }
        }
    }

    fun setPlayingState(currentlyPlaying: Boolean) {
        isPlaying = currentlyPlaying
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val centerX = measuredWidth / 2f
        val centerY = measuredHeight / 2f

        playImageRect = RectF(
            centerX - measuredWidth / 4.8f,
            centerY - measuredHeight / 4.5f,
            centerX + measuredWidth / 4.8f,
            centerY + measuredHeight / 4.5f
        )
        pauseImageRect = RectF(
            centerX - measuredWidth / 6.7f,
            centerY - measuredHeight / 5f,
            centerX + measuredWidth / 6.7f,
            centerY + measuredHeight / 5f
        )
    }

    override fun onDraw(canvas: Canvas) {

        canvas.drawCircle(measuredWidth / 2f, measuredHeight / 2f, measuredWidth / 2f, bgPaint)

        if (isPlaying) {
            pauseImageBitmap?.let {
                canvas.drawBitmap(pauseImageBitmap, null, pauseImageRect, null)
            }
        } else {
            playImageBitmap?.let {
                canvas.drawBitmap(playImageBitmap, null, playImageRect, null)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> return true
            MotionEvent.ACTION_UP -> {
                performClick()
                return true
            }
        }

        return super.onTouchEvent(event)
    }

    override fun performClick(): Boolean {
        setPlayingState(!isPlaying)

        return super.performClick()
    }
}