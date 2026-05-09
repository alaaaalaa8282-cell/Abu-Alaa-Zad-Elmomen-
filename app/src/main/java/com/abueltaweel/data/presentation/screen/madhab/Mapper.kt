package com.abueltaweel.presentation.screen.madhab

import com.abueltaweel.domain.entity.prayer.Madhab

fun MadhabUiState.MadhabState.toDomain():
       Madhab {
    return when (this) {
        MadhabUiState.MadhabState.SHAFI -> Madhab.SHAFI
        MadhabUiState.MadhabState.HANAFI -> Madhab.HANAFI
    }
}