// androidMain/.../ui/theme/SugarAppTheme.android.kt
package com.barkhatov.pulpsugarextractioncalculator.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = SugarDarkBlue,
    secondary = SugarBlue,
    tertiary = SugarLightBlue,
    background = SugarDarkBackground,
    surface = SugarDarkSurface
)

private val LightColorScheme = lightColorScheme(
    primary = SugarDarkBlue,
    secondary = SugarBlue,
    tertiary = SugarLightBlue,
    background = SugarLightBackground,
    surface = SugarLightSurface
)

// Android-специфічна реалізація теми
@Composable
actual fun SugarAppTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = androidx.compose.material3.Typography(
            bodyLarge = BodyLarge,
            titleLarge = TitleLarge
        ),
        content = content
    )
}