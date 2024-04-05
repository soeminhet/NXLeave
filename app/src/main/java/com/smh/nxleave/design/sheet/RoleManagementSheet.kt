package com.smh.nxleave.design.sheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smh.nxleave.design.component.NXOutlinedTextField
import com.smh.nxleave.design.component.SheetLip
import com.smh.nxleave.design.component.modifier.noRippleClick
import com.smh.nxleave.domain.model.AccessLevel
import com.smh.nxleave.domain.model.RoleModel
import com.smh.nxleave.ui.theme.NXLeaveTheme
import com.smh.nxleave.ui.theme.spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleManagementSheet(
    model: RoleModel? = null,
    onSubmit: (RoleModel) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val skipPartiallyExpanded by remember { mutableStateOf(true) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded)
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = bottomSheetState,
        windowInsets = WindowInsets(top = 0),
        dragHandle = null,
        containerColor = MaterialTheme.colorScheme.background
    ) {
        RoleManagementSheetContent(
            model = model,
            onSubmit = onSubmit,
        )
    }
}

@Composable
internal fun RoleManagementSheetContent(
    model: RoleModel?,
    onSubmit: (RoleModel) -> Unit,
) {
    var name by remember { mutableStateOf(model?.name.orEmpty()) }
    var level by remember { mutableStateOf(model?.accessLevel) }
    var showLevelSheet by remember { mutableStateOf(false) }

    val enable by remember {
        derivedStateOf {
            name.isNotBlank() && level != null
        }
    }

    if (showLevelSheet) {
        SingleItemSelectableSheet(
            items = AccessLevel.list,
            selectedItem = level,
            onClick = {
                level = it
                showLevelSheet = false
            },
            onDismissRequest = { showLevelSheet = false },
            itemContent = {
                Text(
                    it.name,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = spacing.space12)
                )
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(bottom = 40.dp)
            .padding(horizontal = spacing.horizontalSpace),
    ) {
        SheetLip()

        Text(
            text = if (model == null) "ADD NEW ROLE" else "EDIT ROLE",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        NXOutlinedTextField(
            value = name,
            onValueChange = { name = it },
            modifier = Modifier.padding(top = spacing.space12),
            label = "RoleName"
        )

        NXOutlinedTextField(
            label = "Level",
            value = level?.name.orEmpty(),
            onValueChange = {},
            topLabel = true,
            readOnly = true,
            enabled = false,
            modifier = Modifier
                .padding(top = spacing.space16)
                .noRippleClick { showLevelSheet = true },
            colors = OutlinedTextFieldDefaults.colors(
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                disabledTextColor = MaterialTheme.colorScheme.onSurface
            ),
        )

        Button(
            onClick = {
                val newModel = model?.copy(
                    name = name,
                    accessLevel = level!!
                ) ?: RoleModel(
                    id = "",
                    name = name,
                    accessLevel = level!!,
                    enable = true
                )
                onSubmit(newModel)
            },
            enabled = enable,
            modifier = Modifier
                .navigationBarsPadding()
                .fillMaxWidth()
                .padding(top = spacing.space12),
        ) {
            Text(text = if (model == null) "SUBMIT" else "UPDATE")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RoleManagementSheetPreview() {
    NXLeaveTheme {
        Surface {
            RoleManagementSheetContent(
                model = RoleModel.projectManager,
                onSubmit = {},
            )
        }
    }
}