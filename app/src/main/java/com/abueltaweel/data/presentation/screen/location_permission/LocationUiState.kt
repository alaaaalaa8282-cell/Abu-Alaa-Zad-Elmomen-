package com.abueltaweel.presentation.screen.location_permission

import com.abueltaweel.R

data class LocationUiState(
    val isLoading: Boolean = false,
    val isButtonEnabled: Boolean = true,
    val isLocationPermissionGranted: Boolean = false,
    val isSuccessToast : Boolean = false,
    val buttonState: LocationButtonState = LocationButtonState.REQUEST_PERMISSION
){
    enum class LocationButtonState(val value: Int) {
        REQUEST_PERMISSION((R.string.allow_location_access)),
        LOADING((R.string.loading)),
        NEXT((R.string.next))
    }
}
