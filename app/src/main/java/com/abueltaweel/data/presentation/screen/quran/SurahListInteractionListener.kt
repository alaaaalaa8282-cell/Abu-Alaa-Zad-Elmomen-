package com.abueltaweel.presentation.screen.quran

interface SurahListInteractionListener {
    fun onSurahClick(surahId: Int, arabicName: String, englishName:String)
    fun onSearchClick()
    fun onBookmarksClick()
}