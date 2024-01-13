package com.smh.nxleave.design.component

import NX_Charcoal_20
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smh.nxleave.ui.theme.NXLeaveTheme

@Composable
fun SheetLip() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .background(NX_Charcoal_20, shape = RoundedCornerShape(percent = 100))
                .width(50.dp)
                .height(10.dp)
        )
    }
}

@Preview
@Composable
private fun SheetLipPreview() {
    NXLeaveTheme {
        Surface {
            SheetLip()
        }
    }
}