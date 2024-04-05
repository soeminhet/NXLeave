package com.smh.nxleave.design.sheet

import NX_Charcoal_20
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smh.nxleave.design.component.NXOutlinedTextField
import com.smh.nxleave.design.component.modifier.noRippleClick
import com.smh.nxleave.domain.model.AccessLevel
import com.smh.nxleave.domain.model.ProjectModel
import com.smh.nxleave.domain.model.RoleModel
import com.smh.nxleave.domain.model.StaffModel
import com.smh.nxleave.screen.model.StaffProfileUiModel
import com.smh.nxleave.ui.theme.NXLeaveTheme
import com.smh.nxleave.ui.theme.spacing
import com.smh.nxleave.utility.isEmail
import com.smh.nxleave.utility.isPassword

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageStaffSheet(
    staffModel: StaffProfileUiModel? = null,
    roles: List<RoleModel>,
    projects: List<ProjectModel>,
    onCreate: (StaffModel, String) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val skipPartiallyExpanded by remember { mutableStateOf(true) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded)
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = bottomSheetState,
        windowInsets = WindowInsets(top = 0),
        dragHandle = null,
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxHeight(fraction = spacing.sheetMaxFraction)
    ) {
        ManageStaffContent(
            staffModel = staffModel,
            roles = roles,
            projects = projects,
            onCreate = onCreate
        )
    }
}

@Composable
internal fun ManageStaffContent(
    staffModel: StaffProfileUiModel?,
    roles: List<RoleModel>,
    projects: List<ProjectModel>,
    onCreate: (StaffModel, String) -> Unit
) {
    var name by remember { mutableStateOf(staffModel?.name ?: "") }
    var email by remember { mutableStateOf(staffModel?.email ?:"") }
    var password by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf(staffModel?.phoneNumber ?:"") }
    var role by remember { mutableStateOf<RoleModel?>(null) }
    val selectedProjects = remember { mutableStateListOf<ProjectModel>() }

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
    val createEnable by remember {
        derivedStateOf {
            email.isNotBlank() && !emailError && name.isNotBlank() &&
                    phoneNumber.isNotBlank() && role != null && projects.isNotEmpty() &&
                    if (staffModel == null) password.isNotBlank() && !passwordError else true
        }
    }

    var showPassword by remember { mutableStateOf(false) }
    var showRolesSheet by remember { mutableStateOf(false) }
    var showProjectsSheet by remember { mutableStateOf(false) }

    if (showRolesSheet) {
        SingleItemSelectableSheet(
            items = roles,
            selectedItem = role,
            onClick = {
                role = it
                showRolesSheet = false
            },
            onDismissRequest = { showRolesSheet = false },
            itemContent = {
                Text(
                    it.name,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = spacing.space16)
                )
            }
        )
    }

    if (showProjectsSheet) {
        MultiItemSelectableSheet(
            items = projects,
            selectedItems = selectedProjects,
            onDone = { newProjects ->
                selectedProjects.removeAll{ true }
                selectedProjects.addAll(newProjects)
                showProjectsSheet = false
            },
            onDismissRequest = { showProjectsSheet = false },
            itemContent = {
                Text(
                    it.name,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = spacing.space16)
                )
            }
        )
    }

    LaunchedEffect(key1 = staffModel) {
        staffModel?.let {
            role = roles.firstOrNull { role -> role.id == it.roleId }
            it.currentProjectIds.forEach { projectId ->
                projects.firstOrNull { project -> project.id == projectId }?.let { project ->
                    selectedProjects.add(project)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            Column {
                Text(
                    text = if(staffModel == null) "Add New Staff" else "Edit Staff",
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
            Button(
                onClick = {
                    val currentProjectIds = if(role?.accessLevel == AccessLevel.All()) projects.map { it.id } else selectedProjects.map { it.id }
                    val staff = staffModel?.let {
                        StaffModel(
                            id = it.id,
                            roleId = role!!.id,
                            currentProjectIds = currentProjectIds,
                            name = name,
                            email = email,
                            photo = it.photo,
                            phoneNumber = phoneNumber,
                            enable = it.enable,
                        )
                    } ?: StaffModel(
                        id = "",
                        roleId = role!!.id,
                        currentProjectIds = currentProjectIds,
                        name = name,
                        email = email,
                        photo = "",
                        phoneNumber = phoneNumber,
                        enable = true,
                    )
                    onCreate(
                        staff,
                        password
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = spacing.space20)
                    .padding(bottom = spacing.sheetBottomSpace)
                    .padding(horizontal = spacing.horizontalSpace),
                enabled = createEnable
            ) {
                Text(text = if(staffModel == null) "CREATE" else "UPDATE")
            }
        },
        modifier = Modifier.navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(horizontal = spacing.horizontalSpace)
        ) {
            NXOutlinedTextField(
                value = name,
                onValueChange = { value -> name = value },
                label = "Name",
                modifier = Modifier.padding(top = spacing.space24),
            )

            NXOutlinedTextField(
                value = email,
                onValueChange = { value -> email = value },
                label = "Email",
                errorMsg = if (emailError) "Email invalid" else "",
                modifier = Modifier.padding(top = spacing.space12),
            )

            if (staffModel == null) {
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
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                )
            }

            NXOutlinedTextField(
                value = phoneNumber,
                onValueChange = { value -> phoneNumber = value },
                label = "PhoneNumber",
                modifier = Modifier.padding(top = spacing.space12),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            )

            NXOutlinedTextField(
                value = role?.name ?: "",
                onValueChange = {},
                label = "Role",
                readOnly = true,
                enabled = false,
                modifier = Modifier
                    .noRippleClick { showRolesSheet = true }
                    .padding(top = spacing.space12),
                colors = OutlinedTextFieldDefaults.colors(
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledTextColor = MaterialTheme.colorScheme.onSurface
                )
            )

            if (role?.accessLevel != AccessLevel.All()) {
                NXOutlinedTextField(
                    value = selectedProjects.joinToString { it.name },
                    onValueChange = {},
                    label = "Projects",
                    readOnly = true,
                    enabled = false,
                    modifier = Modifier
                        .noRippleClick { showProjectsSheet = true }
                        .padding(top = spacing.space12),
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledTextColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ManageStaffPreview() {
    NXLeaveTheme {
        ManageStaffContent(
            roles = emptyList(),
            projects = emptyList(),
            onCreate = { _, _  -> },
            staffModel = null
        )
    }
}