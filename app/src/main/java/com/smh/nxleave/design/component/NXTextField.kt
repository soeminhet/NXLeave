package com.smh.nxleave.design.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.smh.nxleave.ui.theme.NXLeaveTheme

@Composable
fun NXOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "",
    placeHolder: String = "",
    enabled: Boolean = true,
    readOnly: Boolean = false,
    topLabel: Boolean = false,
    minLines: Int = 1,
    errorMsg: String = "",
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(),
    textAlign: TextAlign = TextAlign.Start
) {
    Column(modifier = modifier.animateContentSize()) {
        if (topLabel && label.isNotBlank()) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium
            )
        }
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(text = placeHolder) },
            label = { if (!topLabel) Text(text = label) },
            readOnly = readOnly,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            minLines = minLines,
            isError = errorMsg.isNotBlank(),
            visualTransformation = visualTransformation,
            trailingIcon = trailingIcon,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            colors = colors,
            textStyle = LocalTextStyle.current.copy(textAlign = textAlign)
        )
        if (errorMsg.isNotBlank()) {
            Text(
                text = errorMsg,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NXOutlinedTextFieldPreview() {
    NXLeaveTheme {
        NXOutlinedTextField(
            value = "",
            onValueChange = {},
            label = "Label",
            errorMsg = "Error Message"
        )
    }
}