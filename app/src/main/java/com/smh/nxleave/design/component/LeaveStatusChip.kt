package com.smh.nxleave.design.component

import NX_Green
import NX_Red
import NX_Yellow
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.smh.nxleave.ui.theme.NXLeaveTheme

enum class LeaveStatus(val color: Color) {
    Pending(color = NX_Yellow),
    Approved(color = NX_Green),
    Rejected(color = NX_Red),
}

class PreviewParameterProvider : PreviewParameterProvider<LeaveStatus> {
    override val values: Sequence<LeaveStatus>
        get() = sequenceOf(
            LeaveStatus.Pending,
            LeaveStatus.Approved,
            LeaveStatus.Rejected
        )
}

@Composable
fun LeaveStatusChip(
    status: LeaveStatus,
    modifier: Modifier = Modifier
) {
    AssistChip(
        modifier = modifier,
        onClick = {},
        label = {
            Text(
                text = status.name,
                style = MaterialTheme.typography.labelSmall
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = status.color,
            labelColor = Color.White
        ),
        border = AssistChipDefaults.assistChipBorder(borderColor = status.color)
    )
}

@Preview(showBackground = true)
@Composable
private fun LeaveStatusChipPreview(@PreviewParameter(com.smh.nxleave.design.component.PreviewParameterProvider::class) status: LeaveStatus) {
    NXLeaveTheme {
        Box(modifier = Modifier.padding(20.dp)) {
            LeaveStatusChip(status = status)
        }
    }
}