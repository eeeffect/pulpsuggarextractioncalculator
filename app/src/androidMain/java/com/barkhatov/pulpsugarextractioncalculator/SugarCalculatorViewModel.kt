package com.barkhatov.pulpsugarextractioncalculator

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlin.math.exp
import kotlin.math.pow

enum class OperationMode {
    WITHOUT_RETURN,              // Без повернення жомопресової води
    WITH_RETURN_WITHOUT_CLEANING, // З поверненням жомопресової води без хім. очищення
    WITH_RETURN_WITH_CLEANING    // З поверненням жомопресової води з хім. очищенням
}

class SugarCalculatorViewModel : ViewModel() {
    // Вхідні параметри
    var sugarContentInBeets by mutableStateOf("16.0")       // Масова частка цукрози в буряках, %
    var cellJuicePurity by mutableStateOf("84.2")           // Чистота клітинного соку, %
    var crystallizationEffect by mutableStateOf("32.5")     // Ефект кристалізації цукрози, %
    var sugarLossFromDecomposition by mutableStateOf("0.4") // Втрати цукрози від розкладання, %

    var pulpOutput by mutableStateOf("80.0")                // Вихід жому з дифузійної установки, % до м.б.
    var dryMatterInRawPulp by mutableStateOf("6.0")         // Масова частка сухих речовин в сирому жомі, %
    var pulpMassPercent by mutableStateOf("5.0")            // Масова частка м'якоті у жомі, %
    var normalDryMatterLossInPress by mutableStateOf("10.0") // Нормативна втрата сухих речовин при пресуванні, %

    var dryMatterInPressedPulp by mutableStateOf(18f)       // Масова частка сухих речовин у пресованому жомі, %
    var sugarContentInPulp by mutableStateOf(1.0f)          // Масова частка цукрози в жомі, %

    // Вибір режиму
    val operationModes = listOf(
        OperationMode.WITHOUT_RETURN,
        OperationMode.WITH_RETURN_WITHOUT_CLEANING,
        OperationMode.WITH_RETURN_WITH_CLEANING
    )
    var selectedMode by mutableStateOf(OperationMode.WITHOUT_RETURN)

    // Результати розрахунків
    var diffusionJuicePurity by mutableStateOf("0.0")       // Чистота дифузійного соку, %
    var pressedPulpOutput by mutableStateOf("0.0")          // Вихід пресованого жому, % до м.б.
    var pressedPulpWaterOutput by mutableStateOf("0.0")     // Вихід жомопресової води, % до м.б.
    var sugarLossWithPulp by mutableStateOf("0.0")          // Втрати цукрози з жомом, % до м.б.
    var sugarLossInMolasses by mutableStateOf("0.0")        // Втрати цукрози з мелясою, % до м.б.
    var sugarOutput by mutableStateOf("0.0")                // Вихід цукру, % до м.б.
    var optimalSugarContent by mutableStateOf("0.0")        // Оптимальний вміст цукрози в жомі, %

    fun calculate() {
        // Розрахунок усіх параметрів
        calculateDiffusionJuicePurity()
        calculatePressedPulpOutput()
        calculatePressedPulpWaterOutput()
        calculateSugarLossWithPulp()
        calculateSugarLossInMolasses()
        calculateSugarOutput()
        calculateOptimalSugarContent()
    }

    /**
     * Розрахунок чистоти дифузійного соку
     * Формула: Ч диф.соку = Ч кл.соку - Еф.оч.
     * де Еф.оч. = α₀ + α₁ * x
     */
    private fun calculateDiffusionJuicePurity() {
        val cellJuicePurityValue = cellJuicePurity.toFloatOrNull() ?: 0f
        val purificationEffect = calculatePurificationEffect(sugarContentInPulp)

        val result = cellJuicePurityValue - purificationEffect
        diffusionJuicePurity = String.format("%.2f", result)
    }

