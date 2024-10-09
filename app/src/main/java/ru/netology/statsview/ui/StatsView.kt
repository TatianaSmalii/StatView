package ru.netology.statsview.ui

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
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

    private var progress = 0f
    private var valueAnimator: ValueAnimator? = null
    var data: List<Float> = emptyList()
        set(value) {
            field = value
            if (data.sum() > 1) {
                calculatePercentages()
            } else calculate()
            update()
        }


    private var rotationAngle = 0f
    private var total: Float = 0.0F
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
            this@StatsView.textSize // учтем  получ размер  шрифта при создании кисти для текста
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


    private fun update() {
        valueAnimator?.let {
            it.removeAllListeners()
            it.cancel()
        }

        progress = 0f

        valueAnimator = ValueAnimator.ofFloat(0f, 360f).apply {
            addUpdateListener { anim ->
                progress = anim.animatedFraction // Если прогресс должен отслеживать долю анимации
                rotationAngle = anim.animatedValue as Float // Угол поворота
                invalidate() // Запрашиваем перерисовку
            }
            duration = 5000 // Продолжительность анимации
            interpolator = LinearInterpolator() // Интерполятор
            //  repeatMode = ValueAnimator.RESTART // Повторять анимацию
            // repeatCount = ValueAnimator.INFINITE // Бесконечное повторение
        }.also {
            it.start()
        }

    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas) // Не забывайте вызывать super.onDraw
        if (data.isEmpty()) {
            return
        }

        var startFrom = -90f

        // Применяем поворот перед рисованием дуг
        canvas.save() // Сохраняем текущее состояние холста
        canvas.rotate(rotationAngle, center.x, center.y) // Поворачиваем холст вокруг центра

        for ((index, datum) in data.withIndex()) {
            val angle = 360f * datum
            paint.color = colorsList.getOrNull(index) ?: generateRandomColor()
            canvas.drawArc(oval, startFrom, angle * progress, false, paint)
            startFrom += angle
        }

        canvas.restore() // Восстанавливаем холст после поворота

        // Рисуем текст
        canvas.drawText(
            "%.2f%%".format((data.sum() * 100)),
            center.x,
            center.y + textPaint.textSize / 4,
            textPaint,
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