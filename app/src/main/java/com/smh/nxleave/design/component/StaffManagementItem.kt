package com.smh.nxleave.design.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.smh.nxleave.R
import com.smh.nxleave.design.component.modifier.noRippleClick
import com.smh.nxleave.design.sheet.Option
import com.smh.nxleave.design.sheet.OptionsSheet
import com.smh.nxleave.screen.model.StaffProfileUiModel
import com.smh.nxleave.ui.theme.spacing

@Composable
fun StaffManagementItem(
    staff: StaffProfileUiModel,
    onEdit: (StaffProfileUiModel) -> Unit,
    onDisable: (StaffProfileUiModel) -> Unit,
    onEnable: (StaffProfileUiModel) -> Unit,
) {
    val options = remember(staff.enable) {
        if (staff.enable) listOf(Option.EDIT, Option.DISABLE)
        else listOf(Option.EDIT, Option.ENABLE)
    }
    val animateEnableDisableAlpha by animateFloatAsState(
        targetValue = if (staff.enable) 1f else 0.5f,
        label = "EnableDisableAlpha"
    )
    var showOptionsSheet by remember { mutableStateOf(false) }

    if (showOptionsSheet) {
        OptionsSheet(
            options = options,
            onClick = {
                when(it) {
                    Option.EDIT -> onEdit(staff)
                    Option.DISABLE -> onDisable(staff)
                    Option.ENABLE -> onEnable(staff)
                    else -> {}
                }
                showOptionsSheet = false
            },
            onDismissRequest = { showOptionsSheet = false }
        )
    }

    Column(
        modifier = Modifier
            .noRippleClick { showOptionsSheet = true }
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(spacing.space12),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = spacing.horizontalSpace)
                .padding(bottom = spacing.space12)
        ) {
            AsyncImage(
                model = staff.photo,
                contentDescription = "ProfilePicture",
                placeholder = painterResource(id = R.drawable.placeholder_default),
                error = painterResource(id = R.drawable.placeholder_default),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .alpha(animateEnableDisableAlpha)
            )

            Column {
                Text(
                    text = staff.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = LocalContentColor.current.copy(alpha = animateEnableDisableAlpha)
                )

                Text(
                    text = staff.role,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = LocalContentColor.current.copy(alpha = animateEnableDisableAlpha)
                )
            }
        }

        Divider()
    }
}