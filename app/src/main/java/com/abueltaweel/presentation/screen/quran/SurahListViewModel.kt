package com.abueltaweel.presentation.screen.quran

import android.annotation.SuppressLint
import com.abueltaweel.domain.repository.quran.QuranRepository
import com.abueltaweel.presentation.base.BaseViewModel
import kotlinx.coroutines.delay

class SurahListViewModel(
    private val quranRepository: QuranRepository
) : BaseViewModel<SurahListUiState, SurahListEffect>(
    SurahListUiState()
), SurahListInteractionListener {

    init {
        loadSurahs()
    }

    @SuppressLint("SuspiciousIndentation")
    private fun loadSurahs() {
        tryToCall(
            onStart = { updateState { it.copy(isLoading = true) } },
            block = {
                val tafseer = quranRepository.getAyahTafseer(114, 1)
                println("TAfseer : ${tafseer}")
                quranRepository.getSurahs().map { it.toUiState() }
            },
            onSuccess = { surahs ->
                updateState { it.copy(surahList = surahs) }
                delay(100)
                updateState { it.copy(isLoading = false) }
            },
            onError = {}
        )
    }
    fun onScreenOpened() {
    }
    override fun onSurahClick(surahId: Int, arabicName: String, englishName: String) {
        sendEffect(SurahListEffect.NavigateToSurahAyat(surahId, arabicName,englishName))
    }

    override fun onSearchClick() {
        sendEffect(SurahListEffect.NavigateToQuranSearch)
    }

    override fun onBookmarksClick() {
        sendEffect(SurahListEffect.NavigateToBookmarksList)
    }
}