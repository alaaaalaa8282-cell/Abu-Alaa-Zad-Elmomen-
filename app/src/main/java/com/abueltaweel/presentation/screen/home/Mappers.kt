package com.abueltaweel.presentation.screen.home

import com.abueltaweel.domain.entity.prayer.Prayer
import com.abueltaweel.presentation.utils.format
import com.abueltaweel.presentation.utils.toUiIcon
import com.abueltaweel.presentation.utils.toUiName
import kotlinx.datetime.TimeZone
import kotlin.time.ExperimentalTime


@OptIn(ExperimentalTime::class)
fun Prayer.toPrayerUiState(zone: TimeZone): HomeUiState.PrayerUiState{
    val formatted = format(instant = this.time, zone = zone)
    return HomeUiState.PrayerUiState(
        name = this.toUiName(this.name),
        time = formatted.time,
        isAm = formatted.isAm,
        icon = this.toUiIcon(this.name)
    )
}