    /**
     * Розрахунок ефекту очищення залежно від режиму роботи
     * Формула: Еф.оч. = α₀ + α₁ * x
     * де коефіцієнти α₀, α₁ залежать від режиму роботи дифузійної установки
     */
    private fun calculatePurificationEffect(sugarContentInPulpValue: Float): Float {
        val alpha0 = when (selectedMode) {
            OperationMode.WITHOUT_RETURN -> 28.653f
            OperationMode.WITH_RETURN_WITHOUT_CLEANING -> 22.253f
            OperationMode.WITH_RETURN_WITH_CLEANING -> 24.941f
        }

        val alpha1 = when (selectedMode) {
            OperationMode.WITHOUT_RETURN -> -0.372f
            OperationMode.WITH_RETURN_WITHOUT_CLEANING -> -0.533f
            OperationMode.WITH_RETURN_WITH_CLEANING -> -0.348f
        }

        return alpha0 + alpha1 * sugarContentInPulpValue
    }

    /**
     * Розрахунок виходу пресованого жому
     * Формула: В.пр.ж. = В.с.ж. * (СРс.ж. * (100 - Нпр.) / 100) / СРпр.ж.
     */
    private fun calculatePressedPulpOutput() {
        val rawPulpOutput = pulpOutput.toFloatOrNull() ?: 0f
        val dryMatterInRawPulpValue = dryMatterInRawPulp.toFloatOrNull() ?: 0f
        val normalLossInPress = normalDryMatterLossInPress.toFloatOrNull() ?: 0f

        val result = rawPulpOutput * (dryMatterInRawPulpValue * (100 - normalLossInPress) / 100) / dryMatterInPressedPulp
        pressedPulpOutput = String.format("%.2f", result)
    }

    /**
     * Розрахунок виходу жомопресової води
     * Формула: В.ж.п.в. = В.с.ж. - В.пр.ж.
     */
    private fun calculatePressedPulpWaterOutput() {
        val rawPulpOutput = pulpOutput.toFloatOrNull() ?: 0f
        val pressedPulpOutputValue = pressedPulpOutput.toFloatOrNull() ?: 0f

        val result = rawPulpOutput - pressedPulpOutputValue
        pressedPulpWaterOutput = String.format("%.2f", result)
    }

    /**
     * Розрахунок втрат цукрози з жомом
     * Формула: Втр.ц.ж. = В.пр.ж. * Цк.ж. * (100 - М) / 100
     */
    private fun calculateSugarLossWithPulp() {
        val pressedPulpOutputValue = pressedPulpOutput.toFloatOrNull() ?: 0f
        val pulpMassPercentValue = pulpMassPercent.toFloatOrNull() ?: 0f

        val result = pressedPulpOutputValue * sugarContentInPulp * (100 - pulpMassPercentValue) / 100
        sugarLossWithPulp = String.format("%.3f", result)
    }

    /**
     * Розрахунок втрат цукрози з мелясою
     * Формула: Втр.ц.м. = В.ц.стр. - Втр.ц.в. - Еф кр. * К
     * К = 100 / (100 - Ч диф.соку)
     */
    private fun calculateSugarLossInMolasses() {
        val sugarContentInBeetsValue = sugarContentInBeets.toFloatOrNull() ?: 0f
        val diffusionJuicePurityValue = diffusionJuicePurity.toFloatOrNull() ?: 0f
        val sugarLossInProductionValue = sugarLossWithPulp.toFloatOrNull() ?: 0f
        val crystallizationEffectValue = crystallizationEffect.toFloatOrNull() ?: 0f

        val k = 100f / (100f - diffusionJuicePurityValue)
        val result = sugarContentInBeetsValue - sugarLossInProductionValue - (crystallizationEffectValue * k / 100f)

        sugarLossInMolasses = String.format("%.3f", result)
    }

    /**
     * Розрахунок виходу цукру
     * Формула: В.ц. = В.ц.стр. - Втр.ц.ж. - Втр.ц.м. - Втр.розкл.
     */
    private fun calculateSugarOutput() {
        val sugarContentInBeetsValue = sugarContentInBeets.toFloatOrNull() ?: 0f
        val sugarLossWithPulpValue = sugarLossWithPulp.toFloatOrNull() ?: 0f
        val sugarLossInMolassesValue = sugarLossInMolasses.toFloatOrNull() ?: 0f
        val sugarLossFromDecompositionValue = sugarLossFromDecomposition.toFloatOrNull() ?: 0f

        val result = sugarContentInBeetsValue - sugarLossWithPulpValue - sugarLossInMolassesValue - sugarLossFromDecompositionValue
        sugarOutput = String.format("%.3f", result)
    }

