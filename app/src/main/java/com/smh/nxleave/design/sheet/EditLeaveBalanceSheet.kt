package com.smh.nxleave.design.sheet

import NX_Charcoal_20
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.smh.nxleave.design.component.NXOutlinedTextField
import com.smh.nxleave.screen.model.LeaveBalanceUiModel
import com.smh.nxleave.ui.theme.NXLeaveTheme
import com.smh.nxleave.ui.theme.spacing
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditLeaveBalanceSheet(
    title: String,
    leaveBalanceList: List<LeaveBalanceUiModel>,
    onDismissRequest: () -> Unit,
    onSubmit: (List<LeaveBalanceUiModel>) -> Unit
) {
    val skipPartiallyExpanded by remember { mutableStateOf(true) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded)
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = bottomSheetState,
        windowInsets = WindowInsets(top = 0),
        dragHandle = null,
        modifier = Modifier.fillMaxHeight(fraction = spacing.sheetMaxFraction)
    ) {
        EditLeaveBalanceContent(
            title = title,
            leaveBalanceList = leaveBalanceList,
            onSubmit = onSubmit
        )
    }
}

@Composable
internal fun EditLeaveBalanceContent(
    title: String,
    leaveBalanceList: List<LeaveBalanceUiModel>,
    onSubmit: (List<LeaveBalanceUiModel>) -> Unit
) {
    var internalList by remember(leaveBalanceList) { mutableStateOf(leaveBalanceList) }

    Scaffold(
        topBar = {
            Column {
                Text(
                    text = "Edit $title",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing.horizontalSpace)
                        .padding(vertical = spacing.space12),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Divider(color = NX_Charcoal_20)
            }
        },
        bottomBar = {
            Column {
                Divider(color = NX_Charcoal_20)

                Button(
                    onClick = { onSubmit(internalList) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing.horizontalSpace)
                        .padding(bottom = spacing.sheetBottomSpace)
                        .padding(top = spacing.space12)
                ) {
                    Text(text = "SUBMIT")
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.navigationBarsPadding()
    ) {
        LazyColumn(
            modifier = Modifier.padding(it),
            contentPadding = PaddingValues(
                horizontal = spacing.horizontalSpace,
                vertical = spacing.space12
            ),
            verticalArrangement = Arrangement.spacedBy(spacing.space12)
        ) {
            itemsIndexed(
                internalList,
                key = { _, item -> item.id }
            ) { index, model ->
                NXOutlinedTextField(
                    value = model.balance,
                    onValueChange = { value ->
                        val mList = internalList.toMutableList()
                        mList[index] = model.copy(balance = value)
                        internalList = mList
                    },
                    label = "${model.leaveTypeName} (Days)",
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EditLeaveBalancePreview() {
    NXLeaveTheme {
        Surface {
            EditLeaveBalanceContent(
                title = "Android Developer",
                leaveBalanceList = listOf(
                    LeaveBalanceUiModel(
                        id = UUID.randomUUID().toString(),
                        roleId = UUID.randomUUID().toString(),
                        leaveTypeId = UUID.randomUUID().toString(),
                        leaveTypeName = "Android Developer",
                        balance = "100"
                    )
                ),
                onSubmit = {}
            )
        }
    }
}