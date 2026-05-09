package com.abueltaweel.presentation.screen.settings

import com.abueltaweel.R

 fun getMoazenName(moazen: SettingsUiState.Moazen): Int {
    return when(moazen) {
        SettingsUiState.Moazen.AZAN_ABED_ALBASET -> R.string.moazen_abed_albaset
        SettingsUiState.Moazen.AZAN_MAKKAH -> R.string.Moazen_makkah
        SettingsUiState.Moazen.AZAN_MANSOOR_AL_ZAHRANI -> R.string.moazen_mansoor_alzahrani
        SettingsUiState.Moazen.AZAN_MISHARY_ALAFASI -> R.string.moazen_mishary_alafasi
        SettingsUiState.Moazen.AZAN_MOHAMMED_ALMENSHWY -> R.string.moazen_mohammed_almenshawy
        SettingsUiState.Moazen.AZAN_NASSER_ALQATAMI -> R.string.moazen_nasser_alqatami
        SettingsUiState.Moazen.AZAN_SUHAIB_KHATBA -> R.string.moazen_suhaib_khatba
    }
}
