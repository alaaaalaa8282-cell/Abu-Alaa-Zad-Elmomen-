package com.abueltaweel.presentation.screen.madhab

import com.abueltaweel.R

data class MadhabUiState(
    val selectedMadhab : MadhabState = MadhabState.SHAFI,
){
    enum class MadhabState(val value: Int) {
        SHAFI(value =R.string.shafi),
        HANAFI(value =R.string.hanafi)
    }
}
