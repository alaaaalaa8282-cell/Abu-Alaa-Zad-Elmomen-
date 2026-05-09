package com.abueltaweel.presentation.screen.batteryOptimization

import com.abueltaweel.domain.repository.settings.BatteryOptimizationRepository
import com.abueltaweel.presentation.base.BaseViewModel

class BatteryOptimizationViewModel(
    private val batteryOptimizationRepository: BatteryOptimizationRepository
) :
    BaseViewModel<BatteryOptimizationUiState, BatteryOptimizationEffect>(
        BatteryOptimizationUiState()
    ), BatteryOptimizationInteractionListener {

    fun loadInstructions(manufacturer: String, isArabic: Boolean) {
        tryToCall(
            block = { batteryOptimizationRepository.getBrandInstructions(manufacturer, isArabic) },
            onSuccess = { instructions ->
                updateState { it.copy(instructions = instructions) }
            },
            onError = { e -> }
        )
    }

    override fun onOpenSettingsClicked() {
        sendEffect(BatteryOptimizationEffect.OpenSettings)
    }

    override fun onSkipForNowClicked() {
        sendEffect(BatteryOptimizationEffect.SkipForNow)
    }

    override fun onBackClicked() {
        sendEffect(BatteryOptimizationEffect.NavigateBack)
    }

    override fun onLearnMoreClick() {
        sendEffect(BatteryOptimizationEffect.NavigateToLearnMore)
    }
}