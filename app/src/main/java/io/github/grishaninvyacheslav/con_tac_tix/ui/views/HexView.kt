package io.github.grishaninvyacheslav.con_tac_tix.ui.views

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import java.util.*
import kotlin.math.sqrt

class HexView : View {
    private var radius = 0f
    private var centerX = 0f
    private var centerY = 0f
    private val collisionEdges = arrayListOf<Triple<Float, Float, Float>>()
    private var bottomBorderPath = Path()
    private val bottomBorderPaint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.FILL
    }
    private var bottomLeftBorderPath = Path()
    private val bottomLeftBorderPaint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.FILL
    }
    private var topLeftBorderPath = Path()
    private val topLeftBorderPaint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.FILL
    }
    private var topBorderPath = Path()
    private val topBorderPaint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.FILL
    }
    private var topRightBorderPath = Path()
    private val topRightBorderPaint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.FILL
    }
    private var bottomRightBorderPath = Path()
    private val bottomRightBorderPaint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.FILL
    }
    private var hexagonPath = Path()
    private val hexagonPaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.FILL
    }
    private var animator: ValueAnimator? = null
    private var innerColor = Color.WHITE
    private var borderColors =
        listOf(Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK)

    private fun isPointsAreOnSameSideOfLine(
        a: Float, b: Float,
        c: Float, x1: Float,
        y1: Float, x2: Float,
        y2: Float
    ): Boolean {
        val fx1 = a * x1 + b * y1 - c
        val fx2 = a * x2 + b * y2 - c
        return (fx1 > 0 && fx2 > 0) || (fx1 < 0 && fx2 < 0)
    }

    private fun getLineParams(x1: Float, y1: Float, x2: Float, y2: Float) =
        Triple(y2 - y1, x1 - x2, x1 * y2 - x2 * y1)

    private fun setBorderColors(colors: List<Int>) {
        topBorderPaint.color = colors[0]
        topRightBorderPaint.color = colors[1]
        bottomRightBorderPaint.color = colors[2]
        bottomBorderPaint.color = colors[3]
        bottomLeftBorderPaint.color = colors[4]
        topLeftBorderPaint.color = colors[5]
        invalidate()
    }

    var borderRadius = 8

    fun startAnimation(startDelay: Long) {
        animator?.cancel()
        animator = ValueAnimator.ofInt(0, 5).apply {
            duration = 10
            interpolator = LinearInterpolator()
            addListener(object : Animator.AnimatorListener{
                private var borderColorsRotationCount = 0
                private var innerColorShift = 5
                private var innerColorShiftInc = 1
                private var isCanceled = false

                override fun onAnimationStart(animation: Animator?) {
                    isCanceled = false
                    onAnimationRepeat(animation)
                }

                override fun onAnimationEnd(animation: Animator?) {
                    if(isCanceled){
                        return
                    }
                    animator?.startDelay = 10
                    animator?.start()
                }

                override fun onAnimationCancel(animation: Animator?) {
                    Log.d("[ANIM]", "CANCELED")
                    Collections.rotate(borderColors, - borderColorsRotationCount)
                    borderColorsRotationCount = 0
                    hexagonPaint.color = innerColor
                    setBorderColors(borderColors)
                    invalidate()
                    isCanceled = true
                }

                override fun onAnimationRepeat(animation: Animator?) {
                    Collections.rotate(borderColors, 1)
                    borderColorsRotationCount = (borderColorsRotationCount + 1) % 6
                    if(borderColorsRotationCount % 6 == 0){
                        when(innerColorShift){
                            0 ->
                                hexagonPaint.color = borderColors[5]
                            1 ->
                                hexagonPaint.color = borderColors[0]
                            2 ->
                                hexagonPaint.color = borderColors[4]
                            3 ->
                                hexagonPaint.color = borderColors[1]
                            4 ->
                                hexagonPaint.color = borderColors[3]
                            5 ->
                                hexagonPaint.color = borderColors[2]
                        }
                        innerColorShift += innerColorShiftInc
                        if(innerColorShift > 5 || innerColorShift < 0){
                            innerColorShiftInc *= -1
                            innerColorShift += innerColorShiftInc
                        }
                    }
                    setBorderColors(borderColors)
                    invalidate()
                }
            })
        }
        animator?.startDelay = startDelay
        animator?.start()
    }

    fun cancelAnimation() {
        animator?.cancel()
    }

    fun setColor(color: Int, borderColors: List<Int>) {
        innerColor = color
        hexagonPaint.color = color
        this.borderColors = borderColors
        setBorderColors(borderColors)
        invalidate()
    }

    fun setRadius(borderRadius: Int) {
        this.borderRadius = borderRadius
        calculatePath()
    }

    constructor(context: Context?, borderRadius: Int, borderColors: List<Int>) : this(
        context,
        null
    ) {
        this.borderRadius = borderRadius
        this.borderColors = borderColors
        setBorderColors(borderColors)
    }

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private fun calculatePath() {
        val triangleHeight = (sqrt(3.0) * radius / 2f).toFloat()
        collisionEdges.clear()

        var x1 = centerX - radius / 2f
        var y1 = centerY - triangleHeight
        var x2 = centerX + radius / 2f
        var y2 = centerY - triangleHeight
        topBorderPath.moveTo(centerX, centerY)
        topBorderPath.lineTo(x1, y1)
        topBorderPath.lineTo(x2, y2)
        topBorderPath.moveTo(centerX, centerY)
        collisionEdges.add(getLineParams(x1, y1, x2, y2))

        x1 = centerX + radius / 2f
        y1 = centerY - triangleHeight
        x2 = centerX + radius
        y2 = centerY
        topRightBorderPath.moveTo(centerX, centerY)
        topRightBorderPath.lineTo(x1, y1)
        topRightBorderPath.lineTo(x2, y2)
        topRightBorderPath.moveTo(centerX, centerY)
        collisionEdges.add(getLineParams(x1, y1, x2, y2))

        x1 = centerX + radius
        y1 = centerY
        x2 = centerX + radius / 2f
        y2 = centerY + triangleHeight
        bottomRightBorderPath.moveTo(centerX, centerY)
        bottomRightBorderPath.lineTo(x1, y1)
        bottomRightBorderPath.lineTo(x2, y2)
        bottomRightBorderPath.moveTo(centerX, centerY)
        collisionEdges.add(getLineParams(x1, y1, x2, y2))

        x1 = centerX + radius / 2f
        y1 = centerY + triangleHeight
        x2 = centerX - radius / 2f
        y2 = centerY + triangleHeight
        bottomBorderPath.moveTo(centerX, centerY)
        bottomBorderPath.lineTo(x1, y1)
        bottomBorderPath.lineTo(x2, y2)
        bottomBorderPath.moveTo(centerX, centerY)
        collisionEdges.add(getLineParams(x1, y1, x2, y2))

        x1 = centerX - radius / 2f
        y1 = centerY + triangleHeight
        x2 = centerX - radius
        y2 = centerY
        bottomLeftBorderPath.moveTo(centerX, centerY)
        bottomLeftBorderPath.lineTo(x1, y1)
        bottomLeftBorderPath.lineTo(x2, y2)
        bottomLeftBorderPath.moveTo(centerX, centerY)
        collisionEdges.add(getLineParams(x1, y1, x2, y2))

        x1 = centerX - radius
        y1 = centerY
        x2 = centerX - radius / 2f
        y2 = centerY - triangleHeight
        topLeftBorderPath.moveTo(centerX, centerY)
        topLeftBorderPath.lineTo(x1, y1)
        topLeftBorderPath.lineTo(x2, y2)
        topLeftBorderPath.moveTo(centerX, centerY)
        collisionEdges.add(getLineParams(x1, y1, x2, y2))


        hexagonPath = Path()
        Log.d("[RADIUS]", "borderRadius: $borderRadius")
        val innerRadius = radius - borderRadius
        val innerTriangleHeight = (sqrt(3.0) * innerRadius / 2).toFloat()
        hexagonPath.moveTo(centerX + innerRadius / 2, centerY - innerTriangleHeight)
        hexagonPath.lineTo(centerX + innerRadius, centerY)
        hexagonPath.lineTo(centerX + innerRadius / 2, centerY + innerTriangleHeight)
        hexagonPath.lineTo(centerX - innerRadius / 2, centerY + innerTriangleHeight)
        hexagonPath.lineTo(centerX - innerRadius, centerY)
        hexagonPath.lineTo(centerX - innerRadius / 2, centerY - innerTriangleHeight)
        hexagonPath.moveTo(centerX + innerRadius / 2, centerY - innerTriangleHeight)
        invalidate()
    }

    override fun onDraw(c: Canvas) {
        super.onDraw(c)
        c.drawPath(bottomBorderPath, bottomBorderPaint)
        c.drawPath(bottomLeftBorderPath, bottomLeftBorderPaint)
        c.drawPath(topLeftBorderPath, topLeftBorderPaint)
        c.drawPath(topBorderPath, topBorderPaint)
        c.drawPath(topRightBorderPath, topRightBorderPaint)
        c.drawPath(bottomRightBorderPath, bottomRightBorderPaint)
        c.drawPath(hexagonPath, hexagonPaint)
        c.save()
    }

    public override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        centerX = MeasureSpec.getSize(widthMeasureSpec).toFloat() / 2f
        centerY = MeasureSpec.getSize(heightMeasureSpec).toFloat() / 2f
        if (radius == 0f) {
            radius = centerX
        }
        calculatePath()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val x = event!!.x
        val y = event.y
        for (edge in collisionEdges) {
            if (!isPointsAreOnSameSideOfLine(
                    edge.first,
                    edge.second,
                    edge.third,
                    centerX,
                    centerY,
                    x,
                    y
                )
            ) {
                return true
            }
        }
        return super.onTouchEvent(event)
    }
}