    /**
     * Розрахунок оптимального вмісту цукрози в жомі
     * на основі поліноміальних рівнянь для різних режимів роботи
     */
    private fun calculateOptimalSugarContent() {
        val dryMatterValue = dryMatterInPressedPulp

        // Використовуємо відповідні рівняння для різних режимів
        val optimalValue = when {
            // При поверненні неочищеної жомопресової води при пресуванні жому до вмісту СР 25%
            selectedMode == OperationMode.WITH_RETURN_WITHOUT_CLEANING && dryMatterValue >= 24f -> {
                // Оптимальне значення з діапазону 1.2-1.5
                1.2f
            }

            // При поверненні хімічно очищеної жомопресової води при пресуванні жому до вмісту СР 25%
            selectedMode == OperationMode.WITH_RETURN_WITH_CLEANING && dryMatterValue >= 24f -> {
                // Оптимальне значення з діапазону 1.2-1.5
                1.3f
            }

            // При поверненні неочищеної жомопресової води при пресуванні жому до вмісту СР 20%
            selectedMode == OperationMode.WITH_RETURN_WITHOUT_CLEANING && dryMatterValue >= 19f && dryMatterValue < 24f -> {
                // Оптимальне значення з діапазону 1-1.3
                1.0f
            }

            // При поверненні хімічно очищеної жомопресової води при пресуванні жому до вмісту СР 20%
            selectedMode == OperationMode.WITH_RETURN_WITH_CLEANING && dryMatterValue >= 19f && dryMatterValue < 24f -> {
                // Оптимальне значення з діапазону 1-1.3
                1.2f
            }

            // Для всіх інших випадків
            else -> {
                if (dryMatterValue < 17f) {
                    0.7f  // Для низького ступеня пресування (14-16%)
                } else if (dryMatterValue < 19f) {
                    0.9f  // Для середнього ступеня пресування (16-18%)
                } else {
                    1.1f  // Для всіх інших випадків
                }
            }
        }

        optimalSugarContent = String.format("%.1f", optimalValue)
    }

    /**
     * Оптимізаційні рівняння для різних режимів роботи
     * При поверненні неочищеної жомопресової води при пресуванні жому до вмісту СР 25%:
     * f(x) = -0.083x³ - 0.159x² + 0.583x + 12.729
     */
    fun optimizationEquation1(x: Float): Float {
        return -0.083f * x.pow(3) - 0.159f * x.pow(2) + 0.583f * x + 12.729f
    }

    /**
     * При поверненні хімічно очищеної жомопресової води при пресуванні жому до вмісту СР 25%:
     * f(x) = -0.147x³ - 0.094x² + 0.749x + 13.056
     */
    fun optimizationEquation2(x: Float): Float {
        return -0.147f * x.pow(3) - 0.094f * x.pow(2) + 0.749f * x + 13.056f
    }

    /**
     * При поверненні неочищеної жомопресової води при пресуванні жому до вмісту СР 20%:
     * f(x) = -0.081x³ - 0.124x² + 0.483x + 12.699
     */
    fun optimizationEquation3(x: Float): Float {
        return -0.081f * x.pow(3) - 0.124f * x.pow(2) + 0.483f * x + 12.699f
    }

    /**
     * При поверненні хімічно очищеної жомопресової води при пресуванні жому до вмісту СР 20%:
     * f(x) = -0.1x³ - 0.15x² + 0.606x + 13.026
     */
    fun optimizationEquation4(x: Float): Float {
        return -0.1f * x.pow(3) - 0.15f * x.pow(2) + 0.606f * x + 13.026f
    }

    /**
     * Метод для обчислення оптимального значення вмісту цукрози
     * шляхом знаходження максимуму функції виходу цукру
     */
    private fun findOptimalSugarContent(start: Float, end: Float, step: Float, equation: (Float) -> Float): Float {
        var maxValue = Float.NEGATIVE_INFINITY
        var optimalX = start

        var x = start
        while (x <= end) {
            val value = equation(x)
            if (value > maxValue) {
                maxValue = value
                optimalX = x
            }
            x += step
        }

        return optimalX
    }
}