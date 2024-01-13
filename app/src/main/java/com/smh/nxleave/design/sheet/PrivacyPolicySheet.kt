package com.smh.nxleave.design.sheet

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.smh.nxleave.design.component.SheetLip
import com.smh.nxleave.ui.theme.NXLeaveTheme
import com.smh.nxleave.ui.theme.spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicySheet(
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
        PrivacyPolicyContent()
    }
}

@Composable
private fun PrivacyPolicyContent() {
    Scaffold(
        topBar = {
            SheetLip()
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .padding(horizontal = spacing.horizontalSpace),
            contentPadding = PaddingValues(vertical = spacing.space12)
        ) {
            item {
                Text(
                    text = "NXLeave is committed to protecting your privacy. This Privacy Policy explains how we collect, use, and share your personal information when you use the NXLeave mobile application.",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            item {
                Text(
                    text = "Information We Collect",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = spacing.space12)
                )
            }

            item {
                Text(
                    text = "We collect the following types of information:",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = spacing.space4)
                )
            }

            item {
                Text(
                    text = "• Personal Information: Information that identifies you personally, such as your name, email address, phone number, and employment information.",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            item {
                Text(
                    text = "• Device Information: Information about your device, such as your device ID, operating system, and IP address.",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            item {
                Text(
                    text = "• Usage Information: Information about how you use the NXLeave app, such as the dates and times you access the app, the features you use, and the leave requests you submit.",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            item {
                Text(
                    text = "How We Use Your Information",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = spacing.space12)
                )
            }

            item {
                Text(
                    text = "We use your information to:",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = spacing.space4)
                )
            }

            item {
                Text(
                    text = "• Provide you with the NXLeave app and its features.",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            item {
                Text(
                    text = "• Process your leave requests.",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            item {
                Text(
                    text = "• Communicate with you about your leave requests and other app-related matters.",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            item {
                Text(
                    text = "• Improve the NXLeave app and its features.",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            item {
                Text(
                    text = "• Personalize your experience in the NXLeave app.",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PrivacyPolicyPreview() {
    NXLeaveTheme {
        PrivacyPolicyContent()
    }
}