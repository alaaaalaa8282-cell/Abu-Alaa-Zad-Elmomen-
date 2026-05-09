package com.abueltaweel.domain.repository.prayer


import com.abueltaweel.domain.entity.prayer.PrayerAlarm
import com.abueltaweel.domain.model.RescheduleResult

interface PrayerAlarmRepository {
    fun reschedule(prayers: List<PrayerAlarm>) : RescheduleResult
}