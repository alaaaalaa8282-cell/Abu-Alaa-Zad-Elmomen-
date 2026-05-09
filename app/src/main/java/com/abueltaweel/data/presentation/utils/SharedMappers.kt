package com.abueltaweel.presentation.utils

import com.abueltaweel.R
import com.abueltaweel.domain.entity.prayer.Prayer

fun Prayer.toUiIcon(prayerName: Prayer.PrayerName):Int{
    return when(prayerName){
        Prayer.PrayerName.FAJR -> R.drawable.fajr
        Prayer.PrayerName.ZUHR ->  R.drawable.duhr
        Prayer.PrayerName.ASR ->  R.drawable.asr
        Prayer.PrayerName.MAGHRIB ->  R.drawable.shalat_maghrib
        Prayer.PrayerName.ISHA ->  R.drawable.shalat_isya
    }
}
fun Prayer.toUiName(prayerName: Prayer.PrayerName):Int{
    return when(prayerName){
        Prayer.PrayerName.FAJR -> R.string.fajr
        Prayer.PrayerName.ZUHR ->  R.string.dhuhr
        Prayer.PrayerName.ASR ->  R.string.asr
        Prayer.PrayerName.MAGHRIB ->  R.string.maghrib
        Prayer.PrayerName.ISHA ->  R.string.isha
    }
}