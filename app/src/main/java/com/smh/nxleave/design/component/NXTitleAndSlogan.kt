package com.smh.nxleave.design.component

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.smh.nxleave.ui.theme.DMSerifDisplayRegular

@Composable
fun NXTitleAndSlogan(
    modifier: Modifier = Modifier,
    horizontalAlignment: Alignment.Horizontal = Alignment.End
) {
    Column(
        modifier = modifier,
        horizontalAlignment = horizontalAlignment
    ) {
        Text(
            text = "NXLeave",
            fontFamily = DMSerifDisplayRegular,
            style = MaterialTheme.typography.displayMedium
        )

        Text(
            text = "Take flight from paperwork.",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}