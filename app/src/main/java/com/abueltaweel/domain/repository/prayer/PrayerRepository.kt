package com.abueltaweel.domain.repository.prayer

import com.abueltaweel.domain.entity.prayer.CalculationMethod
import com.abueltaweel.domain.entity.location.Location
import com.abueltaweel.domain.entity.prayer.Madhab
import com.abueltaweel.domain.entity.prayer.Prayer
import kotlinx.datetime.LocalDate
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

interface PrayerRepository {
     fun getDailyPrayers(
        madhab: Madhab,
        calculationMethod: CalculationMethod,
        location: Location,
        date: LocalDate
    ): List<Prayer>

    @OptIn(ExperimentalTime::class)
    fun getNextPrayer(
        instant: Instant,
        madhab: Madhab,
        calculationMethod: CalculationMethod,
        location: Location,
        date: LocalDate
    ): Prayer

}