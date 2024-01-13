package com.smh.nxleave.design.sheet

import NX_Charcoal
import NX_Green
import NX_Red
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smh.nxleave.ui.theme.LocalSpacing
import com.smh.nxleave.ui.theme.NXLeaveTheme
import com.smh.nxleave.ui.theme.spacing

enum class Option(val color: Color) {
    EDIT(color = NX_Charcoal),
    DISABLE(color = NX_Red),
    ENABLE(color = NX_Green),
    DELETE(color = NX_Red),
    EMAIL(color = NX_Charcoal),
    PHONE(color = NX_Charcoal)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptionsSheet(
    options: List<Option>,
    onClick: (Option) -> Unit,
    onDismissRequest: () -> Unit
) {
    val skipPartiallyExpanded by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded)
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = bottomSheetState,
        windowInsets = WindowInsets(top = 0),
        dragHandle = null,
        tonalElevation = 0.dp,
        containerColor = Color.Transparent
    ) {
        OptionsContent(
            options = options,
            onCancel = onDismissRequest,
            onClick = onClick
        )
    }
}

@Composable
private fun OptionsContent(
    options: List<Option>,
    onCancel: () -> Unit,
    onClick: (Option) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.padding(horizontal = spacing.horizontalSpace),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background
            )
        ) {
            Column {
                options.forEachIndexed { index, option -> 
                    TextButton(
                        onClick = { onClick(option) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = option.color
                        )
                    ) {
                        Text(text = option.name)
                    }
                    
                    if (index != options.lastIndex) {
                        Divider()
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(LocalSpacing.current.space8))

        Button(
            onClick = onCancel,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.horizontalSpace)
        ) {
            Text(text = "CANCEL")
        }

        Spacer(modifier = Modifier.height(LocalSpacing.current.space20))
    }
}

@Preview
@Composable
private fun PickPhotoActionContentPreview() {
    NXLeaveTheme {
        OptionsContent(
            options = listOf(
                Option.EDIT,
                Option.DISABLE
            ),
            onCancel = {},
            onClick = {}
        )
    }
}