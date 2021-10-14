package io.github.grishaninvyacheslav.con_tac_tix.ui.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import io.github.grishaninvyacheslav.con_tac_tix.App
import io.github.grishaninvyacheslav.con_tac_tix.R
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class HexFieldView : FrameLayout {
    private var fieldLeftBorderPath = Path()
    private var fieldRightBorderPath = Path()
    var fieldBorderRadius = 18
    private val fieldVerticalBorderPaint = Paint().apply {
        color = Color.RED
        strokeWidth = (fieldBorderRadius * 2).toFloat()
        style = Paint.Style.STROKE
    }
    private var fieldTopBorderPath = Path()
    private var fieldBottomBorderPath = Path()
    private val fieldHorizontalBorderPaint = Paint().apply {
        color = Color.BLUE
        strokeWidth = (fieldBorderRadius * 2).toFloat()
        style = Paint.Style.STROKE
    }
    private var fieldTopLeftBorderConnectorPath = Path()
    private var fieldTopRightBorderConnectorPath = Path()
    private var fieldBottomLeftBorderConnectorPath = Path()
    private var fieldBottomRightBorderConnectorPath = Path()
    private val fieldHorizontalBorderConnectorPaint = Paint().apply {
        color = Color.BLUE
        style = Paint.Style.FILL
    }
    private var fieldLeftTopBorderConnectorPath = Path()
    private var fieldRightTopBorderConnectorPath = Path()
    private var fieldLeftBottomBorderConnectorPath = Path()
    private var fieldRightBottomBorderConnectorPath = Path()
    private val fieldVerticalBorderConnectorPaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.FILL
    }
    var borderRadius = 9
    private val gridSize = 11
    private var cells = hashMapOf<Pair<Int, Int>, HexView>().apply {
        for (i in 0 until gridSize) {
            for (j in 0 until gridSize) {
                this[Pair(j, i)] = HexView(
                    context,
                    borderRadius = borderRadius,
                    borderColors = listOf(App.instance.getColor(R.color.light_gray), App.instance.getColor(R.color.light_gray), App.instance.getColor(R.color.light_gray), App.instance.getColor(R.color.light_gray), App.instance.getColor(R.color.light_gray), App.instance.getColor(R.color.light_gray))
                ).apply {
                    setOnClickListener { view ->
                        if (view is HexView) {
                            onCellClickListener?.let { it(view, Pair(j, i)) }
                        }
                    }
                }
            }
        }
    }
    private val backgroundSpace = View(context).apply {
        setOnClickListener{
            onBackgroundClickListener?.let { it() }
        }
    }

    var onCellClickListener: ((HexView, Pair<Int, Int>) -> Unit)? = null
    var onBackgroundClickListener: (() -> Unit)? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        this.setWillNotDraw(false)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        removeAllViews()
        val params = LayoutParams(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec))
        addView(backgroundSpace, params)

        // https://www.wolframalpha.com/input/?i=solve+%28x%2F2+%2B+x%2F2*c%29*n+%2B+%28x+-+%28x%2F2+%2B+x%2F2*c%29%29+%3D+w+for+x
        var hexDiameter =
            ((2 * MeasureSpec.getSize(widthMeasureSpec) - 4 * fieldBorderRadius) / (cos(60 * PI / 180) * (gridSize - 1) + gridSize + 1)).toInt()
        var hexHeight = (((hexDiameter / 2) * sqrt(3.0)) / 2).toInt()
        var hexWidth = (hexDiameter / 2 + hexDiameter / 2 * cos(60 * (PI / 180))).toInt()
        var diff =
            MeasureSpec.getSize(widthMeasureSpec) - (hexWidth * gridSize + (hexDiameter - hexWidth))
        Log.d("[MYLOG]", "diff: $diff")
        var initialHexMarginTop =
            MeasureSpec.getSize(heightMeasureSpec) - (hexHeight * 2 + (hexDiameter / 2 - hexHeight) + fieldBorderRadius)
        val borderLeftShift = hexDiameter - hexWidth
        fieldLeftBorderPath = Path()
        fieldTopBorderPath = Path()
        fieldLeftTopBorderConnectorPath = Path()
        fieldTopLeftBorderConnectorPath = Path()
        fieldTopRightBorderConnectorPath = Path()
        fieldRightBorderPath = Path()
        fieldRightTopBorderConnectorPath = Path()
        fieldBottomBorderPath = Path()
        fieldLeftBottomBorderConnectorPath = Path()
        fieldBottomLeftBorderConnectorPath = Path()
        fieldBottomRightBorderConnectorPath = Path()
        fieldRightBottomBorderConnectorPath = Path()
        for (i in 0 until gridSize) {
            for (j in 0 until gridSize) {
                val hexView = cells[Pair(j, i)]
                val params = LayoutParams(hexDiameter, hexDiameter)
                val firstHexMarginTop =
                    initialHexMarginTop - (hexHeight) * i
                params.leftMargin = (i * (hexWidth) + diff / 2)
                params.topMargin = (firstHexMarginTop - (hexHeight) * 2 * j)
                addView(hexView, params)
                if (i == 0 && j == 0) {
                    val bottomLeftCornerX = params.leftMargin.toFloat() + borderLeftShift
                    val bottomLeftCornerY =
                        (params.topMargin + (hexHeight * 2 + (hexDiameter / 2 - hexHeight))).toFloat()
                    fieldLeftBorderPath.moveTo(bottomLeftCornerX, bottomLeftCornerY)
                    fieldBottomBorderPath.moveTo(bottomLeftCornerX, bottomLeftCornerY)
                    val cornerX =
                        (bottomLeftCornerX - ((fieldHorizontalBorderPaint.strokeWidth / 2) / cos(30 * PI / 180)) * sin(
                            30 * PI / 180
                        )).toFloat()
                    val cornerY = bottomLeftCornerY + fieldHorizontalBorderPaint.strokeWidth / 2
                    fieldLeftBottomBorderConnectorPath.moveTo(bottomLeftCornerX, bottomLeftCornerY)
                    fieldLeftBottomBorderConnectorPath.lineTo(
                        cornerX,
                        cornerY
                    )
                    fieldLeftBottomBorderConnectorPath.lineTo(
                        (cornerX - ((fieldHorizontalBorderPaint.strokeWidth / 2) / cos(30 * PI / 180)) * cos(
                            30 * PI / 180
                        ) * cos(30 * PI / 180)).toFloat(),
                        (bottomLeftCornerY - ((fieldHorizontalBorderPaint.strokeWidth / 2) / cos(30 * PI / 180)) * cos(
                            30 * PI / 180
                        ) * sin(30 * PI / 180)).toFloat()
                    )

                    fieldBottomLeftBorderConnectorPath.moveTo(bottomLeftCornerX, bottomLeftCornerY)
                    fieldBottomLeftBorderConnectorPath.lineTo(bottomLeftCornerX, bottomLeftCornerY + fieldHorizontalBorderPaint.strokeWidth / 2)
                    fieldBottomLeftBorderConnectorPath.lineTo(cornerX, cornerY)
                }
                if (i == 0) {
                    fieldLeftBorderPath.lineTo(
                        params.leftMargin.toFloat(),
                        (params.topMargin + hexHeight + (hexDiameter / 2 - hexHeight)).toFloat()
                    )
                    fieldLeftBorderPath.lineTo(
                        params.leftMargin.toFloat() + borderLeftShift,
                        (params.topMargin + (hexDiameter / 2 - hexHeight)).toFloat()
                    )
                }
                if (i == 0 && j == gridSize - 1) {
                    val startX = params.leftMargin.toFloat() + borderLeftShift
                    val startY = (params.topMargin + (hexDiameter / 2 - hexHeight)).toFloat()
                    fieldTopBorderPath.moveTo(startX, startY)
                    fieldTopBorderPath.lineTo(
                        params.leftMargin.toFloat() + (hexDiameter - borderLeftShift),
                        (params.topMargin + (hexDiameter / 2 - hexHeight)).toFloat()
                    )

                    val cornerX =
                        (startX - ((fieldHorizontalBorderPaint.strokeWidth / 2) / cos(30 * PI / 180)) * sin(
                            30 * PI / 180
                        )).toFloat()
                    val cornerY = startY - fieldHorizontalBorderPaint.strokeWidth / 2
                    fieldTopLeftBorderConnectorPath.moveTo(startX, startY)
                    fieldTopLeftBorderConnectorPath.lineTo(
                        startX,
                        startY - fieldHorizontalBorderPaint.strokeWidth / 2
                    )
                    fieldTopLeftBorderConnectorPath.lineTo(cornerX, cornerY)

                    fieldLeftTopBorderConnectorPath.moveTo(startX, startY)
                    fieldLeftTopBorderConnectorPath.lineTo(cornerX, cornerY)
                    fieldLeftTopBorderConnectorPath.lineTo(
                        (cornerX - ((fieldHorizontalBorderPaint.strokeWidth / 2) / cos(30 * PI / 180)) * cos(
                            30 * PI / 180
                        ) * cos(30 * PI / 180)).toFloat(),
                        (startY + ((fieldHorizontalBorderPaint.strokeWidth / 2) / cos(30 * PI / 180)) * cos(
                            30 * PI / 180
                        ) * sin(30 * PI / 180)).toFloat()
                    )

                } else if (j == gridSize - 1) {
                    fieldTopBorderPath.lineTo(
                        params.leftMargin.toFloat() + borderLeftShift,
                        (params.topMargin + (hexDiameter / 2 - hexHeight)).toFloat()
                    )
                    fieldTopBorderPath.lineTo(
                        params.leftMargin.toFloat() + (hexDiameter - borderLeftShift),
                        (params.topMargin + (hexDiameter / 2 - hexHeight)).toFloat()
                    )
                }
                if (j == 0) {
                    fieldBottomBorderPath.lineTo(
                        (params.leftMargin + hexWidth).toFloat(),
                        (params.topMargin + hexDiameter / 2 + hexHeight).toFloat()
                    )
                    if (i != gridSize - 1) {
                        fieldBottomBorderPath.lineTo(
                            (params.leftMargin + hexDiameter).toFloat(),
                            (params.topMargin + hexDiameter / 2).toFloat()
                        )
                    }
                }
                if (i == gridSize - 1 && j == gridSize - 1) {
                    val topRightInnerCornerX = (params.leftMargin + hexWidth).toFloat()
                    val topRightInnerCornerY =
                        (params.topMargin + (hexDiameter / 2 - hexHeight)).toFloat()
                    val topRightOuterCornerX =
                        (topRightInnerCornerX + ((fieldHorizontalBorderPaint.strokeWidth / 2) / cos(
                            30 * PI / 180
                        )) * cos(30 * PI / 180) * sin(30 * PI / 180)).toFloat()
                    val topRightOuterCornerY =
                        topRightInnerCornerY - fieldHorizontalBorderPaint.strokeWidth / 2
                    fieldTopRightBorderConnectorPath.moveTo(
                        topRightInnerCornerX,
                        topRightInnerCornerY
                    )
                    fieldTopRightBorderConnectorPath.lineTo(
                        topRightInnerCornerX,
                        topRightInnerCornerY - fieldHorizontalBorderPaint.strokeWidth / 2
                    )
                    fieldTopRightBorderConnectorPath.lineTo(
                        topRightOuterCornerX,
                        topRightOuterCornerY
                    )

                    fieldRightTopBorderConnectorPath.moveTo(
                        topRightInnerCornerX,
                        topRightInnerCornerY
                    )
                    fieldRightTopBorderConnectorPath.lineTo(
                        topRightOuterCornerX,
                        topRightOuterCornerY
                    )
                    fieldRightTopBorderConnectorPath.lineTo(
                        (topRightOuterCornerX + ((fieldHorizontalBorderPaint.strokeWidth / 2) / cos(
                            30 * PI / 180
                        )) * cos(30 * PI / 180) * cos(30 * PI / 180)).toFloat(),
                        (topRightInnerCornerY + ((fieldHorizontalBorderPaint.strokeWidth / 2) / cos(
                            30 * PI / 180
                        )) * cos(30 * PI / 180) * sin(30 * PI / 180)).toFloat()
                    )
                }
                if (i == gridSize - 1) {
                    if (j == 0) {
                        val bottomRightInnerCornerX = (params.leftMargin + hexWidth).toFloat()
                        val bottomRightInnerCornerY = (params.topMargin + hexDiameter / 2 + hexHeight).toFloat()
                        fieldRightBorderPath.moveTo(
                            bottomRightInnerCornerX,
                            bottomRightInnerCornerY
                        )

                        val bottomRightOuterCornerX =
                            (bottomRightInnerCornerX + ((fieldHorizontalBorderPaint.strokeWidth / 2) / cos(
                                30 * PI / 180
                            )) * cos(30 * PI / 180) * sin(30 * PI / 180)).toFloat()
                        val bottomRightOuterCornerY =
                            bottomRightInnerCornerY + fieldHorizontalBorderPaint.strokeWidth / 2
                        fieldBottomRightBorderConnectorPath.moveTo(
                            bottomRightInnerCornerX,
                            bottomRightInnerCornerY
                        )
                        fieldBottomRightBorderConnectorPath.lineTo(
                            bottomRightInnerCornerX,
                            bottomRightInnerCornerY + fieldHorizontalBorderPaint.strokeWidth/2
                        )
                        fieldBottomRightBorderConnectorPath.lineTo(
                            bottomRightOuterCornerX,
                            bottomRightOuterCornerY
                        )

                        fieldRightBottomBorderConnectorPath.moveTo(
                            bottomRightInnerCornerX,
                            bottomRightInnerCornerY
                        )
                        fieldRightBottomBorderConnectorPath.lineTo(
                            bottomRightOuterCornerX,
                            bottomRightOuterCornerY
                        )
                        fieldRightBottomBorderConnectorPath.lineTo(
                            (bottomRightOuterCornerX + ((fieldHorizontalBorderPaint.strokeWidth / 2) / cos(
                                30 * PI / 180
                            )) * cos(30 * PI / 180) * cos(30 * PI / 180)).toFloat(),
                            (bottomRightInnerCornerY - ((fieldHorizontalBorderPaint.strokeWidth / 2) / cos(
                                30 * PI / 180
                            )) * cos(30 * PI / 180) * sin(30 * PI / 180)).toFloat()
                        )
                    }
                    fieldRightBorderPath.lineTo(
                        (params.leftMargin + hexDiameter).toFloat(),
                        (params.topMargin + hexDiameter / 2).toFloat()
                    )
                    fieldRightBorderPath.lineTo(
                        (params.leftMargin + hexWidth).toFloat(),
                        (params.topMargin + (hexDiameter / 2 - hexHeight)).toFloat()
                    )
                }
            }
        }
        invalidate()
    }

    override fun onDraw(c: Canvas) {
        super.onDraw(c)
        c.drawPath(fieldTopBorderPath, fieldHorizontalBorderPaint)
        c.drawPath(fieldLeftBorderPath, fieldVerticalBorderPaint)
        c.drawPath(fieldTopLeftBorderConnectorPath, fieldHorizontalBorderConnectorPaint)
        c.drawPath(fieldLeftTopBorderConnectorPath, fieldVerticalBorderConnectorPaint)
        c.drawPath(fieldTopRightBorderConnectorPath, fieldHorizontalBorderConnectorPaint)
        c.drawPath(fieldRightBorderPath, fieldVerticalBorderPaint)
        c.drawPath(fieldRightTopBorderConnectorPath, fieldVerticalBorderConnectorPaint)
        c.drawPath(fieldBottomBorderPath, fieldHorizontalBorderPaint)
        c.drawPath(fieldLeftBottomBorderConnectorPath, fieldVerticalBorderConnectorPaint)
        c.drawPath(fieldBottomLeftBorderConnectorPath, fieldHorizontalBorderConnectorPaint)
        c.drawPath(fieldBottomRightBorderConnectorPath, fieldHorizontalBorderConnectorPaint)
        c.drawPath(fieldRightBottomBorderConnectorPath, fieldVerticalBorderConnectorPaint)
        c.save()
    }

    fun setBorderParams(position: Pair<Int, Int>, innerColor: Int, borderParams: Pair<List<Int>, Int>) {
        cells[position]?.setColor(innerColor, borderParams.first)
        cells[position]?.setRadius(borderParams.second)
    }

    fun startAnimation(positions: List<Pair<Int, Int>>){
        for(i in positions.indices){
            cells[positions[i]]?.startAnimation(100L*i)
        }
    }

    fun cancelAnimation(positions: List<Pair<Int, Int>>){
        for(position in positions){
            cells[position]?.cancelAnimation()
        }
    }
}