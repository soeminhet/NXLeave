package com.smh.nxleave.design.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.tooling.preview.Preview
import com.smh.nxleave.design.sheet.Option
import com.smh.nxleave.design.sheet.OptionsSheet
import com.smh.nxleave.domain.model.RoleModel
import com.smh.nxleave.ui.theme.NXLeaveTheme
import com.smh.nxleave.ui.theme.spacing

@Composable
fun <T> EditableLabelItem(
    label: String,
    enable: Boolean,
    model: T,
    onEdit: (T) -> Unit,
    onDisable: (T) -> Unit,
    onEnable: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = remember(enable) {
        if (enable) listOf(Option.EDIT, Option.DISABLE)
        else listOf(Option.EDIT, Option.ENABLE)
    }
    val animateEnableDisableAlpha by animateFloatAsState(
        targetValue = if (enable) 1f else 0.5f,
        label = "EnableDisableAlpha"
    )
    var showOptionsSheet by remember { mutableStateOf(false) }

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

    Column(modifier = modifier.clickable { showOptionsSheet = true }) {
        Text(
            text = label,
            modifier = Modifier
                .defaultMinSize(minHeight = spacing.space48)
                .padding(
                    vertical = spacing.space12,
                    horizontal = spacing.horizontalSpace
                ),
            color = LocalContentColor.current.copy(alpha = animateEnableDisableAlpha)
        )

        Divider()
    }
}

@Preview
@Composable
private fun EditableLabelItemPreview() {
    NXLeaveTheme {
        Surface {
            EditableLabelItem(
                label = "Label",
                model = RoleModel.projectManager,
                onEdit = {},
                onDisable = {},
                onEnable = {},
                enable = true
            )
        }
    }
}