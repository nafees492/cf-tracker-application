package com.gourav.competrace.app_core.util

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit

object SizeUtil {

    @Composable
    fun textUnitToDp(testUnit: TextUnit) =
        with(LocalDensity.current) { testUnit.toDp() }

    @Composable
    fun getTextWidthInDp(testSize: TextUnit, letters: Int = 1) =
        textUnitToDp(testUnit = testSize) * letters

    @Composable
    fun getCardHeight(
        titleLargeTexts: Int,
        bodyMediumTexts: Int,
        extraPaddingValues: Dp
    ) = extraPaddingValues +
            textUnitToDp(testUnit = MaterialTheme.typography.titleLarge.lineHeight) * titleLargeTexts +
            textUnitToDp(testUnit = MaterialTheme.typography.bodyMedium.lineHeight) * bodyMediumTexts
}