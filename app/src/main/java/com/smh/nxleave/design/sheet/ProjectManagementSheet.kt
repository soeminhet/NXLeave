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
import com.smh.nxleave.domain.model.ProjectModel
import com.smh.nxleave.ui.theme.NXLeaveTheme
import com.smh.nxleave.ui.theme.spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectManagementSheet(
    model: ProjectModel? = null,
    onSubmit: (ProjectModel) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val skipPartiallyExpanded by remember { mutableStateOf(true) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded)

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = bottomSheetState,
        windowInsets = WindowInsets(top = 0.dp),
        dragHandle = null,
        tonalElevation = 0.dp,
    ) {
        ProjectManagementContent(
            model = model,
            onSubmit = onSubmit
        )
    }
}

@Composable
internal fun ProjectManagementContent(
    model: ProjectModel?,
    onSubmit: (ProjectModel) -> Unit,
) {
    var name by remember { mutableStateOf(model?.name.orEmpty()) }
    val submitEnable by remember {
        derivedStateOf {
            name.isNotBlank()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(bottom = spacing.sheetBottomSpace)
            .padding(horizontal = spacing.horizontalSpace)
            .navigationBarsPadding()
    ) {
        SheetLip()

        Text(
            text = if (model == null) "Add Project" else "Edit Project",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        NXOutlinedTextField(
            value = name,
            onValueChange = { name = it },
            modifier = Modifier.padding(top = spacing.space16),
            label = "Name"
        )

        Button(
            onClick = {
                val newModel = model?.copy(
                    name = name,
                ) ?: ProjectModel(
                    id = "",
                    name = name,
                    enable = true
                )
                onSubmit(newModel)
            },
            modifier = Modifier
                .padding(top = spacing.space20)
                .fillMaxWidth(),
            enabled = submitEnable
        ) {
            Text(text = "SUBMIT")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProjectManagementPreview() {
    NXLeaveTheme {
        ProjectManagementContent(
            model = null,
            onSubmit = {}
        )
    }
}