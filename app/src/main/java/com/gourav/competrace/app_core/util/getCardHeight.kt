package com.gourav.competrace.utils

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp

@Composable

fun getCardHeight(
    titleLargeTexts: Int,
    bodyMediumTexts: Int,
    extraPaddingValues: Dp
) =
    extraPaddingValues +
    textUnitToDp(testUnit = MaterialTheme.typography.titleLarge.lineHeight) * titleLargeTexts +
    textUnitToDp(testUnit = MaterialTheme.typography.bodyMedium.lineHeight) * bodyMediumTexts