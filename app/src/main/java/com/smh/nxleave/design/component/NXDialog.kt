package com.smh.nxleave.design.component

import NX_Charcoal_80
import NX_Grey
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.smh.nxleave.design.component.modifier.bounceClick
import com.smh.nxleave.design.component.modifier.noRippleClick
import com.smh.nxleave.ui.theme.LocalSpacing
import com.smh.nxleave.ui.theme.NXLeaveTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NXAlertDialog(
    title: String = "",
    body: String,
    onDismissRequest: () -> Unit = {},
    confirmButtonText: String = "OK",
    confirmButton: () -> Unit,
    dismissButtonText: String= "Cancel",
    dismissButton: (() -> Unit)? = null,
    properties: DialogProperties = DialogProperties()
) {
    AlertDialog(onDismissRequest = onDismissRequest, properties = properties) {
        Surface(
            color = MaterialTheme.colorScheme.background,
            shape = MaterialTheme.shapes.large,
            modifier = Modifier
                .border(
                    width = 0.5.dp,
                    color = NX_Charcoal_80,
                    shape = MaterialTheme.shapes.large
                )
        ) {
            Column {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = LocalSpacing.current.space24)
                        .padding(top = LocalSpacing.current.space20)
                        .padding(bottom = LocalSpacing.current.space16),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(LocalSpacing.current.space4)
                ) {
                    if (title.isNotBlank()) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                    Text(
                        text = body,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
                Divider()
                Row(
                    modifier = Modifier.height(44.dp)
                ) {
                    if (dismissButtonText.isNotEmpty() && dismissButton != null) {
                        TextButton(
                            modifier = Modifier
                                .bounceClick()
                                .weight(1f),
                            onClick = dismissButton
                        ) {
                            Text(dismissButtonText)
                        }
                        Divider(
                            modifier = Modifier
                                .height(44.dp)
                                .width(1.dp)
                        )
                    }

                    TextButton(
                        modifier = Modifier
                            .bounceClick()
                            .weight(1f),
                        onClick = confirmButton
                    ) {
                        Text(confirmButtonText)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NXAlertDialogPreview() {
    NXLeaveTheme {
        NXAlertDialog(
            title = "Login in manually first",
            body = "You need to login with your username and password first to setup Touch ID.",
            onDismissRequest = { },
            confirmButtonText = "OK",
            confirmButton = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NXAlertDialogBodyOnlyPreview() {
    NXLeaveTheme {
        NXAlertDialog(
            body = "You need to login with your username and password first to setup Touch ID.",
            onDismissRequest = { },
            confirmButtonText = "OK",
            confirmButton = { }
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun NXAlertDialogBothButtonPreview() {
    NXLeaveTheme {
        NXAlertDialog(
            title = "Login in manually first",
            body = "You need to login with your username and password first to setup Touch ID.",
            onDismissRequest = { },
            confirmButtonText = "OK",
            dismissButtonText = "Cancel",
            confirmButton = {},
            dismissButton = {},

            )
    }
}