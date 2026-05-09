package com.abueltaweel.presentation.screen.location_permission

import com.abueltaweel.design_system.component.ToastDetails

sealed interface LocationEffect {
    object RequestLocationPermission : LocationEffect
    object RequestEnableGps : LocationEffect
    object NavigateToHome : LocationEffect
    data class ShowToast(
        val toast: ToastDetails
    ) : LocationEffect
}