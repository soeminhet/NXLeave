package com.smh.nxleave.design.component

import NX_Black
import NX_Grey
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.smh.nxleave.ui.theme.LocalSpacing
import com.smh.nxleave.ui.theme.NXLeaveTheme

@Composable
fun NXLoading() {
    Dialog(
        onDismissRequest = { },
        properties = DialogProperties(
            decorFitsSystemWindows = false,
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Card(
            modifier = Modifier.size(64.dp),
            shape = MaterialTheme.shapes.medium,
        ) {
            Box(
                modifier = Modifier.size(64.dp),
                contentAlignment = Alignment.Center
            ) {
                SpinningProgressBar(
                    modifier = Modifier.size(32.dp),
                    tint = NX_Black
                )
            }
        }
    }
}

@Composable
fun NXLoadMore() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        contentAlignment = Alignment.Center
    ) {
        SpinningProgressBar(
            modifier = Modifier
                .size(32.dp)
                .padding(LocalSpacing.current.space4),
        )
    }
}

@Composable
private fun SpinningProgressBar(
    modifier: Modifier = Modifier,
    tint: Color = NX_Black
) {
    val count = 8
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = count.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = ""
    )

    Canvas(modifier = modifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        val width = size.width * .25f
        val height = size.height / 9

        val cornerRadius = width.coerceAtMost(height) / 2

        for (i in 0..360 step 360 / count) {
            rotate(i.toFloat()) {
                drawRoundRect(
                    color = Color.LightGray.copy(alpha = 0.7f),
                    topLeft = Offset(canvasWidth - width, (canvasHeight - height) / 2),
                    size = Size(width, height),
                    cornerRadius = CornerRadius(cornerRadius, cornerRadius)
                )
            }
        }

        val coefficient = 360f / count

        for (i in 1..5) {
            rotate((angle.toInt() + i) * coefficient) {
                drawRoundRect(
                    color = tint.copy(alpha = (0.2f + 0.2f * i).coerceIn(0f, 1f)),
                    topLeft = Offset(canvasWidth - width, ((canvasHeight - height) / 2)),
                    size = Size(width, height),
                    cornerRadius = CornerRadius(cornerRadius, cornerRadius)
                )
            }
        }
    }
}

@Preview
@Composable
private fun NXLoadingPreview() {
    NXLeaveTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            NXLoading()
        }
    }
}

@Preview
@Composable
private fun NXLoadMorePreview() {
    NXLeaveTheme {
        Surface(modifier = Modifier.fillMaxWidth()) {
            NXLoadMore()
        }
    }
}