package com.gourav.competrace.progress.user.presentation

import android.content.Context
import androidx.compose.animation.core.FloatTweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat
import com.gourav.competrace.R
import com.gourav.competrace.ui.theme.*
import android.graphics.Paint as Paint1


@Composable
fun BarGraphNoOfQueVsRatings(questionCountArray: Array<Int>) {

    val ratingArray = arrayListOf(
        "800 - 1199",
        "1200 - 1399",
        "1400 - 1599",
        "1600 - 1899",
        "1900 - 2099",
        "2100 - 2399",
        "2400 - 3500",
        "Unrated",
        "Incorrect Submissions"
    )

    val colors = arrayListOf(
        NewbieGray,
        PupilGreen,
        SpecialistCyan,
        ExpertBlue,
        CandidMasterViolet,
        MasterOrange,
        GrandmasterRed,
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.onSurface
    )

    val context = LocalContext.current
    val textColor = MaterialTheme.colorScheme.onSurface.toArgb()

    var begin by remember { mutableStateOf(false) }

    val animateHeight by animateFloatAsState(
        targetValue = if (begin) 1f else 0f,
        animationSpec = FloatTweenSpec(duration = 1000)
    )

    val screenWidthInDp = context.resources.configuration.screenWidthDp.dp

    val onSurface = MaterialTheme.colorScheme.onSurface

    var status by remember {
        mutableStateOf("Total Question Attempted: " + questionCountArray.sum())
    }

    var selectedOffset by remember {
        mutableStateOf(Offset(0f, 0f))
    }

    Canvas(
        modifier = Modifier
            .size(screenWidthInDp * 0.9f)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { selectedOffset = it }
                )
            }
    ) {
        val start = size.width * 0.2f
        val bottom = size.height * 0.7f
        val top = bottom - size.height * 0.55f
        val end = start + size.width * 0.7f

        val barWidth = size.width * 0.06f
        val gap = size.width * 0.016f
        val verticalStep = size.height * 0.05f

        val labelSize = size.width * 0.02f
        val headingSize = size.width * 0.05f

        val maxQuestionCount = questionCountArray.max()
        val stepSizeOfGraph = (maxQuestionCount / 10 + 1)

        val xValueAndHeight = arrayListOf<Offset>()
        for (i in 0..8) {
            xValueAndHeight.add(Offset((i + 1) * gap + i * barWidth, (questionCountArray[i] * verticalStep) / stepSizeOfGraph))
        }

        selectedOffset.let {
            var index = -1
            for ((i, point) in xValueAndHeight.withIndex()) {
                if (it.x > point.x + start && it.x < point.x + start + barWidth && it.y > bottom - point.y && it.y < bottom) {
                    index = i
                    break
                }
            }
            status = when (index) {
                -1 -> "Total Question Attempted: " + questionCountArray.sum()
                7 -> "Unrated: ${questionCountArray[index]}"
                8 -> "Incorrect/Partial Submissions: ${questionCountArray[index]}"
                else -> "For Rating (${ratingArray[index]}): ${questionCountArray[index]}"
            }
        }

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

        for (i in 1..10) {
            drawLine(
                start = Offset(x = start, y = bottom - verticalStep * i),
                end = Offset(x = end, y = bottom - verticalStep * i),
                color = onSurface,
                strokeWidth = 0.4f
            )
            drawLabelInCanvas(
                context = context,
                text = "${stepSizeOfGraph * i}",
                color = textColor,
                size = labelSize,
                position = Offset(
                    start - size.width * 0.02f,
                    bottom - verticalStep * i + size.width * 0.008f
                ),
                alignment = Paint1.Align.RIGHT
            )
        }

        drawLabelInCanvas(
            context = context,
            text = "Rating",
            color = textColor,
            size = headingSize,
            position = Offset(size.width * 0.5f, size.height * 0.9f),
            alignment = Paint1.Align.CENTER
        )

        drawLabelInCanvas(
            context = context,
            text = status,
            color = textColor,
            size = headingSize,
            position = Offset(size.width * 0.5f, size.height * 0.1f),
            alignment = Paint1.Align.CENTER
        )

        begin = true

        for (i in 0..8) {
            drawRect(
                color = colors[i],
                topLeft = Offset(
                    x = xValueAndHeight[i].x + start,
                    y = bottom - (xValueAndHeight[i].y) * animateHeight
                ),
                size = Size(width = barWidth, height = (xValueAndHeight[i].y) * animateHeight)
            )
        }
    }
    Canvas(
        modifier = Modifier
            .rotate(-90f)
            .size(screenWidthInDp * 0.9f)
    ) {
        val barWidth = size.height * 0.06f
        val gap = size.height * 0.016f

        val barLabelStart = Offset(
            size.width * 0.27f,
            size.height * 0.20f + gap + barWidth * 0.6f
        )
        val labelSize = size.width * 0.02f
        val headingSize = size.width * 0.05f

        for (i in 0..8) {
            drawLabelInCanvas(
                context = context,
                text = ratingArray[i],
                color = textColor,
                size = labelSize,
                position = Offset(
                    barLabelStart.x,
                    barLabelStart.y + i * (barWidth + gap)
                ),
                alignment = Paint1.Align.RIGHT
            )
        }
        drawLabelInCanvas(
            context = context,
            text = "No. of Questions",
            color = textColor,
            size = headingSize,
            position = Offset(size.width * 0.6f, size.height * 0.1f),
            alignment = Paint1.Align.CENTER
        )
    }

}

fun DrawScope.drawLabelInCanvas(
    context: Context,
    text: String,
    color: Int,
    size: Float,
    position: Offset,
    alignment: Paint1.Align
) {
    drawIntoCanvas {
        it.nativeCanvas.drawText(
            /* text = */ text,
            /* x = */ position.x,            // x-coordinates of the origin (top left)
            /* y = */ position.y, // y-coordinates of the origin (top left)
            /* paint = */ Paint1().apply {
                textSize = size
                this.color = color
                typeface = ResourcesCompat.getFont(context, R.font.montserrat_regular)
                textAlign = alignment
            }
        )
    }
}