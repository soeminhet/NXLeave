package com.smh.nxleave.design.component

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smh.nxleave.design.sheet.Option
import com.smh.nxleave.design.sheet.OptionsSheet
import com.smh.nxleave.domain.model.LeaveTypeModel
import com.smh.nxleave.ui.theme.NXLeaveTheme
import com.smh.nxleave.ui.theme.spacing

@Composable
fun LeaveTypeItem(
    model: LeaveTypeModel,
    onEdit: (LeaveTypeModel) -> Unit,
    onDisable: (LeaveTypeModel) -> Unit,
    onEnable: (LeaveTypeModel) -> Unit,
    modifier: Modifier = Modifier
) {
    val animateEnableDisableAlpha by animateFloatAsState(
        targetValue = if (model.enable) 1f else 0.5f,
        label = "EnableDisableAlpha"
    )
    var showOptionsSheet by remember { mutableStateOf(false) }
    val options = remember(model.enable) {
        if (model.enable) listOf(Option.EDIT, Option.DISABLE)
        else listOf(Option.EDIT, Option.ENABLE)
    }

    if (showOptionsSheet) {
        OptionsSheet(
            options = options,
            onClick = {
                when(it) {
                    Option.EDIT -> onEdit(model)
                    Option.DISABLE -> onDisable(model)
                    Option.ENABLE -> onEnable(model)
                    else -> {}
                }
                showOptionsSheet = false
            },
            onDismissRequest = { showOptionsSheet = false }
        )
    }

    Column(
        modifier = modifier.clickable { showOptionsSheet = true }
    ) {
        Row(
            modifier = Modifier
                .animateContentSize()
                .fillMaxWidth()
                .defaultMinSize(minHeight = spacing.space48)
                .padding(
                    start = spacing.horizontalSpace,
                    end = spacing.space10
                ),
        ) {
            Box(
                modifier = Modifier
                    .padding(top = spacing.space8)
                    .size(30.dp)
                    .background(
                        color = Color(model.color).copy(animateEnableDisableAlpha),
                        shape = CircleShape
                    )
            )

            Text(
                text = model.name,
                modifier = Modifier
                    .weight(1f)
                    .padding(
                        start = spacing.space12,
                        top = spacing.space12,
                        bottom = spacing.space12
                    ),
                color = LocalContentColor.current.copy(alpha = animateEnableDisableAlpha)
            )
        }

        Divider()
    }
}

@Preview
@Composable
private fun LeaveTypeItemPreview() {
    NXLeaveTheme {
        Surface {
            LeaveTypeItem(
                model = LeaveTypeModel.annualLeave,
                onEdit = {},
                onDisable = {},
                onEnable = {}
            )
        }
    }
}