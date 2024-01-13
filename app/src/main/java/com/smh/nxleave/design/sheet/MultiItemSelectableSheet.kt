package com.smh.nxleave.design.sheet

import NX_Charcoal_40
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smh.nxleave.design.component.SheetLip
import com.smh.nxleave.domain.model.Identifiable
import com.smh.nxleave.domain.model.RoleModel
import com.smh.nxleave.ui.theme.NXLeaveTheme
import com.smh.nxleave.ui.theme.spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T: Identifiable> MultiItemSelectableSheet(
    items: List<T>,
    selectedItems: List<T>,
    onDone: (List<T>) -> Unit,
    onDismissRequest: () -> Unit,
    itemContent: @Composable (item: T) -> Unit
) {
    val skipPartiallyExpanded by remember { mutableStateOf(true) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded)
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = bottomSheetState,
        windowInsets = WindowInsets(top = 0),
        dragHandle = null,
    ) {
        MultiItemSelectableContent(
            items = items,
            selectedItems = selectedItems,
            onDone = onDone,
            itemContent = itemContent
        )
    }
}

@Composable
private fun <T: Identifiable> MultiItemSelectableContent(
    items: List<T>,
    selectedItems: List<T>,
    onDone: (List<T>) -> Unit,
    itemContent: @Composable (item: T) -> Unit
) {
    var internalList by remember(selectedItems) { mutableStateOf(selectedItems) }
    val doneEnable by remember {
        derivedStateOf {
            internalList.isNotEmpty()
        }
    }

    Column(
        modifier = Modifier.navigationBarsPadding()
    ) {
        SheetLip()

        LazyColumn {
            itemsIndexed(
                items,
                key = { _, item -> item.id }
            ) { index, item ->
                Column {
                    Row(
                        modifier = Modifier
                            .clickable {
                                val mList = internalList.toMutableList()
                                val index = mList.indexOfFirst { item.id == it.id }
                                if (index == -1) mList.add(item)
                                else mList.remove(item)
                                Log.i("SELECTEDDDD", mList.toString())
                                internalList = mList
                            }
                            .fillMaxSize()
                            .padding(horizontal = spacing.horizontalSpace),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        itemContent(item)

                        if (internalList.contains(item)) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    if (index != items.lastIndex) {
                        Divider(color = NX_Charcoal_40)
                    }
                }
            }
        }

        Button(
            onClick = { onDone(internalList) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.horizontalSpace)
                .padding(bottom = spacing.sheetBottomSpace)
                .padding(top = spacing.space20),
            enabled = doneEnable
        ) {
            Text(text = "DONE")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MultiItemSelectableSheetPreview() {
    NXLeaveTheme {
        MultiItemSelectableContent(
            items = listOf(
                RoleModel.androidDeveloper,
                RoleModel.projectManager
            ),
            selectedItems = listOf(RoleModel.androidDeveloper),
            onDone = {},
            itemContent = {
                Text(
                    it.name,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = spacing.space12)
                )
            }
        )
    }
}