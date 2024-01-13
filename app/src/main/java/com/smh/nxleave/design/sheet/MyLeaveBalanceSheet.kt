package com.smh.nxleave.design.sheet

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smh.nxleave.design.component.LeaveType
import com.smh.nxleave.design.component.SheetLip
import com.smh.nxleave.screen.model.MyLeaveBalanceUiModel
import com.smh.nxleave.ui.theme.NXLeaveTheme
import com.smh.nxleave.utility.toDays
import com.smh.nxleave.utility.toIntOrDoubleString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyLeaveBalanceSheet(
    balances: List<MyLeaveBalanceUiModel>,
    onDismissRequest: () -> Unit
) {
    val skipPartiallyExpanded by remember { mutableStateOf(true) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded)
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = bottomSheetState,
        windowInsets = WindowInsets(top = 0),
        dragHandle = null,
    ) {
        MyLeaveBalanceSheetContent(balances = balances)
    }
}

@Composable
private fun MyLeaveBalanceSheetContent(
    balances: List<MyLeaveBalanceUiModel>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 40.dp)
            .navigationBarsPadding()
    ) {
        SheetLip()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Leave Type",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(3f)
            )

            Text(
                text = "Took",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.End,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "Total",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.End,
                modifier = Modifier.weight(1f)
            )
        }

        Divider(
            modifier = Modifier.padding(vertical = 20.dp)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(horizontal = 20.dp)
        ) {
            balances.forEach { balance ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    LeaveType(
                        color = balance.color,
                        label = balance.name,
                        modifier = Modifier.weight(3f)
                            .graphicsLayer {
                                alpha = if (balance.enable) 1f else 0.5f
                            }
                    )

                    Text(
                        text = balance.took.toIntOrDoubleString(),
                        textAlign = TextAlign.End,
                        modifier = Modifier.weight(1f)
                            .graphicsLayer {
                                alpha = if (balance.enable) 1f else 0.5f
                            }
                    )

                    Text(
                        text = balance.total.toIntOrDoubleString(),
                        textAlign = TextAlign.End,
                        modifier = Modifier.weight(1f)
                            .graphicsLayer {
                                alpha = if (balance.enable) 1f else 0.5f
                            }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MyLeaveBalanceSheetContentPreview() {
    NXLeaveTheme {
        Surface {
            MyLeaveBalanceSheetContent(
                balances = listOf(
                    MyLeaveBalanceUiModel.exampleEnable,
                    MyLeaveBalanceUiModel.exampleDisable
                )
            )
        }
    }
}