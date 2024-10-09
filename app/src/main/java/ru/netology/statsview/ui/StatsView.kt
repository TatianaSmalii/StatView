package ru.netology.statsview.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import ru.netology.statsview.R
import ru.netology.statsview.utils.AndroidUtils
import kotlin.math.min
import kotlin.random.Random

class StatsView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(context, attributeSet, defStyleAttr, defStyleRes) {


    private var textSize = AndroidUtils.dp(context, 20F).toFloat()
    private var lineWidth = AndroidUtils.dp(context, 5F).toFloat()
    private var colorsList = emptyList<Int>()

    init {
        context.withStyledAttributes(attributeSet, R.styleable.StatsView) {
            textSize = getDimension(R.styleable.StatsView_textSize, textSize)
            lineWidth = getDimension(R.styleable.StatsView_lineWidth, lineWidth)
            colorsList = listOf(
                getColor(R.styleable.StatsView_color1, generateRandomColor()),
                getColor(R.styleable.StatsView_color2, generateRandomColor()),
                getColor(R.styleable.StatsView_color3, generateRandomColor()),
                getColor(R.styleable.StatsView_color4, generateRandomColor())
            )
        }
    }

    var data: List<Float> = emptyList()
        set(value) {
            field = value
            if (data.sum() > 1) {
                calculatePercentages()
            } else calculate()

            invalidate()
        }

    private var total: Float = 0.0f

    private var percentages: List<Float> = emptyList()

    private var radius = 0F
    private var center = PointF() //  точка центра окружности
    private var oval = RectF()

    private val paint = Paint(
        Paint.ANTI_ALIAS_FLAG // сглаживание
    ).apply {
        strokeWidth = lineWidth// ширина строки
        style = Paint.Style.STROKE // стиль : строки!
        strokeJoin = Paint.Join.ROUND // округление краев при отрисовке
        strokeCap = Paint.Cap.ROUND // округление краев при отрисовке


    }
    private var textPaint = Paint(
        Paint.ANTI_ALIAS_FLAG // сглаживание
    ).apply {
        textSize =
            this@StatsView.textSize // учтем  получ размер  шрифта при создании кисти доя текста
        style = Paint.Style.FILL // стиль : строки - заливка!
        textAlign = Paint.Align.CENTER // текст по центру

    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        radius = min(w, h) / 2F - lineWidth // отступ от краёв окружности
        center = PointF(w / 2F, h / 2F) // координаты x и y
        oval = RectF(
            center.x - radius, // положение левой грани
            center.y - radius,// положение верхней грани
            center.x + radius, // положение правой грани
            center.y + radius// положение нижней грани
        )
    }

    override fun onDraw(canvas: Canvas) {


        if (percentages.isEmpty()) {
            return
        }

        var startAngle = -90F // стартовый угол поворота
        val fullCircle = 360F
        var angle: Float

        if (total == 1F) {
            paint.color = Color.parseColor("#808080")
            canvas.drawArc(oval, startAngle, fullCircle, false, paint)
        }


        percentages.forEachIndexed { index, datum ->

            angle = datum * 360F  // угол поворота для каждого элемента
            if (data.sum() <= total) {
                paint.color = colorsList.getOrElse(index) { generateRandomColor() }
            }

            canvas.drawArc(oval, startAngle, angle, false, paint) // отрисовка дуги

            startAngle += angle // не рисовать на 1ом месте
        }



        if (total >= 1) {
            paint.color = colorsList.getOrNull(data.withIndex().firstOrNull()?.index!!)
                ?: generateRandomColor()
        }

        canvas.drawPoint(center.x, center.y - radius, paint)

        canvas.drawText(
            "%.2f%%".format(percentages.sum() * 100), // получить как надо в процентах
            center.x,
            center.y + textPaint.textSize / 4, // выравнивание по центру  Y  текста (высота)
            textPaint
        )
    }

    private fun generateRandomColor() = Random.nextInt(0xFF000000.toInt(), 0xFFFFFFFF.toInt())

    private fun calculatePercentages() {
        total = data.sum()
        percentages = data.map { it / total }
    }

    private fun calculate() {
        total = 1F
        percentages = data.map { it / total }
    }
}