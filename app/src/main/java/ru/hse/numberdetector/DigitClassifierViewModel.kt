package ru.hse.numberdetector

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel

class DigitClassifierViewModel(context: Context) : ViewModel() {
    private val lines = mutableStateListOf<Line>()
    private var size by mutableStateOf(
        Size(400.0F, 400.0F)
    )

    var initialized by mutableStateOf(false)
    private val classifier: DigitClassifier = DigitClassifier(context)
    var imageBitmap: ImageBitmap by mutableStateOf(drawToBitmap(lines))
    var prediction by mutableStateOf(
        "Ожидание ввода..."
    )

    fun initialize(width: Float) {
        classifier.initialize()
            .addOnSuccessListener {
                prediction = "Ожидание ввода..."
            }
            .addOnFailureListener { _ ->
                prediction = "Ошибка загрузки классификатора"
            }
        size = Size(width, width)

        imageBitmap = drawToBitmap(lines)
        initialized = true
    }

    fun clearImage() {
        lines.clear()
        imageBitmap = drawToBitmap(lines)
        prediction = "Ожидание ввода..."
    }

    fun addLine(line: Line) {
        lines.add(line)
        imageBitmap = drawToBitmap(lines)
        if (classifier.isInitialized) {
            classifier.classifyAsync(imageBitmap.asAndroidBitmap())
                .addOnSuccessListener { resultText ->
                    prediction = resultText
                }
                .addOnFailureListener { _ ->
                    prediction = "Ошибка распознавания"
                }
        }
    }

    data class Line(
        val start: Offset,
        val end: Offset,
        val color: Color = Color.Black,
        val strokeWidth: Dp = 4.dp
    )


    private fun CanvasDrawScope.asBitmap(size: Size, onDraw: DrawScope.() -> Unit): ImageBitmap {
        val bitmap = ImageBitmap(size.width.toInt(), size.height.toInt())
        draw(
            Density(4f),
            LayoutDirection.Ltr,
            androidx.compose.ui.graphics.Canvas(bitmap),
            size
        ) { onDraw() }
        return bitmap
    }

    private fun drawToBitmap(lines: List<Line>): ImageBitmap {
        val drawScope = CanvasDrawScope()
        val bitmap = drawScope.asBitmap(size) {
            lines.forEach { line ->
                drawLine(
                    color = line.color,
                    start = line.start,
                    end = line.end,
                    strokeWidth = line.strokeWidth.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }
        return bitmap
    }

    override fun onCleared() {
        classifier.close()
        super.onCleared()
    }
}



