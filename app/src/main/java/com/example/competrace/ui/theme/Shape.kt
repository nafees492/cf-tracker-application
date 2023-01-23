package com.example.competrace.ui.theme

import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val CompetraceShapes = Shapes(
    extraSmall = ShapeDefaults.ExtraSmall.copy(CornerSize(1.dp)),
    small = ShapeDefaults.Small.copy(CornerSize(2.dp)),
    medium = ShapeDefaults.Medium.copy(CornerSize(1.dp)),
    large = ShapeDefaults.Large.copy(CornerSize(1.dp)),
    extraLarge = ShapeDefaults.ExtraLarge.copy(CornerSize(1.dp))
)
