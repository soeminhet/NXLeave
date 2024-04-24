package com.smh.nxleave.design.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.smh.nxleave.ui.theme.NXLeaveTheme

@Composable
fun Label(
    value: String,
    modifier: Modifier = Modifier,
    onViewAll: (() -> Unit)? = null,
) {
    val showViewAll = remember(onViewAll) { onViewAll != null }

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )

        TextButton(
            onClick = { onViewAll?.invoke() },
            enabled = showViewAll
        ) {
            Text(
                text = "View All",
                style = MaterialTheme.typography.labelMedium,
                color = if (showViewAll) MaterialTheme.colorScheme.secondary else Color.Transparent
            )
        }
    }
}

@Preview
@Composable
fun LabelPreview() {
    NXLeaveTheme {
        Surface {
            Label(value = "Value")
        }
    }
}

@Preview
@Composable
fun LabelViewAllPreview() {
    NXLeaveTheme {
        Surface {
            Label(value = "Value", onViewAll = {})
        }
    }
}