package com.abueltaweel.presentation.screen.prayers

import com.abueltaweel.domain.entity.prayer.Prayer
import com.abueltaweel.presentation.utils.format
import kotlinx.datetime.TimeZone
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun Prayer.toPrayerUiState(zone: TimeZone): FullPrayerTimesUiState.PrayerUiState {
    return FullPrayerTimesUiState.PrayerUiState(
        name = this.name,  // استخدام الـ Enum مباشرة
        time = format(instant = this.time, zone = zone),
        icon = this.toUiIcon(this.name),
        instantTime = this.time
    )
}
