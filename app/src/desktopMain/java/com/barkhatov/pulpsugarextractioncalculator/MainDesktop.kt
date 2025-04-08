package com.barkhatov.pulpsugarextractioncalculator

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.barkhatov.pulpsugarextractioncalculator.ui.theme.SugarAppTheme

fun main() = application {
    val windowState = rememberWindowState()

    Window(
        onCloseRequest = ::exitApplication,
        title = "Розрахунок вмісту цукрози у буряковій стружці",
        state = windowState
    ) {
        val viewModel = remember { SugarCalculatorViewModel() }

        SugarAppTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                SugarCalculatorApp(viewModel)
            }
        }
    }
}