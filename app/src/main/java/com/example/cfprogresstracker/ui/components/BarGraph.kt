package com.example.cfprogresstracker.ui.components

import android.content.res.Configuration
import android.graphics.Point
import android.widget.Toast
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun BarGraph(heights: Array<Int>, colors: Array<Color>, questionCount: Array<Int>) {
    val width = 90
    val gap = 20
    val point = arrayListOf<Point>()
    for (i in 0..7) {
        point.add(Point((i + 1) * gap + i * width, heights[i]))
    }

    val context = LocalContext.current

    var start by remember { mutableStateOf(false) }

    val animate by animateFloatAsState(
        targetValue = if (start) 1f else 0f,
        animationSpec = FloatTweenSpec(duration = 1000)
    )

    val configuration: Configuration = context.resources.configuration
    val screenWidthDp: Int = configuration.screenWidthDp - 32

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(screenWidthDp.dp)
            .padding(16.dp)
            .background(
                color = MaterialTheme.colorScheme.primaryContainer.copy(0.1f),
                shape = RoundedCornerShape(8.dp)
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        val i = identifyCLickItem(
                            x = it.x,
                            points = point,
                            questionCount = questionCount
                        )
                        if(i != -1)
                            Toast.makeText(context, "No of Questions: $i", Toast.LENGTH_SHORT)
                                .show()
                    }
                )
            }
    ) {
        drawLine(
            start = Offset(45f, 850f),
            end = Offset(45f, 50f),
            color = Color.Black,
            strokeWidth = 2f
        )
        drawLine(
            start = Offset(45f, 850f),
            end = Offset(945f, 850f),
            color = Color.Black,
            strokeWidth = 1f
        )

        start = true

        for (i in 0..7) {
            drawRect(
                color = colors[i],
                topLeft = Offset(x = point[i].x + 45f, y = 850 - (point[i].y) * animate),
                size = Size(width = width.toFloat(), height = (point[i].y) * animate)
            )
        }
    }
}

private fun identifyCLickItem(
    x: Float,
    points: List<Point>,
    questionCount: Array<Int>
): Int {
    for ((index, point) in points.withIndex()) {
        if (x > point.x + 45 && x < point.x + 45 + 90) {
            return questionCount[index]
        }
    }
    return -1
}
