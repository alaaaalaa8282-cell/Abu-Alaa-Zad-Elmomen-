package com.abueltaweel.presentation.screen.settings

import com.abueltaweel.design_system.component.ToastDetails

sealed interface SettingsEffect {
    object NavigateToLocation    : SettingsEffect
    data class ShowToast(val toast: ToastDetails) : SettingsEffect
}
