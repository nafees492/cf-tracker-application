package com.example.competrace.utils

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

class Polygon(private val sides: Int, private val rotation: Float = 0f) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(
            Path().apply {
                val radius = if (size.width > size.height) size.width / 2f else size.height / 2f
                val angle = 2.0 * Math.PI / sides
                val cx = size.width / 2f
                val cy = size.height / 2f
                val r = rotation * (Math.PI / 180)
                moveTo(
                    cx + (radius * kotlin.math.cos(0.0 + r).toFloat()),
                    cy + (radius * kotlin.math.sin(0.0 + r).toFloat())
                )
                for (i in 1 until sides) {
                    lineTo(
                        cx + (radius * kotlin.math.cos(angle * i + r).toFloat()),
                        cy + (radius * kotlin.math.sin(angle * i + r).toFloat())
                    )
                }
                close()
            })
    }
}