package com.smh.nxleave.design.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.smh.nxleave.design.sheet.Option
import com.smh.nxleave.design.sheet.OptionsSheet
import com.smh.nxleave.domain.model.ProjectModel
import com.smh.nxleave.ui.theme.spacing

@Composable
fun ProjectItem(
    model: ProjectModel,
    onEdit: (ProjectModel) -> Unit,
    onDisable: (ProjectModel) -> Unit,
    onEnable: (ProjectModel) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = remember(model.enable) {
        if (model.enable) listOf(Option.EDIT, Option.DISABLE)
        else listOf(Option.EDIT, Option.ENABLE)
    }
    val animateEnableDisableAlpha by animateFloatAsState(
        targetValue = if (model.enable) 1f else 0.5f,
        label = "EnableDisableAlpha"
    )
    var showOptionsSheet by remember { mutableStateOf(false) }

    if (showOptionsSheet) {
        OptionsSheet(
            options = options,
            onClick = {
                when(it) {
                    Option.EDIT -> onEdit(model)
                    Option.DISABLE -> onDisable(model.copy(enable = false))
                    Option.ENABLE -> onEnable(model.copy(enable = true))
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
        Text(
            text = model.name,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = LocalContentColor.current.copy(alpha = animateEnableDisableAlpha),
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    vertical = spacing.space12,
                    horizontal = spacing.horizontalSpace
                )
        )

        Divider()
    }
}