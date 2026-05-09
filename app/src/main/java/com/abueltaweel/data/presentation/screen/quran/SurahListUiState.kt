package com.abueltaweel.presentation.screen.quran

import com.abueltaweel.R

data class SurahListUiState(
    val surahList: List<SurahUiState> = emptyList(),
    val isLoading: Boolean = true
)
data class SurahUiState(
    val id:Int,
    val name: String,
    val nameArabic: String,
    val nameEnglish: String,
    val ayahNumbers: Int,
    val surahType: SurahType,
    val surahImage: Int
)
enum class SurahType(val text:Int) {
    MAKKI(R.string.mekki),
    MADANI(R.string.madani)
}