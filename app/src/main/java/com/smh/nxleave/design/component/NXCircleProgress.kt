package com.smh.nxleave.design.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smh.nxleave.ui.theme.NXLeaveTheme

@Composable
fun NXCircleProgress(
    progresses: Map<Color, Int>,
    modifier: Modifier = Modifier,
    strokeWidth: Float = 70f,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    centerContent: @Composable BoxScope.() -> Unit
) {
    val padding = with(LocalDensity.current) { (strokeWidth / 2).toDp() }
    var calculatedProgress by remember { mutableStateOf<Map<Color, Float>>(mapOf()) }

    LaunchedEffect(key1 = progresses) {
        val newProgresses: MutableMap<Color, Float> = mutableMapOf()
        val progressesList = progresses.toList()
        val lastIndex = progressesList.lastIndex
        progressesList.forEachIndexed { index, progress ->
            val realProgress = (index..lastIndex).sumOf { progressesList[it].second }.toFloat() / 100f
            newProgresses[progress.first] = realProgress * 360f
        }
        calculatedProgress = newProgresses
    }

    Box(
        modifier = modifier
            .padding(padding)
            .drawWithCache {
                onDrawBehind {
                    drawCircle(
                        color = backgroundColor,
                        style = Stroke(width = strokeWidth)
                    )

                    for (progress in calculatedProgress) {
                        drawArc(
                            color = progress.key,
                            startAngle = -90f,
                            sweepAngle = progress.value,
                            useCenter = false,
                            style = Stroke(
                                width = strokeWidth,
                                cap = StrokeCap.Round
                            )
                        )
                    }
                }
            },
        contentAlignment = Alignment.Center,
        content = centerContent
    )
}

@Preview(showBackground = true)
@Composable
private fun NXCircleProgressPreview() {
    NXLeaveTheme {
        NXCircleProgress(
            modifier = Modifier.size(300.dp),
            progresses = mapOf(
                Pair(Color.Green, 10),
                Pair(Color.Yellow, 20),
                Pair(Color.Red, 30),
            ),
            centerContent = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Total Left",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        text = "50 Days",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        )
    }
}