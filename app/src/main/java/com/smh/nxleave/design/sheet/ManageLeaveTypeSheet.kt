package com.smh.nxleave.design.sheet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smh.nxleave.design.component.NXOutlinedTextField
import com.smh.nxleave.design.component.SheetLip
import com.smh.nxleave.domain.model.LeaveTypeModel
import com.smh.nxleave.ui.theme.NXLeaveTheme
import com.smh.nxleave.ui.theme.spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageLeaveTypeSheet(
    title: String,
    leaveTypeModel: LeaveTypeModel?,
    btnLabel: String,
    onSubmit: (String, Long) -> Unit,
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
        ManageLeaveTypeSheetContent(
            title = title,
            leaveTypeModel = leaveTypeModel,
            btnLabel = btnLabel,
            onSubmit = onSubmit
        )
    }
}

@Composable
private fun ManageLeaveTypeSheetContent(
    title: String,
    leaveTypeModel: LeaveTypeModel?,
    btnLabel: String,
    onSubmit: (String, Long) -> Unit,
) {
    var value by remember { mutableStateOf(leaveTypeModel?.name ?: "") }
    var selectedColor by remember { mutableStateOf(leaveTypeModel?.color) }
    val enable by remember {
        derivedStateOf {
            value.isNotBlank() && selectedColor != null &&
                    if (leaveTypeModel != null) value != leaveTypeModel.name || selectedColor != leaveTypeModel.color else true
        }
    }

    val colors = listOf(
        0xFFFF004D,
        0xFF0F1035,
        0xFF80BCBD,
        0xFF525CEB,
        0xFF76453B,
        0xFF607274,
        0xFF49108B,
        0xFFFF90BC,
        0xFF2B2A4C,
        0xFF164863,
        0xFF4F6F52,
        0xFF65B741
    )

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .fillMaxWidth()
            .padding(bottom = 40.dp)
            .padding(horizontal = spacing.horizontalSpace)
    ) {
        SheetLip()

        Text(
            text = title,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        NXOutlinedTextField(
            value = value,
            onValueChange = { value = it },
            modifier = Modifier.padding(top = spacing.space16),
            label = "Leave Type"
        )

        Text(
            text = "Color",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(top = spacing.space16),
        )

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 100.dp),
            horizontalArrangement = Arrangement.spacedBy(spacing.space12),
            verticalArrangement = Arrangement.spacedBy(spacing.space12),
            modifier = Modifier.padding(top = spacing.space6),
        ) {
            items(
                colors,
                key = { it }
            ) { color ->
                Box(
                    modifier = Modifier
                        .clickable { selectedColor = color }
                        .size(50.dp)
                        .background(
                            color = Color(color),
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (color == selectedColor) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Check",
                            tint = Color.White
                        )
                    }
                }
            }
        }

        Button(
            onClick = { onSubmit(value, selectedColor!!) },
            enabled = enable,
            modifier = Modifier
                .navigationBarsPadding()
                .fillMaxWidth()
                .padding(top = spacing.space16),
        ) {
            Text(text = btnLabel)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ManageLeaveTypeSheetPreview() {
    NXLeaveTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(0.1f))
            )

            ManageLeaveTypeSheetContent(
                title = "Title",
                btnLabel = "SUBMIT",
                onSubmit = { _, _ -> },
                leaveTypeModel = null
            )
        }
    }
}