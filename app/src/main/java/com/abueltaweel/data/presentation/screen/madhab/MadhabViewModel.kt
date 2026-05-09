package com.abueltaweel.presentation.screen.madhab

import com.abueltaweel.domain.repository.settings.SettingsRepository
import com.abueltaweel.presentation.base.BaseViewModel

class MadhabViewModel(
    private val settingsRepository: SettingsRepository
) : BaseViewModel<MadhabUiState, MadhabEffect>(MadhabUiState()),
    MadhabInteractionListener {
    override fun onMadhabClicked(madhab: MadhabUiState.MadhabState) {
        updateState { state ->
            state.copy(selectedMadhab = madhab)
        }
    }



    override fun onClickContinue() {
        tryToCall(
            block = {
                settingsRepository.saveMadhab(screenState.value.selectedMadhab.toDomain())
            },
            onSuccess = {
                sendEffect(MadhabEffect.NavigateToCalculationMethod)
            },
            onError = {}
        )
    }


}