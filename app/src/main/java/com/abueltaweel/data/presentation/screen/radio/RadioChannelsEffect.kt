package com.abueltaweel.presentation.screen.radio

import com.abueltaweel.design_system.component.ToastDetails
import com.abueltaweel.presentation.screen.location_permission.LocationEffect

sealed interface  RadioChannelsEffect {
    data class ShowToast(
        val toast: ToastDetails
    ) : RadioChannelsEffect
    data class PlaySound(val url:String,val titleText:String) : RadioChannelsEffect
   object PauseSound : RadioChannelsEffect
}