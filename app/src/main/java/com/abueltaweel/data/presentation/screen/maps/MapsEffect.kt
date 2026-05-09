package com.abueltaweel.presentation.screen.maps

import com.abueltaweel.design_system.component.ToastDetails
import com.abueltaweel.presentation.screen.location_permission.LocationEffect

sealed interface MapsEffect {
    object NavigateToBack : MapsEffect
    data class MoveCamera(val lat: Double, val lng: Double) : MapsEffect
    data class ShowToast(
        val toast: ToastDetails
    ) : MapsEffect
}