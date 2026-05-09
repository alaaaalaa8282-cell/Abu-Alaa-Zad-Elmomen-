package com.abueltaweel.presentation.screen.prayers

import com.abueltaweel.domain.entity.prayer.Prayer

interface FullPrayerTimeInteractionListener {
    fun onClickBack()
    fun onClickEnablePrayer(
        prayerName: Prayer.PrayerName,
        isEnabled: Boolean
    )
}