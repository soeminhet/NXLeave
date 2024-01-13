package com.smh.nxleave.screen.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.smh.nxleave.R
import com.smh.nxleave.ui.theme.NXLeaveTheme
import com.smh.nxleave.ui.theme.spacing

@Composable
fun AccountSuspendContent(
    onLogout: () -> Unit
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.plant)
    )
    Surface {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            LottieAnimation(
                composition = composition,
                iterations = Int.MAX_VALUE,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f / 1f)
            )

            Text(
                text = "Account Disabled",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "Your employment account appears\nto be disabled.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onLogout,
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(bottom = spacing.sheetBottomSpace)
                    .padding(horizontal = spacing.horizontalSpace)
                    .fillMaxWidth()
            ) {
                Text(text = "LOGOUT")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AccountSuspendPreview() {
    NXLeaveTheme {
        AccountSuspendContent(
            onLogout = {}
        )
    }
}