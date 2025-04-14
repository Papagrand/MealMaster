package ru.point.profile.ui.transformations

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import coil.transform.Transformation
import coil.transform.CircleCropTransformation
import coil.size.Size

class CircleWithBorderTransformation(
    private val borderWidth: Float, // в пикселях
    private val borderColor: Int
) : Transformation {

    override val cacheKey: String
        get() = "CircleWithBorderTransformation(borderWidth=$borderWidth,borderColor=$borderColor)"

    override suspend fun transform(input: Bitmap, size: Size): Bitmap {
        // Применяем круговую обрезку
        val circleBitmap = CircleCropTransformation().transform(input, size)
        // Создаём изменяемую копию
        val output = circleBitmap.copy(circleBitmap.config ?: Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(output)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = borderWidth
            color = borderColor
        }
        // Вычисляем радиус так, чтобы обводка полностью помещалась внутри
        val radius = output.width / 2f - borderWidth / 2f
        canvas.drawCircle(output.width / 2f, output.height / 2f, radius, paint)
        return output
    }
}