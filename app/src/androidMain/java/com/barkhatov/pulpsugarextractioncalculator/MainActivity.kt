package com.barkhatov.pulpsuggarextractioncalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import com.barkhatov.pulpsugarextractioncalculator.ui.theme.SugarAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SugarAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SugarCalculatorApp()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SugarCalculatorApp(viewModel: SugarCalculatorViewModel = SugarCalculatorViewModel()) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Параметри", "Результати", "Графіки")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Калькулятор вмісту цукрози") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex])
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }

            when (selectedTabIndex) {
                0 -> ParametersTab(viewModel)
                1 -> ResultsTab(viewModel)
                2 -> GraphsTab(viewModel)
            }
        }
    }
}

@Composable
fun ParametersTab(viewModel: SugarCalculatorViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Вхідні параметри",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        // Секція: Вхідні параметри для розрахунку
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Загальні параметри",
                    fontWeight = FontWeight.Bold
                )

                InputField(
                    label = "Масова частка цукрози в буряках (%)",
                    value = viewModel.sugarContentInBeets,
                    onValueChange = { viewModel.sugarContentInBeets = it }
                )

                InputField(
                    label = "Чистота клітинного соку (%)",
                    value = viewModel.cellJuicePurity,
                    onValueChange = { viewModel.cellJuicePurity = it }
                )

                InputField(
                    label = "Ефект кристалізації цукрози (%)",
                    value = viewModel.crystallizationEffect,
                    onValueChange = { viewModel.crystallizationEffect = it }
                )

                InputField(
                    label = "Втрати цукрози від розкладання (%)",
                    value = viewModel.sugarLossFromDecomposition,
                    onValueChange = { viewModel.sugarLossFromDecomposition = it }
                )
            }
        }

        // Секція: Параметри жому
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Параметри жому",
                    fontWeight = FontWeight.Bold
                )

                InputField(
                    label = "Вихід жому з дифузійної установки (% до м.б.)",
                    value = viewModel.pulpOutput,
                    onValueChange = { viewModel.pulpOutput = it }
                )

                InputField(
                    label = "Масова частка сухих речовин в сирому жомі (%)",
                    value = viewModel.dryMatterInRawPulp,
                    onValueChange = { viewModel.dryMatterInRawPulp = it }
                )

                InputField(
                    label = "Масова частка м'якоті у жомі (%)",
                    value = viewModel.pulpMassPercent,
                    onValueChange = { viewModel.pulpMassPercent = it }
                )

                InputField(
                    label = "Нормативна втрата сухих речовин при пресуванні (%)",
                    value = viewModel.normalDryMatterLossInPress,
                    onValueChange = { viewModel.normalDryMatterLossInPress = it }
                )

                Text(
                    text = "Цільова масова частка сухих речовин у пресованому жомі (%)",
                    modifier = Modifier.padding(top = 8.dp)
                )

                Slider(
                    value = viewModel.dryMatterInPressedPulp,
                    onValueChange = { viewModel.dryMatterInPressedPulp = it },
                    valueRange = 14f..30f,
                    steps = 10
                )
                Text(
                    text = "Значення: ${viewModel.dryMatterInPressedPulp.toInt()}%",
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }

        // Режим роботи
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Режим роботи дифузійної установки",
                    fontWeight = FontWeight.Bold
                )

                viewModel.operationModes.forEach { mode ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = viewModel.selectedMode == mode,
                            onClick = { viewModel.selectedMode = mode }
                        )
                        Text(
                            text = when (mode) {
                                OperationMode.WITHOUT_RETURN -> "Без повернення жомопресової води"
                                OperationMode.WITH_RETURN_WITHOUT_CLEANING -> "З поверненням жомопресової води без очищення"
                                OperationMode.WITH_RETURN_WITH_CLEANING -> "З поверненням жомопресової води з хімічним очищенням"
                            }
                        )
                    }
                }
            }
        }

        // Масова частка цукрози в жомі
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Масова частка цукрози в жомі (%)",
                    fontWeight = FontWeight.Bold
                )

                Slider(
                    value = viewModel.sugarContentInPulp,
                    onValueChange = { viewModel.sugarContentInPulp = it },
                    valueRange = 0.2f..2.0f,
                    steps = 17
                )
                Text(
                    text = "Значення: ${String.format("%.1f", viewModel.sugarContentInPulp)}%",
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }

        Button(
            onClick = { viewModel.calculate() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Text("Розрахувати")
        }
    }
}

@Composable
fun ResultsTab(viewModel: SugarCalculatorViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Результати розрахунків",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ResultField(
                    label = "Чистота дифузійного соку (%)",
                    value = viewModel.diffusionJuicePurity
                )

                ResultField(
                    label = "Вихід пресованого жому (% до м.б.)",
                    value = viewModel.pressedPulpOutput
                )

                ResultField(
                    label = "Вихід жомопресової води (% до м.б.)",
                    value = viewModel.pressedPulpWaterOutput
                )

                ResultField(
                    label = "Втрати цукрози з жомом (% до м.б.)",
                    value = viewModel.sugarLossWithPulp
                )

                ResultField(
                    label = "Втрати цукрози з мелясою (% до м.б.)",
                    value = viewModel.sugarLossInMolasses
                )

                ResultField(
                    label = "Вихід цукру (% до м.б.)",
                    value = viewModel.sugarOutput
                )
            }
        }

        // Оптимальне значення
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Оптимальний вміст",
                    fontWeight = FontWeight.Bold
                )

                ResultField(
                    label = "Оптимальний вміст цукрози у жомі на виході з дифузійного апарату (%)",
                    value = viewModel.optimalSugarContent
                )

                Text(
                    text = "Відповідно до аналізу на основі режиму роботи дифузійної установки та ступеня пресування жому.",
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
fun GraphsTab(viewModel: SugarCalculatorViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Графіки залежностей",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        // Placeholder для графіків
        // Графік оптимізаційної функції для поточного режиму
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            val title = when (viewModel.selectedMode) {
                OperationMode.WITHOUT_RETURN -> "Вихід цукру без повернення жомопресової води"
                OperationMode.WITH_RETURN_WITHOUT_CLEANING -> "Вихід цукру з поверненням неочищеної води"
                OperationMode.WITH_RETURN_WITH_CLEANING -> "Вихід цукру з поверненням очищеної води"
            }

            val equation: (Float) -> Float = when {
                viewModel.selectedMode == OperationMode.WITH_RETURN_WITHOUT_CLEANING && viewModel.dryMatterInPressedPulp >= 24f ->
                    viewModel::optimizationEquation1
                viewModel.selectedMode == OperationMode.WITH_RETURN_WITH_CLEANING && viewModel.dryMatterInPressedPulp >= 24f ->
                    viewModel::optimizationEquation2
                viewModel.selectedMode == OperationMode.WITH_RETURN_WITHOUT_CLEANING && viewModel.dryMatterInPressedPulp >= 19f ->
                    viewModel::optimizationEquation3
                viewModel.selectedMode == OperationMode.WITH_RETURN_WITH_CLEANING && viewModel.dryMatterInPressedPulp >= 19f ->
                    viewModel::optimizationEquation4
                else ->
                    { x -> 12.5f + 0.3f * (x - 1f) * (x - 1f) }
            }

            SugarOutputChart(
                equation = equation,
                title = title
            )
        }

        // Порівняльний графік різних режимів
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            ComparativeChart(viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = { onValueChange(it) },
        label = { Text(label) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun ResultField(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            fontWeight = FontWeight.Bold
        )
    }
}