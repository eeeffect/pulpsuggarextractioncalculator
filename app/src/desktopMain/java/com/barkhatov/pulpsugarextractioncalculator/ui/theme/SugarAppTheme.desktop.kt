// desktopMain/.../ui/theme/SugarAppTheme.desktop.kt
package com.barkhatov.pulpsugarextractioncalculator.ui.theme

import androidx.compose.runtime.Composable
import org.jetbrains.compose.material3.Typography
import org.jetbrains.compose.material3.MaterialTheme
import org.jetbrains.compose.material3.darkColorScheme
import org.jetbrains.compose.material3.lightColorScheme

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

// Desktop-специфічна реалізація теми
@Composable
actual fun SugarAppTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(
            bodyLarge = BodyLarge,
            titleLarge = TitleLarge
        ),
        content = content
    )
}