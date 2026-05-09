package com.abueltaweel.presentation.screen.calculation_method

import com.abueltaweel.domain.repository.settings.SettingsRepository
import com.abueltaweel.presentation.base.BaseViewModel

class CalculationMethodViewModel (
    private val settingsRepository: SettingsRepository
):
    BaseViewModel<CalculationMethodUiState, CalculationMethodEffect>(CalculationMethodUiState()),
    CalculationMethodInteractionListener {


    override fun onCalculationMethodClicked(method: CalculationMethodUiState.CalculationMethod) {
        updateState { state ->
            state.copy(selectedMethod = method)
        }

    }

    override fun onClickContinue() {
        tryToCall(
            block = {
                settingsRepository.saveCalculationMethod(screenState.value.selectedMethod.toDomain())
            },
            onSuccess = {
                sendEffect(CalculationMethodEffect.NavigateToBatteryOptimizationScreen)
            },
            onError = {}
        )
    }

}