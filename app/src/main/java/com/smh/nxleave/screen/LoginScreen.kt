package com.smh.nxleave.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.rpc.context.AttributeContext.Auth
import com.ramcosta.composedestinations.annotation.Destination
import com.smh.nxleave.design.component.NXAlertDialog
import com.smh.nxleave.design.component.NXBackButton
import com.smh.nxleave.design.component.NXLoading
import com.smh.nxleave.design.component.NXOutlinedTextField
import com.smh.nxleave.design.component.NXTitleAndSlogan
import com.smh.nxleave.domain.model.AuthUserModel
import com.smh.nxleave.ui.theme.DMSerifDisplayRegular
import com.smh.nxleave.ui.theme.NXLeaveTheme
import com.smh.nxleave.ui.theme.spacing
import com.smh.nxleave.utility.isEmail
import com.smh.nxleave.utility.isPassword
import com.smh.nxleave.viewmodel.LoginUiEvent
import com.smh.nxleave.viewmodel.LoginUiState
import com.smh.nxleave.viewmodel.LoginViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onBack: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(key1 = Unit) {
        viewModel.uiEvent.collectLatest { 
            when(it) {
                is LoginUiEvent.Error -> { errorMessage = it.message }
            }
        }
    }

    errorMessage?.let {
        NXAlertDialog(body = it, confirmButton = { errorMessage = null })
    }

    if (uiState.loading) NXLoading()

    LoginContent(
        uiState = uiState,
        userEvent = {
            when(it) {
                LoginUserEvent.OnBack -> {
                    onBack()
                }
                is LoginUserEvent.OnLogin -> {
                    viewModel.loginAccount(it.email, it.password)
                }
            }
        }
    )
}

@Composable
private fun LoginContent(
    uiState: LoginUiState,
    userEvent: (LoginUserEvent) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    val emailError by remember {
        derivedStateOf {
            email.isNotBlank() && !email.isEmail()
        }
    }
    val passwordError by remember {
        derivedStateOf {
            password.isNotBlank() && !password.isPassword()
        }
    }
    val clickEnable by remember {
        derivedStateOf {
            email.isNotBlank() && password.isNotBlank() && !emailError && !passwordError
        }
    }

    Scaffold(
        topBar = {
            NXBackButton(
                onBack = { userEvent(LoginUserEvent.OnBack) },
                modifier = Modifier
                    .padding(start = spacing.space10)
                    .statusBarsPadding()
            )
        }
    ){
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .padding(horizontal = spacing.horizontalSpace),
            verticalArrangement = Arrangement.spacedBy(spacing.space32)
        ) {
            NXTitleAndSlogan(
                modifier = Modifier.fillMaxWidth()
            )

            Column {
                NXOutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email",
                    modifier = Modifier.fillMaxWidth(),
                    errorMsg = if (emailError) "Email invalid" else "",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                )

                NXOutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Password",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = spacing.space12),
                    errorMsg = if (passwordError) "Password must be at least 8 letters and include uppercase, lowercase, non-alphanumeric letter." else "",
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = ""
                            )
                        }
                    },
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                )

                Button(
                    onClick = { userEvent(LoginUserEvent.OnLogin(email, password)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = spacing.space20),
                    enabled = clickEnable
                ) {
                    Text(text = "LOGIN")
                }
            }
        }
    }
}

sealed interface LoginUserEvent {
    data object OnBack: LoginUserEvent
    data class OnLogin(val email: String, val password: String): LoginUserEvent
}

@Preview(showBackground = true)
@Composable
private fun LoginPreview() {
    NXLeaveTheme {
        LoginContent(
            uiState = LoginUiState(),
            userEvent = {}
        )
    }
}