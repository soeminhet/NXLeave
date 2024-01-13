package com.smh.nxleave.ui.theme

import NX_Black
import NX_BlackVariant
import NX_Blue
import NX_Blue_5
import NX_Charcoal
import NX_Charcoal_40
import NX_Charcoal_60
import NX_DeepRed
import NX_Grey
import NX_Mono_0
import NX_Mono_20
import NX_Red
import NX_Warm_Grey_25
import NX_White
import NX_White_Grey
import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = NX_Black,
    onPrimary = NX_White,
    primaryContainer = NX_White,
    onPrimaryContainer = NX_Black,
    secondary = NX_Blue,
    onSecondary = NX_BlackVariant,
    secondaryContainer = NX_Blue_5,
    onSecondaryContainer = NX_BlackVariant,
    tertiary = NX_Grey,
    onTertiary = NX_White,
    tertiaryContainer = NX_Warm_Grey_25,
    onTertiaryContainer = NX_BlackVariant,
    error = NX_Red,
    errorContainer = NX_White,
    onError = NX_White,
    onErrorContainer = NX_DeepRed,

    background = NX_White,
    onBackground = NX_Charcoal,
    onSurface = NX_Charcoal, // Text on Card
    surfaceVariant = NX_White_Grey, // Card
    surface = NX_White_Grey,
    onSurfaceVariant = NX_BlackVariant,

    outline = NX_Charcoal_40,
    outlineVariant = NX_Mono_20,

    inverseSurface = NX_Charcoal,
    inverseOnSurface = NX_Mono_0,
    inversePrimary = NX_Charcoal_60,
    scrim = NX_Charcoal,
)


@Composable
fun NXLeaveTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}