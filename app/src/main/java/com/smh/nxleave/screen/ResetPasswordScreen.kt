package com.smh.nxleave.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smh.nxleave.design.component.NXAlertDialog
import com.smh.nxleave.design.component.NXBackButton
import com.smh.nxleave.design.component.NXLoading
import com.smh.nxleave.design.component.NXOutlinedTextField
import com.smh.nxleave.ui.theme.DMSerifDisplayRegular
import com.smh.nxleave.ui.theme.NXLeaveTheme
import com.smh.nxleave.ui.theme.spacing
import com.smh.nxleave.utility.isEmail
import com.smh.nxleave.viewmodel.ResetPasswordUiEvent
import com.smh.nxleave.viewmodel.ResetPasswordViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ResetPasswordScreen(
    onBack: () -> Unit,
    viewModel: ResetPasswordViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showSuccess by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(key1 = Unit) {
        viewModel.uiEvent.collectLatest {
            when(it) {
                is ResetPasswordUiEvent.Error -> {
                    errorMessage = it.message
                }
                ResetPasswordUiEvent.ResetSuccess -> {
                    showSuccess = true
                }
            }
        }
    }

    if (showSuccess) {
        NXAlertDialog(
            title = "RESET SUCCESS",
            body = "Email has been sent to your account",
            confirmButton = {
                onBack()
                showSuccess = false
            }
        )
    }

    if (errorMessage.isNotEmpty()) {
        NXAlertDialog(
            title = "SORRY!",
            body = errorMessage,
            confirmButton = {
                errorMessage = ""
            }
        )
    }

    if (uiState.loading) NXLoading()

    ResetPasswordContent(
        userEvent = {
            when(it) {
                ResetPasswordUserEvent.OnBack -> onBack()
                is ResetPasswordUserEvent.OnRest -> {
                    viewModel.resetPassword(it.email)
                }
            }
        }
    )
}

@Composable
private fun ResetPasswordContent(
    userEvent: (ResetPasswordUserEvent) -> Unit
) {
    var email by remember { mutableStateOf("") }
    val emailError by remember {
        derivedStateOf {
            email.isNotBlank() && !email.isEmail()
        }
    }
    val clickEnable by remember {
        derivedStateOf {
            email.isNotBlank() && !emailError
        }
    }

    Scaffold(
        topBar = {
            NXBackButton(
                onBack = { userEvent(ResetPasswordUserEvent.OnBack) },
                modifier = Modifier
                    .padding(start = spacing.space10)
                    .statusBarsPadding()
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .padding(horizontal = spacing.horizontalSpace),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Forget Password?",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                fontFamily = DMSerifDisplayRegular
            )

            Text(
                text = "No worries, we'll send you reset instructions",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(top = spacing.space8)
            )

            NXOutlinedTextField(
                value = email,
                onValueChange = { value -> email = value },
                label = "Enter your email",
                topLabel = true,
                modifier = Modifier.padding(top = spacing.space28),
                errorMsg = if (emailError) "Email invalid" else "",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Done)
            )

            Button(
                onClick = { userEvent(ResetPasswordUserEvent.OnRest(email)) },
                modifier = Modifier.padding(top = spacing.space16),
                enabled = clickEnable
            ) {
                Text(
                    text = "RESET PASSWORD",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

sealed interface ResetPasswordUserEvent {
    data object OnBack: ResetPasswordUserEvent
    data class OnRest(val email: String): ResetPasswordUserEvent
}

@Preview(showBackground = true)
@Composable
private fun ResetPasswordPreview() {
    NXLeaveTheme {
        ResetPasswordContent(
            userEvent = {}
        )
    }
}