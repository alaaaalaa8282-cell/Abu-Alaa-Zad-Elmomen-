package com.abueltaweel.data.prayer.mapper

import com.abueltaweel.domain.entity.prayer.Madhab

fun Madhab.toAdhanMadhab(): com.batoulapps.adhan2.Madhab {
    return when (this) {
        Madhab.HANAFI -> com.batoulapps.adhan2.Madhab.HANAFI
        Madhab.SHAFI -> com.batoulapps.adhan2.Madhab.SHAFI
    }
}