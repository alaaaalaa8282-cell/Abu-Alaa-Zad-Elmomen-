package com.abueltaweel.presentation.screen.calculation_method

import com.abueltaweel.presentation.screen.madhab.MadhabUiState

interface CalculationMethodInteractionListener {
    fun onCalculationMethodClicked(method: CalculationMethodUiState.CalculationMethod)
    fun onClickContinue()
}