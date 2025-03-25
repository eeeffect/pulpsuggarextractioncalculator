package com.barkhatov.pulpsuggarextractioncalculator

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.pow

/**
 * Компонент для відображення графіка залежності виходу цукру від вмісту цукрози
 */
@Composable
fun SugarOutputChart(
    equation: (Float) -> Float,
    minX: Float = 0.4f,
    maxX: Float = 2.0f,
    title: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(300.dp)
    ) {

        Text(
            text = title,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 16.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .padding(top = 16.dp)
        ) {
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                val width = size.width
                val height = size.height
                val padding = 40f

                // X та Y осі
                drawLine(
                    color = Color.Gray,
                    start = Offset(padding, height - padding),
                    end = Offset(width - padding, height - padding),
                    strokeWidth = 2f
                )

                drawLine(
                    color = Color.Gray,
                    start = Offset(padding, height - padding),
                    end = Offset(padding, padding),
                    strokeWidth = 2f
                )

                // Знаходимо максимальне значення функції для нормалізації
                val values = mutableListOf<Float>()
                val step = (maxX - minX) / 100
                var x = minX
                while (x <= maxX) {
                    values.add(equation(x))
                    x += step
                }

                val maxY = values.maxOrNull() ?: 15f
                val minY = values.minOrNull() ?: 10f

                // Малюємо графік
                val path = Path()
                x = minX
                var firstPoint = true

                while (x <= maxX) {
                    val y = equation(x)

                    // Нормалізуємо координати для відображення на канвасі
                    val xPos = padding + (x - minX) * (width - 2 * padding) / (maxX - minX)
                    val yPos = height - padding - (y - minY) * (height - 2 * padding) / (maxY - minY)

                    if (firstPoint) {
                        path.moveTo(xPos, yPos)
                        firstPoint = false
                    } else {
                        path.lineTo(xPos, yPos)
                    }

                    x += step
                }

                drawPath(
                    path = path,
                    color = Color.Red,//MaterialTheme.colorScheme.primary,
                    style = Stroke(width = 3f)
                )

                // Малюємо підписи осей
                // X-вісь підписи
                for (i in 0..4) {
                    val xLabel = minX + i * (maxX - minX) / 4
                    val xPos = padding + i * (width - 2 * padding) / 4

                    drawLine(
                        color = Color.Gray,
                        start = Offset(xPos, height - padding),
                        end = Offset(xPos, height - padding + 10),
                        strokeWidth = 1f
                    )

                    drawContext.canvas.nativeCanvas.drawText(
                        String.format("%.1f", xLabel),
                        xPos,
                        height - padding + 25,
                        android.graphics.Paint().apply {
                            color = android.graphics.Color.BLACK
                            textSize = 30f
                            textAlign = android.graphics.Paint.Align.CENTER
                        }
                    )
                }

                // Y-вісь підписи
                for (i in 0..4) {
                    val yLabel = minY + i * (maxY - minY) / 4
                    val yPos = height - padding - i * (height - 2 * padding) / 4

                    drawLine(
                        color = Color.Gray,
                        start = Offset(padding, yPos),
                        end = Offset(padding - 10, yPos),
                        strokeWidth = 1f
                    )

                    drawContext.canvas.nativeCanvas.drawText(
                        String.format("%.1f", yLabel),
                        padding - 15,
                        yPos + 10,
                        android.graphics.Paint().apply {
                            color = android.graphics.Color.BLACK
                            textSize = 30f
                            textAlign = android.graphics.Paint.Align.RIGHT
                        }
                    )
                }

                // Позначення осей
                drawContext.canvas.nativeCanvas.drawText(
                    "Вміст цукрози, %",
                    width / 2,
                    height - 5,
                    android.graphics.Paint().apply {
                        color = android.graphics.Color.BLACK
                        textSize = 30f
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                )

                drawContext.canvas.nativeCanvas.drawText(
                    "Вихід цукру, %",
                    20f,
                    height / 2,
                    android.graphics.Paint().apply {
                        color = android.graphics.Color.BLACK
                        textSize = 30f
                        textAlign = android.graphics.Paint.Align.CENTER

                        // Поворот тексту для вертикальної осі
                        setTextAlign(android.graphics.Paint.Align.CENTER)
                        textScaleX = 1.0f
                        textSkewX = 0.0f
                    }.apply {
                        // Повертаємо канвас для вертикального тексту
                        drawContext.canvas.nativeCanvas.save()
                        drawContext.canvas.nativeCanvas.rotate(-90f, 20f, height / 2)
                    }
                )

                // Відновлюємо канвас після повороту
                drawContext.canvas.nativeCanvas.restore()
            }
        }
    }
}

