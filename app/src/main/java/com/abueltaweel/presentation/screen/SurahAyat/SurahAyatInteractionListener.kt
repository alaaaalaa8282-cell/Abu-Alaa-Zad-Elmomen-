package com.abueltaweel.presentation.screen.SurahAyat

interface SurahAyatInteractionListener {
    fun onAyaLongPressed(id: Int, text: String)
    fun onClearSelection()
    fun onCopyAya()
    fun onBookmarkAya()
    fun onTafseer()
    fun onClickBack()
    fun onClickSearch()
}