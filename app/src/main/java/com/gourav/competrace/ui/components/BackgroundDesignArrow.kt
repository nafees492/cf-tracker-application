package com.gourav.competrace.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import com.gourav.competrace.utils.CardValues.RectangleFractionOfTriangle

@Composable
fun BackgroundDesignArrow(color: Color) {
    Row {
        Box(
            modifier = Modifier
                .weight(1 - RectangleFractionOfTriangle)
                .fillMaxHeight()
                .drawBehind {
                    val path = Path().apply {
                        moveTo(0f, size.height / 2f)
                        lineTo(size.width, 0f)
                        lineTo(size.width, size.height)
                        close()
                    }
                    drawPath(
                        path = path,
                        brush = SolidColor(color)
                    )
                },
        )
        Box(
            modifier = Modifier
                .weight(RectangleFractionOfTriangle)
                .fillMaxHeight()
                .drawBehind {
                    drawRect(
                        brush = SolidColor(color),
                        topLeft = Offset(x = 0f, y = 0f),
                        size = size
                    )
                }
        )
    }
}