package com.abueltaweel.domain.repository.quran

import com.abueltaweel.domain.entity.quran.Ayah
import com.abueltaweel.domain.entity.quran.Surah

interface QuranRepository {
    suspend fun getSurahs(): List<Surah>
    suspend fun getAyahs(surahNumber: Int): List<Ayah>
    suspend fun searchInQuran(query: String): List<Ayah>
    suspend fun searchInSurah(surahNumber: Int, query: String): List<Ayah>
    suspend fun getAyahTafseer(surahNumber: Int, ayahNumber: Int): String
}