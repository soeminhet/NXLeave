package com.smh.nxleave.design.sheet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
fun <T: Identifiable> SearchSingleItemSelectableSheet(
    items: List<T>,
    selectedItem: T?,
    onClick: (T) -> Unit,
    onDismissRequest: () -> Unit,
    onFilter: (String, List<T>) -> List<T>,
    itemContent: @Composable (item: T) -> Unit
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
        SearchSingleItemSelectableContent(
            items = items,
            initSelectedItem = selectedItem,
            onClick = onClick,
            itemContent = itemContent,
            onFilter = onFilter
        )
    }
}

@Composable
private fun <T: Identifiable> SearchSingleItemSelectableContent(
    items: List<T>,
    initSelectedItem: T?,
    onClick: (T) -> Unit,
    onFilter: (String, List<T>) -> List<T>,
    itemContent: @Composable (item: T) -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    var selectedItem by remember { mutableStateOf(initSelectedItem) }
    val doneEnable by remember {
        derivedStateOf {
            selectedItem != initSelectedItem && selectedItem != null
        }
    }
    val filterItems by remember {
        derivedStateOf {
            onFilter(searchText, items)
        }
    }

    Scaffold(
        topBar = {
            Column {
                SheetLip()

                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing.horizontalSpace)
                        .padding(
                            bottom = spacing.space16,
                            top = spacing.space12
                        ),
                    shape = RoundedCornerShape(percent = 50),
                    placeholder = {
                        Text(
                            text = "Search",
                            color = LocalContentColor.current.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            modifier = Modifier.size(19.dp),
                            tint = LocalContentColor.current.copy(alpha = 0.7f)
                        )
                    },
                    textStyle = MaterialTheme.typography.bodyMedium,
                    singleLine = true
                )
            }
        },
        bottomBar = {
            Button(
                onClick = { selectedItem?.let(onClick) },
                enabled = doneEnable,
                modifier = Modifier
                    .navigationBarsPadding()
                    .fillMaxWidth()
                    .padding(horizontal = spacing.horizontalSpace)
                    .padding(bottom = spacing.sheetBottomSpace)
            ) {
                Text(text = "DONE")
            }
        }
    ) {
        SingleItemSelectableContent(
            items = filterItems,
            selectedItem = selectedItem,
            onClick = { selectedItem = it },
            itemContent = itemContent,
            modifier = Modifier
                .padding(it)
                .fillMaxWidth(),
            contentPadding = PaddingValues(bottom = spacing.space12)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchSingleItemSelectablePreview() {
    NXLeaveTheme {
        SearchSingleItemSelectableContent(
            items = listOf(
                RoleModel.androidDeveloper,
                RoleModel.projectManager
            ),
            initSelectedItem = RoleModel.androidDeveloper,
            onClick = {},
            onFilter = { _, _ -> listOf(
                RoleModel.androidDeveloper,
                RoleModel.projectManager
            ) },
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