package com.smh.nxleave.design.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smh.nxleave.domain.model.LeaveTypeModel
import com.smh.nxleave.domain.model.RoleModel
import com.smh.nxleave.ui.theme.NXLeaveTheme

@Composable
fun ShowLeaveItem(
    modifier: Modifier = Modifier,
    status: LeaveStatus,
    title: String,
    dates: String,
    role: RoleModel? = null,
    leaveType: LeaveTypeModel,
) {
    Card(modifier = modifier) {
        Row(
            modifier = Modifier
                .height(intrinsicSize = IntrinsicSize.Min)
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(8.dp)
                    .background(status.color)
            )

            Column(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .padding(vertical = 12.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Date",
                        modifier = Modifier.size(16.dp),
                        tint = status.color
                    )

                    Text(
                        text = dates,
                        style = MaterialTheme.typography.bodyMedium,
                        color = status.color
                    )
                }

                if (role != null) {
                    Text(
                        text = role.name,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Text(
                    text = leaveType.name,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            LeaveStatusChip(
                status = status,
                modifier = Modifier.padding(
                    end = 8.dp,
                    top = 4.dp
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ShowLeaveItemPreview() {
    NXLeaveTheme {
        Box(modifier = Modifier.padding(20.dp)) {
            ShowLeaveItem(
                status = LeaveStatus.entries.random(),
                title = "Title",
                dates = "12 Dec 2023 - 15 Dec 2023",
                role = RoleModel.androidDeveloper,
                leaveType = LeaveTypeModel.annualLeave
            )
        }
    }
}