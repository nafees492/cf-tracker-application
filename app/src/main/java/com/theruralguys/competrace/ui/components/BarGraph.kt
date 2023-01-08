package com.theruralguys.competrace.ui.components

import android.graphics.Point
import android.graphics.Typeface
import androidx.compose.animation.core.FloatTweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import android.graphics.Paint as Paint1

@Composable
fun BarGraph(heights: Array<Int>, colors: Array<Color>, questionCount: Array<Int>, stepSizeOfGraph: Int) {

    val width = 69
    val gap = 22
    val top = 150f
    val bottom = 750f
    val start = 125f
    val end = 875f

    val ratingArray = arrayListOf(
        "800 - 1199",
        "1200 - 1399",
        "1400 - 1599",
        "1600 - 1899",
        "1900 - 2099",
        "2100 - 2399",
        "2400 - 4000",
        "Incorrect Submissions"
    )

    val points = arrayListOf<Point>()
    for (i in 0..7) {
        points.add(Point((i + 1) * gap + i * width, heights[i]))
    }

    val context = LocalContext.current
    val textColor = MaterialTheme.colorScheme.onSurface.toArgb()

    var begin by remember { mutableStateOf(false) }

    val animateHeight by animateFloatAsState(
        targetValue = if (begin) 1f else 0f,
        animationSpec = FloatTweenSpec(duration = 1000)
    )

    val maxQuestionCount = questionCount.max()

    val screenWidthDp: Int = context.resources.configuration.screenWidthDp - 32

    val primary = MaterialTheme.colorScheme.primary
    val onSurface = MaterialTheme.colorScheme.onSurface

    val textPaint: (size: Float, alignment: Paint1.Align) -> Paint1 = { size, alignment ->
        Paint1().apply {
            textSize = size
            color = textColor
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
            textAlign = alignment
        }
    }

    var status by remember {
        mutableStateOf("Total Question Attempted = " + questionCount.sum())
    }

    val onClickedCanvas: (Offset) -> Unit = {
        var index = -1
        for ((i, point) in points.withIndex()) {
            if (it.x > point.x + start && it.x < point.x + start + width && it.y > bottom - point.y && it.y < bottom) {
                index = i
                break
            }
        }
        status = when(index) {
            -1 -> "Total Question Attempted = " + questionCount.sum()
            7 -> "Incorrect/Partial Submissions: ${questionCount[index]}"
            else -> "For Rating (${ratingArray[index]}): ${questionCount[index]}"
        }
    }

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(screenWidthDp.dp)
            .padding(16.dp)
            .background(
                color = MaterialTheme.colorScheme.surface.copy(0.9f),
                shape = RoundedCornerShape(8.dp)
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = onClickedCanvas
                )
            }
    ) {
        drawLine(
            start = Offset(x = start, y = bottom),
            end = Offset(x = start, y = top),
            color = onSurface,
            strokeWidth = 1f
        )
        drawLine(
            start = Offset(x = start, y = bottom),
            end = Offset(x = end, y = bottom),
            color = onSurface,
            strokeWidth = 1f
        )

        for (i in 1..11) {
            drawLine(
                start = Offset(x = start, y = bottom - 50f * i),
                end = Offset(x = end, y = bottom - 50f * i),
                color = onSurface,
                strokeWidth = 0.4f
            )
            drawIntoCanvas {
                it.nativeCanvas.drawText(
                    /* text = */ "${stepSizeOfGraph * i}",
                    /* x = */ 100f,            // x-coordinates of the origin (top left)
                    /* y = */ bottom - 50f * i + 7f, // y-coordinates of the origin (top left)
                    /* paint = */ textPaint(20f, Paint1.Align.RIGHT)
                )
            }
        }

        drawRoundRect(
            color = primary,
            topLeft = Offset(
                x = 700f,
                y = 135f
            ),
            size = Size(width = 260f, height = 260f),
            alpha = 0.5f,
            cornerRadius = CornerRadius(x = 5f, y = 5f)
        )

        for (i in 0..7) {
            drawCircle(
                color = colors[i],
                radius = 5f,
                center = Offset(x = 720f, y = 160f + i * 30f)
            )

            drawIntoCanvas {
                it.nativeCanvas.drawText(
                    /* text = */ ratingArray[i],
                    /* x = */ 740f,            // x-coordinates of the origin (top left)
                    /* y = */ 166f + i * 30f, // y-coordinates of the origin (top left)
                    /* paint = */ textPaint(20f, Paint1.Align.LEFT)
                )
            }
        }

        drawIntoCanvas {
            it.nativeCanvas.drawText(
                /* text = */ "No Of Questions vs Rating",
                /* x = */ 500f,            // x-coordinates of the origin (top left)
                /* y = */ 825f, // y-coordinates of the origin (top left)
                /* paint = */ textPaint(48f, Paint1.Align.CENTER)
            )
        }

        begin = true

        drawIntoCanvas {
            it.nativeCanvas.drawText(
                /* text = */ status,
                /* x = */ 500f,            // x-coordinates of the origin (top left)
                /* y = */ 80f, // y-coordinates of the origin (top left)
                /* paint = */ textPaint(48f, Paint1.Align.CENTER)
            )
        }

        for (i in 0..7) {
            drawRect(
                color = colors[i],
                topLeft = Offset(
                    x = points[i].x + start,
                    y = bottom - (points[i].y) * animateHeight
                ),
                size = Size(width = width.toFloat(), height = (points[i].y) * animateHeight)
            )
        }
    }
}