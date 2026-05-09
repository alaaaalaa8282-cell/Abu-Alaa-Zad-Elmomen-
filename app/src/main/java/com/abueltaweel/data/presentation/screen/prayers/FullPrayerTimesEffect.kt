package com.abueltaweel.presentation.screen.prayers

interface FullPrayerTimesEffect {
    object NavigateBack : FullPrayerTimesEffect
    object RequestExactAlarm : FullPrayerTimesEffect
    object RequestIgnoreBatteryOptimization : FullPrayerTimesEffect
    object RequestNotificationPermission : FullPrayerTimesEffect
    object ShowBatteryOptimizationDialog : FullPrayerTimesEffect
    object RequestXiaomiAutoStart : FullPrayerTimesEffect
}