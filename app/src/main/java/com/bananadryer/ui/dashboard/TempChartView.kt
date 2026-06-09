package com.bananadryer.ui.dashboard

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

class TempChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val dataPoints = ArrayDeque<Float>()
    private val maxPoints = 20

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#A371F7")
        strokeWidth = 3f
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }

    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#33A371F7")
        style = Paint.Style.FILL
    }

    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#21262D")
        strokeWidth = 1f
        style = Paint.Style.STROKE
    }

    private val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#A371F7")
        style = Paint.Style.FILL
    }

    private val linePath = Path()
    private val fillPath = Path()

    fun addPoint(temp: Float) {
        if (dataPoints.size >= maxPoints) dataPoints.removeFirst()
        dataPoints.addLast(temp)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (dataPoints.size < 2) return

        val w = width.toFloat()
        val h = height.toFloat()
        val padding = 4f

        val minVal = (dataPoints.min() - 2f).coerceAtLeast(0f)
        val maxVal = dataPoints.max() + 2f
        val range = maxVal - minVal

        // Grid lines
        for (i in 0..2) {
            val y = padding + (h - padding * 2) * i / 2f
            canvas.drawLine(0f, y, w, y, gridPaint)
        }

        if (range == 0f) return

        val stepX = if (dataPoints.size > 1) w / (dataPoints.size - 1).toFloat() else w

        linePath.reset()
        fillPath.reset()

        dataPoints.forEachIndexed { index, value ->
            val x = index * stepX
            val y = padding + (h - padding * 2) * (1f - (value - minVal) / range)
            if (index == 0) {
                linePath.moveTo(x, y)
                fillPath.moveTo(x, h)
                fillPath.lineTo(x, y)
            } else {
                linePath.lineTo(x, y)
                fillPath.lineTo(x, y)
            }
        }

        val lastX = (dataPoints.size - 1) * stepX
        fillPath.lineTo(lastX, h)
        fillPath.close()

        canvas.drawPath(fillPath, fillPaint)
        canvas.drawPath(linePath, linePaint)

        val lastY = padding + (h - padding * 2) * (1f - (dataPoints.last() - minVal) / range)
        canvas.drawCircle(lastX, lastY, 5f, dotPaint)
    }
}