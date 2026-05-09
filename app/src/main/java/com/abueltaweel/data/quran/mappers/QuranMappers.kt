package com.abueltaweel.data.quran.mappers

import com.abueltaweel.data.quran.local.dto.AyahDto
import com.abueltaweel.domain.entity.quran.Ayah

fun AyahDto.toDomain(surahNumber: Int,surahNameArabic:String="",surahNameEnglish:String=""): Ayah {
    return Ayah(
        id = this.id,
        ayahNumber = this.ayahNumber,
        juzNumber = this.juzNumber,
        surahNumber = surahNumber,
        text = this.text,
        surahNameArabic = surahNameArabic,
        surahNameEnglish = surahNameEnglish
    )
}