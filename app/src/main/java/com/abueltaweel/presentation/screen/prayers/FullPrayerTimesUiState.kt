package com.abueltaweel.presentation.screen.prayers

import com.abueltaweel.domain.entity.prayer.Prayer
import com.abueltaweel.presentation.utils.FormattedTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@ExperimentalTime
data class FullPrayerTimesUiState(
    val time: TimeUiState = TimeUiState(),
    val prayers: List<PrayerUiState> = emptyList(),
    val nextPrayer: PrayerUiState = PrayerUiState(),
    val prayerNotifications: List<PrayerNotificationUiState> = emptyList()
) {
    data class PrayerUiState(
        val name: Prayer.PrayerName = Prayer.PrayerName.FAJR,  // ⚠️ غيّرها من Int إلى Enum
        val time: FormattedTime = FormattedTime(time = "00:00", isAm = false),
        val isUpComing: Boolean = false,
        val progress: Float = 100.0f,
        val icon: Int = 0,
        val instantTime: Instant? = null,
        val isNotificationEnabled: Boolean = false
    )
    
    data class TimeUiState(
        val hours: String = "00",
        val minutes: String = "00",
        val seconds: String = "00",
    )
    
    data class PrayerNotificationUiState(
        val name: Prayer.PrayerName = Prayer.PrayerName.FAJR,  // ⚠️ غيّرها من Int إلى Enum
        val isEnabled: Boolean = true
    )
}
