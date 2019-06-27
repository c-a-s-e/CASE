package com.example.pc.caseproject

import android.animation.ObjectAnimator
import android.animation.ValueAnimator.INFINITE
import android.animation.ValueAnimator.RESTART
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator


class PulseLoadingView : View {
    constructor(context: Context) : super(context) {
        setUpPaint()
        buildPath()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setUpPaint()
        buildPath()
    }

    private val path: Path = Path()
    private val paint: Paint = Paint()
    private val points = listOf(Pair(0.0f, 100.0f), Pair(400.0f, 100.0f), Pair(450.0f, 0.0f), Pair(500.0f, 200.0f), Pair(550.0f, 100.0f), Pair(700.0f, 100.0f))
    var length: Float = 0.0f

    fun buildPath() {
        path.moveTo(points.first().first, points.first().second)
        points.forEach { p -> path.lineTo(p.first, p.second) }
    }

    fun setUpPaint() {
        paint.color = Color.RED
        paint.strokeWidth = 12.0f
        paint.style = Paint.Style.STROKE
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeJoin = Paint.Join.ROUND
        paint.isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawPath(path, paint)
    }

    fun startAnimation() {
        val measure = PathMeasure(path, false)
        length = measure.length

        ObjectAnimator.ofFloat(this@PulseLoadingView, "phase", 1.0f, 0.0f).apply {
            duration=1000
            interpolator = LinearInterpolator()
            repeatCount = INFINITE
            repeatMode = RESTART
        }.start()
    }

    fun setPhase(phase: Float) {
        paint.pathEffect = createPathEffect(length, phase, 0.0f)
        invalidate()
    }

    private fun createPathEffect(pathLength: Float, phase: Float, offset: Float): PathEffect {
        return DashPathEffect(floatArrayOf(pathLength, pathLength), Math.max(phase * pathLength, offset))
    }
}