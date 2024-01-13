package com.smh.nxleave.design.sheet

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
fun <T: Identifiable> SingleItemSelectableSheet(
    items: List<T>,
    selectedItem: T?,
    onClick: (T) -> Unit,
    onDismissRequest: () -> Unit,
    itemContent: @Composable (item: T) -> Unit
) {
    val skipPartiallyExpanded by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded)
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = bottomSheetState,
        windowInsets = WindowInsets(top = 0),
        dragHandle = null,
        modifier = Modifier.fillMaxHeight(fraction = spacing.sheetMaxFraction)
    ) {
        SheetLip()
        SingleItemSelectableContent(
            items = items,
            selectedItem = selectedItem,
            onClick = onClick,
            itemContent = itemContent
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T: Identifiable> SingleItemSelectableContent(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(bottom = spacing.sheetBottomSpace),
    items: List<T>,
    selectedItem: T?,
    onClick: (T) -> Unit,
    itemContent: @Composable (item: T) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding
    ) {
        itemsIndexed(
            items,
            key = { _, item -> item.id }
        ) { index, item ->
            Column(
                modifier = Modifier.animateItemPlacement()
            ) {
                Row(
                    modifier = Modifier
                        .clickable { onClick(item) }
                        .fillMaxSize()
                        .padding(horizontal = spacing.horizontalSpace),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    itemContent(item)

                    if (item.id == selectedItem?.id) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                if (index != items.lastIndex) {
                    Divider()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SingleItemSelectableSheetPreview() {
    NXLeaveTheme {
        Column {
            SheetLip()
            SingleItemSelectableContent(
                items = listOf(
                    RoleModel.androidDeveloper,
                    RoleModel.projectManager
                ),
                selectedItem = RoleModel.androidDeveloper,
                onClick = {},
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
}