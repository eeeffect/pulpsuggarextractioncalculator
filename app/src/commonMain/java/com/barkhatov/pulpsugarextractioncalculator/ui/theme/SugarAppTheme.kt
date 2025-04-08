package com.barkhatov.pulpsugarextractioncalculator.ui.theme

import androidx.compose.runtime.Composable

@Composable
expect fun SugarAppTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
)