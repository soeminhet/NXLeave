package com.smh.nxleave.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Dimensions(
    val default: Dp = 0.dp,
    val space2: Dp = 2.dp,
    val space4: Dp = 4.dp,
    val space6: Dp = 6.dp,
    val space8: Dp = 8.dp,
    val space10: Dp = 10.dp,
    val space12: Dp = 12.dp,
    val space14: Dp = 14.dp,
    val space16: Dp = 16.dp,
    val space18: Dp = 18.dp,
    val space20: Dp = 20.dp,
    val space22: Dp = 22.dp,
    val space24: Dp = 24.dp,
    val space28: Dp = 28.dp,
    val space30: Dp = 30.dp,
    val space32: Dp = 32.dp,
    val space36: Dp = 36.dp,
    val space40: Dp = 40.dp,
    val space48: Dp = 48.dp,
    val space52: Dp = 52.dp,
    val space56: Dp = 56.dp,
    val space64: Dp = 64.dp,
    val space77: Dp = 77.dp,
    val space80: Dp = 80.dp,
    val space100: Dp = 100.dp,

    val horizontalSpace: Dp = 20.dp,
    val sheetBottomSpace: Dp = 40.dp,
    val sheetMaxFraction: Float = 0.9f
)

val LocalSpacing = compositionLocalOf { Dimensions() }

val spacing: Dimensions
    @Composable
    @ReadOnlyComposable
    get() = LocalSpacing.current
