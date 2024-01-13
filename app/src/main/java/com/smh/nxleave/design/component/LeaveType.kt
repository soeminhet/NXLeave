package com.smh.nxleave.design.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smh.nxleave.ui.theme.NXLeaveTheme

@Composable
fun LeaveType(
    color: Color,
    label: String,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .background(color = color, shape = CircleShape)
                .size(20.dp)
        )

        Text(
            text = label
        )
    }
}

@Preview
@Composable
private fun LeaveTypePreview() {
    NXLeaveTheme {
        Surface {
            LeaveType(color = Color.Green, label = "Annual")
        }
    }
}