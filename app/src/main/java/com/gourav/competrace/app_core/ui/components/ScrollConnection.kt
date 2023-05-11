package com.gourav.competrace.app_core.ui.components

import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

data class ScrollConnectionState(
    val topBarHeightOffset: MutableState<Float>,
    val nestedScrollConnection: NestedScrollConnection
)

@Composable
fun rememberScrollConnectionState(): ScrollConnectionState {
    val topBarHeightPx = with(LocalDensity.current) {
        64.dp.roundToPx().toFloat()
    }
    val heightOffset = remember { mutableStateOf(0f) }

    val nestedScrollConnection = object : NestedScrollConnection {
        override fun onPreScroll(
            available: Offset,
            source: NestedScrollSource
        ): Offset {
            val newOffset = heightOffset.value + available.y
            heightOffset.value =
                newOffset.coerceIn(-topBarHeightPx, 0f)
            return Offset.Zero
        }
    }

    return remember {
        ScrollConnectionState(
            heightOffset,
            nestedScrollConnection
        )
    }
}

fun Modifier.addScrollConnection(state: ScrollConnectionState) = then(
    offset {
        IntOffset(
            x = 0,
            y = state.topBarHeightOffset.value.roundToInt()
        )
    }
)