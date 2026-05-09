package com.abueltaweel.presentation.screen.settings

import SettingsUiState
import com.abueltaweel.domain.entity.prayer.Prayer

interface SettingsInteractionListener {
    fun onItemClick(action: SettingsUiState.SettingsAction)
    fun onDialogConfirm(index: Int)
    fun onDialogDismiss()
    fun onLocationClick()
    fun onCalculationMethodClick()
    fun onHelpFeedbackClick()
    fun onRateAppClick()
    fun onAboutClick()
    fun onPrayerMoazenClick(prayer: Prayer.PrayerName)
}
