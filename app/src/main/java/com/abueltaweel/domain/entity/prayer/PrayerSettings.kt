package com.abueltaweel.domain.entity.prayer

import com.abueltaweel.domain.entity.location.Location

data class PrayerSettings(
    val madhab: Madhab,
    val calculationMethod: CalculationMethod,
    val location: Location
)