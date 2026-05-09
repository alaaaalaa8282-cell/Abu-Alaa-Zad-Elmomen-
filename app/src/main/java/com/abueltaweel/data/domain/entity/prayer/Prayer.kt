package com.abueltaweel.domain.entity.prayer

import kotlin.time.ExperimentalTime
import kotlin.time.Instant


@OptIn(ExperimentalTime::class)
data class Prayer (
    val name: PrayerName,
    val time: Instant,
) {
    enum class PrayerName {
        FAJR,
        ZUHR,
        ASR,
        MAGHRIB,
        ISHA
    }
}

