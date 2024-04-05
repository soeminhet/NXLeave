package com.smh.nxleave.design.sheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.smh.nxleave.ui.theme.NXLeaveTheme

@Composable
fun SheetPreview(
    content: @Composable BoxScope.() -> Unit,
    sheet: @Composable BoxScope.() -> Unit
) {
    NXLeaveTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            content()

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(0.1f))
            )

            sheet()
        }
    }
}