/**
 * Компонент для відображення порівняльних графіків виходу цукру
 */
@Composable
fun ComparativeChart(viewModel: SugarCalculatorViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Порівняльний аналіз режимів роботи",
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                val width = size.width
                val height = size.height
                val padding = 40f

                // X та Y осі
                drawLine(
                    color = Color.Gray,
                    start = Offset(padding, height - padding),
                    end = Offset(width - padding, height - padding),
                    strokeWidth = 2f
                )

                drawLine(
                    color = Color.Gray,
                    start = Offset(padding, height - padding),
                    end = Offset(padding, padding),
                    strokeWidth = 2f
                )

                // Діапазон значень X та Y
                val minX = 0.4f
                val maxX = 2.0f
                val minY = 10f
                val maxY = 15f
                val step = (maxX - minX) / 100

                // Графік для режиму без повернення жомопресової води
                val path1 = Path()
                var x = minX
                var firstPoint = true

                while (x <= maxX) {
                    val y = viewModel.optimizationEquation1(x)

                    // Нормалізуємо координати для відображення на канвасі
                    val xPos = padding + (x - minX) * (width - 2 * padding) / (maxX - minX)
                    val yPos = height - padding - (y - minY) * (height - 2 * padding) / (maxY - minY)

                    if (firstPoint) {
                        path1.moveTo(xPos, yPos)
                        firstPoint = false
                    } else {
                        path1.lineTo(xPos, yPos)
                    }

                    x += step
                }

                drawPath(
                    path = path1,
                    color = Color.Blue,
                    style = Stroke(width = 3f)
                )

                // Графік для режиму з поверненням очищеної жомопресової води
                val path2 = Path()
                x = minX
                firstPoint = true

                while (x <= maxX) {
                    val y = viewModel.optimizationEquation2(x)

                    // Нормалізуємо координати для відображення на канвасі
                    val xPos = padding + (x - minX) * (width - 2 * padding) / (maxX - minX)
                    val yPos = height - padding - (y - minY) * (height - 2 * padding) / (maxY - minY)

                    if (firstPoint) {
                        path2.moveTo(xPos, yPos)
                        firstPoint = false
                    } else {
                        path2.lineTo(xPos, yPos)
                    }

                    x += step
                }

                drawPath(
                    path = path2,
                    color = Color.Green,
                    style = Stroke(width = 3f)
                )

                // Графік для режиму з поверненням неочищеної жомопресової води
                val path3 = Path()
                x = minX
                firstPoint = true

                while (x <= maxX) {
                    val y = viewModel.optimizationEquation3(x)

                    // Нормалізуємо координати для відображення на канвасі
                    val xPos = padding + (x - minX) * (width - 2 * padding) / (maxX - minX)
                    val yPos = height - padding - (y - minY) * (height - 2 * padding) / (maxY - minY)

                    if (firstPoint) {
                        path3.moveTo(xPos, yPos)
                        firstPoint = false
                    } else {
                        path3.lineTo(xPos, yPos)
                    }

                    x += step
                }

                drawPath(
                    path = path3,
                    color = Color.Red,
                    style = Stroke(width = 3f)
                )

                // Підписи та легенда
                drawContext.canvas.nativeCanvas.drawText(
                    "Вміст цукрози, %",
                    width / 2,
                    height - 5,
                    android.graphics.Paint().apply {
                        color = android.graphics.Color.BLACK
                        textSize = 30f
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                )
            }
        }

        // Легенда
        Row(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            LegendItem(color = Color.Blue, text = "Без повернення")
            LegendItem(color = Color.Red, text = "З поверненням без очищення")
            LegendItem(color = Color.Green, text = "З поверненням з очищенням")
        }
    }
}

@Composable
fun LegendItem(color: Color, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(color)
        )
        Text(
            text = text,
            fontSize = 12.sp,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}